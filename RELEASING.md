# Releasing easydoc 3.0.x

发版遵循 1.0.x 时代确立的惯例：**发布分支名 = 版本号 = `X.Y.Z.RELEASE`**（如 `1.0.1.RELEASE`）。
3.0 线的首发版本为 `3.0.0.RELEASE`。

## 前置条件

| 依赖 | 要求 |
|---|---|
| JDK | 21+ |
| Maven | 4.0.0-rc-6+（`requireMavenVersion` 强制） |
| GPG | 本地已有签名密钥并发布到公钥服务器（release profile 的 `maven-gpg-plugin` 需要） |
| 凭据 | `packages.aliyun.com`（distributionManagement）或 Maven Central（central profile）的部署凭据 |

## 步骤

```bash
# 1. 从 feature/3.0.x 切发布分支
git checkout feature/3.0.x && git pull
git checkout -b 3.0.0.RELEASE

# 2. 版本号定版（去掉 SNAPSHOT 后缀，与分支同名）
mvn versions:set -DnewVersion=3.0.0.RELEASE -DprocessAllModules=true
mvn versions:commit   # 或人工检查 pom 后提交

# 3. 全量验证（Maven 4，无 skip —— enforcer + 90% 覆盖率门禁生效）
mvn clean verify

# 4. 发布到 Aliyun 私仓（release profile：源码包/javadoc/GPG 签名）
mvn -Prelease clean deploy -DskipTests=false

#    或发布到 Maven Central（central profile：central-publishing-maven-plugin）
mvn -Pcentral clean deploy

# 5. 提交发布分支并打 tag，合并回 feature/3.0.x 与 main
git add -A && git commit -m "3.0.0.RELEASE"
git push origin 3.0.0.RELEASE
git checkout feature/3.0.x && git merge --ff-only 3.0.0.RELEASE && git push
git checkout main && git merge --ff-only feature/3.0.x && git push

# 6. 回到开发线：bump 下一个快照版本
git checkout feature/3.0.x
mvn versions:set -DnewVersion=3.0.x.<yyyymmdd>-SNAPSHOT -DprocessAllModules=true
```

## Checklist

- [ ] `mvn clean verify`（Maven 4，无 skip）13 模块全绿
- [ ] CHANGELOG.md 已更新（Unreleased → 版本号 + 日期）
- [ ] easydoc-bom 包含全部 11 个模块且版本一致
- [ ] GPG 签名在本地可执行（`gpg --list-secret-keys`）
- [ ] README Requirements 与实际依赖矩阵一致
