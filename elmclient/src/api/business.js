import http from '@/utils/request'

export const businessApi = {
    // 获取商家详情
    getBusinessById(businessId) {
        return http.post('/api/business/getBusinessById', {
            businessId: businessId
        })
    },
    
    // 获取商家的食品列表
    listFoodByBusinessId(businessId) {
        return http.post('/api/food/listFoodByBusinessId', {
            businessId: businessId
        })
    },

    // 根据订单类型获取商家列表
    listBusinessByOrderTypeId(orderTypeId) {
        return http.post('/api/business/listBusinessByOrderTypeId', {
            orderTypeId: orderTypeId
        })
    },

    // 获取食品详情
    getFoodById(foodId) {
        return http.post('/api/food/getFoodById', {
            foodId: foodId
        })
    }
}

export default businessApi 