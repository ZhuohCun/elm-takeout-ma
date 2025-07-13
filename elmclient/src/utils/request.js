import axios from 'axios'
import router from '@/router'
import qs from 'qs'

// 创建axios实例
const service = axios.create({
    baseURL: 'http://localhost:9000',
    timeout: 30000, // 增加超时时间
    withCredentials: false, // 禁用携带cookies，避免跨域问题
    headers: {
        'Content-Type': 'application/json',
    }
})

// request拦截器
service.interceptors.request.use(
    config => {
        // 获取token
        const token = localStorage.getItem('token')
        
        // 除了登录和注册请求外，其他请求都需要token
        if (!config.url.includes('login') && !config.url.includes('register') && 
            !config.url.includes('getUserInfo') && !config.url.includes('getUserById')) {
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`
            } else {
                router.push('/login')
                return Promise.reject('No token')
            }
        }

        // POST请求数据处理
        if (config.method === 'post') {
            // 这些API使用JSON格式
            if (config.url.includes('login') || config.url.includes('register') || 
                config.url.includes('getFoodById') || config.url.includes('getBusinessById') ||
                config.url.includes('listCartByBusinessId') || config.url.includes('listAllCartByBusinessId') || 
                config.url.includes('createOrders') || config.url.includes('updateOrderState')) {
                config.headers['Content-Type'] = 'application/json'
            } else {
                // 如果数据已经是 URLSearchParams，不需要再次处理
                if (!(config.data instanceof URLSearchParams)) {
                    // 将对象转换为URLSearchParams
                    const formData = new URLSearchParams()
                    for (const key in config.data) {
                        formData.append(key, config.data[key])
                    }
                    config.data = formData
                }
                config.headers['Content-Type'] = 'application/x-www-form-urlencoded'
            }
        }

        console.log('发送请求:', config.url, config.data)
        return config
    },
    error => {
        console.log('请求错误:', error)
        return Promise.reject(error)
    }
)

// response拦截器
service.interceptors.response.use(
    response => {
        console.log('请求成功:', response.config.url, response.data)
        return response.data
    },
    error => {
        if (error.message === 'No token') {
            return Promise.reject(error)
        }

        console.log('err:', error)
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 清除token和用户信息
                    localStorage.removeItem('token')
                    sessionStorage.removeItem('user')
                    // 非登录请求才跳转
                    if (!error.config.url.includes('login')) {
                        router.push('/login')
                        alert('请重新登录')
                    } else {
                        alert('用户名或密码错误')
                    }
                    break
                case 403:
                    alert('没有权限')
                    break
                case 404:
                    alert('请求的资源不存在')
                    break
                case 405:
                    console.error('请求方法不允许:', error.config.url)
                    break
                case 415:
                    console.error('不支持的媒体类型:', error.config.url)
                    break
                case 503:
                    alert('服务暂时不可用，请稍后重试')
                    break
                default:
                    alert('系统错误，请稍后重试')
            }
        } else {
            alert('网络错误，请确保服务已启动')
        }
        return Promise.reject(error)
    }
)

// 请求方法封装
const http = {
    get(url, params) {
        return service.get(url, { params })
    },

    post(url, data) {
        return service.post(url, data)
    },

    put(url, data) {
        return service.put(url, data)
    },

    delete(url, params) {
        return service.delete(url, { params })
    }
}

export default http
