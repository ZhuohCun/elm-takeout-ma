import http from '@/utils/request'

export const cartApi = {
    listCart(userId) {
        return http.post('/api/cart/listCart', {
            userId: userId
        });
    },

    // 根据用户ID和商家ID查询购物车
    listCartByBusinessId(userId, businessId) {
        return http.post('/api/cart/listCartByBusinessId', {
            userId: userId,
            businessId: businessId
        });
    },

    // 根据用户ID和商家ID查询所有购物车项（包括已加入订单的项）
    listAllCartByBusinessId(userId, businessId) {
        return http.post('/api/cart/listAllCartByBusinessId', {
            userId: userId,
            businessId: businessId
        });
    },

    // 保存购物车
    saveCart(params) {
        const data = new URLSearchParams();
        for (let key in params) {
            data.append(key, params[key]);
        }
        return http.post('/api/cart/saveCart', data);
    },

    // 更新购物车
    updateCart(params) {
        const data = new URLSearchParams();
        for (let key in params) {
            data.append(key, params[key]);
        }
        return http.post('/api/cart/updateCart', data);
    },

    // 删除购物车项
    removeCart(params) {
        const data = new URLSearchParams();
        for (let key in params) {
            data.append(key, params[key]);
        }
        return http.post('/api/cart/removeCart', data);
    }
}

export default cartApi;
