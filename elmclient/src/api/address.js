import request from '@/utils/request'

export const addressApi = {
    // 获取用户的配送地址列表
    listDeliveryAddressByUserId(userId) {
        return request.get(`/api/address/user/${userId}`)
    },

    // 根据ID获取配送地址
    getDeliveryAddressById(daId) {
        return request.get(`/api/address/${daId}`)
    },

    // 更新配送地址
    updateDeliveryAddress(addressData) {
        return request.put('/api/address/', addressData)
    },

    // 删除配送地址
    removeDeliveryAddress(daId) {
        return request.delete(`/api/address/${daId}`)
    },

    // 保存配送地址
    saveDeliveryAddress(addressData) {
        return request.post('/api/address/', addressData)
    }
}

export default addressApi 