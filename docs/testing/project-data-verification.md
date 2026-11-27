# 项目数据层 MySQL 验收

本轮目标是验证 `docs/project-v0.sql` 与已合并的 Project、Mapper、Service 配合工作。基于 main 的 `f4732a3`：这些实现已随 #18 合并，因此本 PR 保留现有 Service 接口及实现，不再提交另一套同名类。

## 两种测试方式

在 IDEA 的 Maven 面板运行 Lifecycle → test，或在 backend 目录执行 `./mvnw test`。未配置下述环境变量时，运行原有上下文测试，跳过 8 个真实 MySQL 用例；这不代表数据库验证通过。

真实数据库验收使用 `ProjectDataMySqlTests`。它没有模拟 Mapper、Service 或数据库；HTTP 部分通过 MockMvc 调用真实 Controller 和 Session 拦截器。

### 本地 MySQL 验收

1. 准备 MySQL 8.0，启用严格模式（包含 `STRICT_TRANS_TABLES` 或 `STRICT_ALL_TABLES`）。
2. 使用自己的测试账号创建一个**新的空库**，库名以 `codeleap_project_test_` 开头。例如：

```sql
CREATE DATABASE codeleap_project_test_local_01 CHARACTER SET utf8mb4;
```

3. 在 IDEA 的 Maven Runner 环境变量中设置以下三项；账号需要能在该测试库建表、读写。不要写入 application.yml 或提交真实密码。

```text
PROJECT_MYSQL_TEST_URL=jdbc:mysql://127.0.0.1:3306/codeleap_project_test_local_01?serverTimezone=UTC&characterEncoding=utf8
PROJECT_MYSQL_TEST_USERNAME=你的本机测试账号
PROJECT_MYSQL_TEST_PASSWORD=该测试账号的密码
```

4. 在 Maven 面板运行 **test**。Git Bash 中也可在设置相同环境变量后，于 backend 目录运行 `./mvnw test`。

预期汇总：`Tests run: 9, Failures: 0, Errors: 0, Skipped: 0`。

测试会执行仓库中的真实建表脚本。建表前检查当前库名前缀和空库状态；不符合就失败，避免在已有业务库运行 DDL。每个用例的测试数据由事务回滚，空表保留以便检查。再次运行时请新建另一个空库，而不是复用已有 project 表的库。测试不会删除数据库或修改已有 MySQL 账号。

仅希望建业务表的成员，可在确认选中了正确数据库后，单独执行 `docs/project-v0.sql`。该脚本不包含 DROP、账号、密码或固定数据库名；已有同名表时会报错，不能用重复执行脚本替代迁移。

### GitHub Actions

backend job 使用独立的 `mysql:8.0` service，每次工作流都创建空的 `codeleap_project_test_ci`。健康检查通过后，将随机映射的端口写入测试 URL，再运行完整 Maven test。

CI MySQL 使用仅供临时容器的空密码配置，不依赖团队数据库或仓库凭据。工作流结束后由 Actions 回收容器；普通本地 test 仍不要求安装 Docker 或连接 MySQL。

## 验收覆盖

| 用例 | 证明的行为 |
|---|---|
| schemaMatchesFiveFieldsAndOwnerTimeIndex | 只有约定五列；类型、长度、非空、自增主键、联合索引、InnoDB 和 utf8mb4 正确 |
| saveFillsGeneratedIdAndMapsOwnerAndTimeToDatabaseColumns | Service 保存成功、回填 id；ownerId/createTime 正确映射；空描述及中文、emoji 可保存 |
| listFiltersOwnerAndOrdersByCreationTimeDescending | A 两条、B 一条，A 查询只返回自身记录且最新在前；无项目用户返回空列表 |
| databaseRejectsMissingAndNullRequiredValues | name、owner_id、create_time 缺失或显式 NULL 均被拒绝，检查 MySQL 错误码 1364/1048 |
| databaseAcceptsLengthLimitsAndRejectsOverflow | 100/500 字符可保存，101/501 字符拒绝，检查 MySQL 错误码 1406 |
| httpCreationUsesSessionOwnerAndDoesNotExposeOwnerId | 创建时 trim 名称；忽略伪造 id/ownerId/createTime；A/B Session 写入归属正确，列表不泄露其他用户记录或内部 ownerId |
| unauthenticatedRequestsAreRejectedWithoutWritingRows | 无 Session 的 GET/POST 返回 HTTP 401、业务码 4000，数据库无新增 |
| invalidRequestsDoNotWriteRows | 缺失名称、空名称、全空格、超长名称/描述返回业务码 5002，无新增 |

## 本次实际执行记录

- 日期：2026-10-30。
- 环境：Windows、Java 17.0.18、Spring Boot 3.4.3、MySQL 8.0.46。
- 本地命令：在 backend 目录，通过 IDEA 内置 Maven 3.9.11 执行 `mvn.cmd -s <本机代理配置> -B test`；本机代理文件未提交。CI 使用仓库 Maven Wrapper 执行 `./mvnw test`。
- 使用本机已有 MySQL 可执行文件，在独立临时数据目录启动 127.0.0.1:13316，未改动原有 MySQL80 服务、账号及业务数据库。
- 先运行最新 main 的原有 Maven test：1 项通过。
- 开启 MySQL 环境变量后运行完整 Maven test：9 项通过，0 失败、0 错误、0 跳过。
- 本轮执行与验证由 Codex 代做，不能作为作者已独立掌握 Java/SQL 的证据。

范围：本记录验证数据层与后端 HTTP Session 链路；没有代替团队的浏览器登录、前端交互及完整联调验收，也不涵盖成员、编辑、删除功能。

审查时发现一处已合并接口的边界差异：DTO 先对原始 name 执行 100 字符校验，Service 再 trim，因此“原始输入超过 100、trim 后不超过 100”的名称仍会被拒绝。本 PR 没有修改接口作者的 DTO；该边界需要接口负责人后续确认。时间完全相同的记录也未约定额外排序键，本次只验证不同创建时间的倒序。
