# Releasing easydoc

easydoc 使用**日期戳发布模型**：版本号格式为 `X.x.YYYYMMDD`（如 `3.0.x.20260831`），
SNAPSHOT 版本为 `X.x.YYYYMMDD-SNAPSHOT`（或 `X.x.YYYYMM(DD)-SNAPSHOT` 表示下一周期）。

> **历史参考**：1.0.x 时代使用 GA 模型（`X.Y.Z.RELEASE`，如 `1.0.1.RELEASE`），
> 3.0.x 的首发版本 `3.0.0.RELEASE` 也沿用此惯例。此后切换为日期戳模型。

## 前置条件

| 依赖 | 要求 |
|---|---|
| JDK | 21+（3.0.x）；17（2.0.x）；8（1.0.x） |
| Maven | 4.0.0-rc-6+（3.0.x，`requireMavenVersion` 强制）；3.9.x（1.0/2.0） |
| GPG | 本地已有签名密钥并发布到公钥服务器（release/central profile 的 `maven-gpg-plugin` 需要） |
| 凭据 | `packages.aliyun.com`（distributionManagement）或 Maven Central（central profile）的部署凭据 |

## 发布流程（3.0.x 日期戳模型）

```bash
# 1. 从 feature/3.0.x 切发布分支（可选，也可直接在 feature/3.0.x 上发布）
git checkout feature/3.0.x && git pull

# 2. 版本号定版：去掉 SNAPSHOT 后缀
#    根 pom <revision> 改为 3.0.x.YYYYMMDD（无 -SNAPSHOT）
#    或使用 mvn versions:set（CI-friendly 版本需用 flatten-maven-plugin 或手工编辑）

# 3. 全量验证（Maven 4，无 skip —— enforcer + JaCoCo 覆盖率门禁生效）
mvn clean verify

# 4. 覆盖率棘轮检查（可选，确认各模块覆盖率达标）
./scripts/coverage-ratchet-check.sh

# 5. 发布
#    方式 A：Aliyun 私仓（release profile）
mvn -Prelease clean deploy -DskipTests=false

#    方式 B：Maven Central（central profile）
mvn -Pcentral clean deploy

#    方式 C：Maven 4 中央发布（需手工修包，见 docs/release-central.md）
#    deploy 会 FAILED（已知缺陷），用 scripts/central-bundle-fix.sh 修复后 API 上传：
./scripts/central-bundle-fix.sh target/central-publishing/central-bundle.zip 3.0.x.YYYYMMDD /tmp/central-bundle-fixed.zip

# 6. 确认 Portal 状态 PUBLISHED 后：打 tag、升下一个快照版本
git tag X.x.YYYYMMDD
git push origin X.x.YYYYMMDD

# 7. 回到开发线：bump 下一个快照版本
git checkout feature/3.0.x
# 根 pom <revision> 改为 3.0.x.YYYYMM(DD)-SNAPSHOT
```

## Checklist

- [ ] `mvn clean verify`（Maven 4，无 skip）全模块绿
- [ ] CHANGELOG.md 已更新（Unreleased → 版本号 + 日期）
- [ ] easydoc-bom 包含全部模块且版本一致
- [ ] GPG 签名在本地可执行（`gpg --list-secret-keys`）
- [ ] README Requirements 与实际依赖矩阵一致
- [ ] 覆盖率棘轮检查通过（`./scripts/coverage-ratchet-check.sh`）

## 历史参考：GA 发布模型（1.0.x / 3.0.0.RELEASE）

1.0.x 和 3.0.x 的首发版本使用 GA 模型（`X.Y.Z.RELEASE`）：

```bash
# 切发布分支
git checkout -b 3.0.0.RELEASE

# 版本号定版
mvn versions:set -DnewVersion=3.0.0.RELEASE -DprocessAllModules=true

# 验证 + 发布 + tag + 合并回 feature/3.0.x 与 main
mvn clean verify
mvn -Prelease clean deploy -DskipTests=false
git tag v3.0.0.RELEASE
```

此后 3.0.x 切换为日期戳模型（`X.x.YYYYMMDD`）。
