import http from '@/utils/request'

export const orderApi = {
    // 创建订单
    createOrders(orderData) {
        return http.post('/api/orders/createOrders', orderData)
    },

    // 获取订单列表
    listOrdersByUserId(params) {
        const data = new URLSearchParams()
        data.append('userId', params.userId)
        return http.post('/api/orders/listOrdersByUserId', data)
    },

    // 获取订单详情
    getOrdersById(orderId) {
        const id = typeof orderId === 'object' ? orderId.orderId : orderId
        const data = new URLSearchParams()
        data.append('orderId', id)
        return http.post('/api/orders/getOrdersById', data)
    },

    // 获取订单明细
    getOrderDetailetByOrderId(orderId) {
        return http.post('/api/orders/getOrderDetailetByOrderId', {
            orderId
        })
    },

    // 更新订单状态
    updateOrderState(params) {
        return http.post('/api/orders/updateOrderState', params)
    }
}

export default orderApi 