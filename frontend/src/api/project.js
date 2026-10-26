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
