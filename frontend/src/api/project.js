import request from '../utils/request'

/**
 * 查询当前登录用户自己的项目列表
 * GET /api/v1/projects
 * 返回：项目数组，按 createTime 从新到旧；没有项目时是 []，不是 null
 */
export function getProjects() {
  return request.get('/api/v1/projects')
}

/**
 * 创建项目
 * POST /api/v1/projects
 * 请求体只允许 name、description；id、ownerId、createTime 一律由后端生成
 * @param {{ name: string, description?: string | null }} data
 */
export function createProject(data) {
  return request.post('/api/v1/projects', data)
}

/**
 * 查询单个项目详情（只能查自己的，别人的编号后端按“不存在”处理）
 * GET /api/v1/projects/{id}
 * 编号放路径，不放请求体；身份由后端从 Session 取，前端不传 ownerId
 * @param {number|string} id
 * @returns {Promise<{ id: number, name: string, description: string | null, createTime: string }>}
 */
export function getProject(id) {
  return request.get(`/api/v1/projects/${id}`)
}

/**
 * 编辑项目（PUT 是“覆盖这两个可编辑字段”，不是 PATCH）
 * PUT /api/v1/projects/{id}
 * data 只允许 name、description；id 来自路径，ownerId/createTime 不能通过请求体修改
 * 描述传空字符串表示清空，不要传 null 之外的额外字段
 * @param {number|string} id
 * @param {{ name: string, description: string }} data
 */
export function updateProject(id, data) {
  return request.put(`/api/v1/projects/${id}`, data)
}

/**
 * 查询项目成员（创建者和普通成员都能查，其他已登录用户会被后端按“不存在”拒绝）
 * GET /api/v1/projects/{projectId}/members
 * 返回：成员数组，创建者固定在第一项，其余按加入时间升序
 * 每项只有三个字段：{ userId, username, role }，role 是 'OWNER' 或 'MEMBER'
 * 没有普通成员时也会返回创建者那一项，不会是空数组
 * @param {number|string} projectId
 * @returns {Promise<Array<{ userId: number, username: string, role: 'OWNER'|'MEMBER' }>>}
 */
export function getProjectMembers(projectId) {
  return request.get(`/api/v1/projects/${projectId}/members`)
}

/**
 * 按用户名把已注册用户加进项目（只有创建者能调用）
 * POST /api/v1/projects/{projectId}/members
 * data 只允许 { username }：项目编号在路径里，操作者身份由后端从 Session 取，
 * userId / ownerId / role 一律不从请求体接收
 * 重复添加已有成员仍返回成功（返回现有成员），所以不要在前端自己判断“是不是新加的”
 * @param {number|string} projectId
 * @param {{ username: string }} data
 * @returns {Promise<{ userId: number, username: string, role: 'OWNER'|'MEMBER' }>}
 */
export function addProjectMember(projectId, data) {
  return request.post(`/api/v1/projects/${projectId}/members`, data)
}
