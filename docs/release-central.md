# Maven Central 发布手册（easydoc）

> 依据 3.0.x.20260630 与 3.0.x.20260830 两次发布的实操记录整理。

## 前置条件

- `~/.m2/settings.xml` 配置 `<id>central</id>` 服务器（Central Portal token 作为 username/password）
- GPG 私钥 AF1B6E00（hiwepy@gmail.com）在本机，且 gpg-agent 已缓存口令（非交互可签名）
- 三条版本线：1.0.x（JDK 8，Maven 3.9.x + `-Dmaven.compiler.release=8`）、2.0.x（JDK 17，Maven 3.9.x）、3.0.x（JDK 21，Maven 4.0.0-rc-6）

## 标准流程（每条版本线）

1. 根 pom `<revision>` 去掉 `-SNAPSHOT`，提交 `release: X.x.YYYYMMDD (remove SNAPSHOT)`
2. 全量测试已在发布前验证通过（`clean verify`，jacoco 0 违规）
3. 部署：`-Pcentral clean deploy -DskipTests`
   - **1.0/2.0（Maven 3）**：extension 钩子正常工作，聚合 bundle 自动上传并 autoPublish；
     构建可能在 `waitUntil=published` 阶段因长时间等待被 SSL 断开（BUILD FAILURE 但
     部署已成功上传）——此时不要重跑 deploy，改用 API 轮询 deployment 状态即可
   - **3.0（Maven 4）**：插件路径仍不可靠（见下节），用手工修包 + API 上传
4. 确认 Portal 状态 PUBLISHED 后：打 tag `X.x.YYYYMMDD`，根 pom 升下一个
   `X.x.YYYYMM(DD)-SNAPSHOT` 并提交，推送分支 + tag

## Maven 4（3.0.x）已知缺陷与手工修包流程

`central-publishing-maven-plugin 0.11.0` 在 Maven 4 下的 bundle 有两类污染：

1. 每个模块混入 `*-consumer.pom`（及其 .md5/.sha1/.sha256/.sha512/.asc 伴生文件，
   每模块 6 个）；`excludeArtifacts: *-consumer.pom*` 配置**不生效**（伴生校验和文件
   不被排除）
2. 主 `artifact-version.pom` 是**原始 pom**（`<version>${revision}</version>` 未解析），
   Portal 校验报 "The version contains invalid character(s)"，且因 parent 版本不可解析，
   继承的 description/URL/license/SCM/developers 全部被判缺失

手工修包（在 `mvn -P central clean deploy -DskipTests` 生成 bundle 后执行，deploy 本身
会因上述原因 FAILED，属预期）：

```bash
# 1. 解包 bundle，剔除 consumer.pom 系文件
rm -rf /tmp/cbf && mkdir /tmp/cbf && cd /tmp/cbf
unzip -q <repo>/target/central-publishing/central-bundle.zip
find . -name '*-consumer.pom*' -delete

# 2. 所有 pom 内 ${revision} 替换为发布版本，重算校验和，GPG 重签
V=3.0.x.YYYYMMDD
for pom in $(grep -rl '\${revision}' --include='*.pom' .); do
  sed -i '' "s|\\\${revision}|$V|g" "$pom"
  rm -f "$pom.md5" "$pom.sha1" "$pom.sha256" "$pom.sha512" "$pom.asc"
  md5 -q "$pom" | tr -d '\n' > "$pom.md5"
  shasum -a 1   "$pom" | awk '{printf $1}' > "$pom.sha1"
  shasum -a 256 "$pom" | awk '{printf $1}' > "$pom.sha256"
  shasum -a 512 "$pom" | awk '{printf $1}' > "$pom.sha512"
  # 注意：本机 gpg -b 默认输出 .sig，必须显式 --output 指定 .asc
  gpg --batch --yes --default-key AF1B6E00 --output "$pom.asc" -b "$pom"
done

# 3. 重打包并上传（AUTOMATIC 发布）
zip -q -r /tmp/central-bundle-fixed.zip .
TOKEN=$(printf '%s:%s' <portal-user> <portal-token> | base64)
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -F "bundle=@/tmp/central-bundle-fixed.zip;type=application/zip" \
  "https://central.sonatype.com/api/v1/publisher/upload?name=easydoc-$V&publishingType=AUTOMATIC"
# 返回 deploymentId，轮询：
curl -X POST -H "Authorization: Bearer $TOKEN" \
  "https://central.sonatype.com/api/v1/publisher/status?id=<deploymentId>"
# state 流转：VALIDATING → PUBLISHING → PUBLISHED（FAILED 则先 DROP：
# DELETE /api/v1/publisher/deployment/<id>）
```

保留原始 pom（modelVersion 4.1.0、注释齐全）替换 `${revision}` 后发布，与
3.0.x.20260630 的已发布构件形态一致。

## 长期修复方向（待办）

- 让 Maven 4 不部署原始 pom/consumer.pom 双份（关注 maven-deploy-plugin 对
  consumer pom 的处理配置或插件 issue），使 `-P central deploy` 真正一键可用
- 或将上述修包脚本固化为 `scripts/central-bundle-fix.sh`
