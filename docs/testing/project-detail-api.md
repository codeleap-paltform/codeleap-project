# 项目详情 / 编辑接口验证

## 环境
- 后端本地启动，base url: http://localhost:8080
- 登录方式：POST /api/v1/user/login 获取 Cookie（文档不记录真实 Cookie/密码）

## 1. A 查自己项目 + 编辑后复查
GET /api/v1/projects/{A的项目id}   （带 A 的 Cookie）
→ 200, code=0, data={id,name,description,createTime}

PUT /api/v1/projects/{A的项目id}
body: {"name":"新名字","description":"新描述"}
→ 200, code=0

GET /api/v1/projects/{A的项目id}
→ 200, data.name=新名字

## 2. B 用 A 的项目编号
GET /api/v1/projects/{A的项目id}   （带 B 的 Cookie）→ code=5004
PUT /api/v1/projects/{A的项目id}   （带 B 的 Cookie）→ code=5004
再以 A 身份 GET → 数据未变化

## 3. 不存在编号 / 他人编号
GET /api/v1/projects/999999 → code=5004
（他人编号见第2组，同样 5004）

## 4. 未登录
GET /api/v1/projects/1  （无 Cookie）→ HTTP 401, code=4000
PUT /api/v1/projects/1  （无 Cookie）→ HTTP 401, code=4000

## 5. 校验
PUT body {"name":"   ","description":"x"}        → 拒绝（VALIDATE_ERROR）
PUT body {"name":"<101字符>","description":"x"}   → 拒绝
PUT body {"name":"ok","description":"<501字符>"}  → 拒绝
PUT body {"name":"<100字符>","description":"<500字符>"} → 成功
PUT body {"name":"  合法名  ","description":null} → 成功，保存为 trim 后的"合法名"，描述被清空

## 6. 越权字段
PUT body {"name":"x","description":"y","ownerId":999,"id":123,"createTime":"2000-01-01T00:00:00"}
→ 成功，但归属仍是当前登录用户，id 与 createTime 不变

## id<=0
GET /api/v1/projects/0 → VALIDATE_ERROR
PUT /api/v1/projects/-1 → VALIDATE_ERROR