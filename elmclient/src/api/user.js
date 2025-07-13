import http from '@/utils/request'

export const userApi = {
    // 用户登录
    login(userId, password) {
        return http.post('/api/user/login', {
            userId,
            password
        })
    },

    // 用户注册
    register(userInfo) {
        return http.post('/api/user/register', userInfo)
    },

    // 检查用户ID是否存在
    checkUserId(userId) {
        return http.post('/api/user/getUserById', {
            userId
        })
    },

    // 获取用户信息 - 改为GET请求
    getUserInfo(userId) {
        return http.get('/api/user/getUserInfo', {
            userId
        })
    }
}

export default userApi
