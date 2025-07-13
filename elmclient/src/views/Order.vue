<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>确认订单</p>
    </header>
    <!-- 订单信息部分 -->
    <div class="order-info">
      <h5>订单配送至：</h5>
      <div class="order-info-address" @click="toUserAddress">
        <div class="address-content">
          <p v-if="deliveryaddress && deliveryaddress.address" class="address-text">
            {{deliveryaddress.address}}
          </p>
          <p v-else class="no-address">请选择送货地址</p>
          <p v-if="deliveryaddress && deliveryaddress.contactName" class="contact-info">
            {{deliveryaddress.contactName}}{{deliveryaddress.contactSex | sexFilter}} {{deliveryaddress.contactTel}}
          </p>
        </div>
        <i class="fa fa-angle-right"></i>
      </div>

    </div>
    <h3>{{business.businessName}}</h3>
    <!-- 订单明细部分 -->
    <div v-if="cartArr && cartArr.length > 0">
      <ul class="order-detailed">
        <li v-for="item in cartArr" :key="item.foodId">
          <div class="order-detailed-left">
            <img
              :src="item.food.foodImg"
              @error="handleImageError"
              :alt="item.food.foodName"
            >
            <div class="food-info">
              <p class="food-name">{{item.food.foodName}}</p>
              <p class="food-quantity">x {{item.quantity}}</p>
            </div>
          </div>
          <p class="food-price">&#165;{{(item.food.foodPrice * item.quantity).toFixed(2)}}</p>
        </li>
      </ul>
    </div>
    <div v-else class="empty-cart">
      <p>购物车为空</p>
    </div>
    <div class="order-deliveryfee">
      <p>配送费</p>
      <p>&#165;3</p>
    </div>
    <!-- 合计部分 -->
    <div class="total">
      <div class="total-left">
        &#165;{{totalPrice}}
      </div>
      <div class="total-right" @click="toPayment">
        去支付
      </div>
    </div>
  </div>
</template>
<script>
import { businessApi } from '@/api/business'
import { cartApi } from '@/api/cart'
import { orderApi } from '@/api/order'
import { addressApi } from '@/api/address'

export default {
  name: 'Orders',
  data() {
    return {
      businessId: this.$route.query.businessId,
      business: {},
      user: {},
      cartArr: [],
      deliveryaddress: null
    }
  },
  async created() {
    this.user = this.$getSessionStorage('user')
    console.log('当前用户信息:', this.user)
    console.log('获取到商家ID:', this.businessId)

    if (!this.user) {
      this.$router.push('/login')
      return
    }

    await this.loadAddress()
    await this.loadBusinessAndCart()
  },
  computed: {
    totalPrice() {
      try {
        const foodTotal = this.cartArr.reduce((sum, item) => {
          const price = parseFloat(item.food.foodPrice) || 0
          const quantity = parseInt(item.quantity) || 0
          return sum + (price * quantity)
        }, 0)

        const deliveryPrice = parseFloat(this.business.deliveryPrice) || 0
        const total = foodTotal + deliveryPrice

        console.log('计算总价:', {
          foodTotal,
          deliveryPrice,
          total
        })

        return total
      } catch (error) {
        console.error('计算总价出错:', error)
        return 0
      }
    }
  },
  filters: {
    sexFilter(value) {
      return value === 1 ? '先生' : '女士'
    }
  },
  methods: {
    // 加载地址信息
    async loadAddress() {
      try {
        // 先从本地存储获取地址
        const addressKey = `address_${this.user.userId}`
        const savedAddress = localStorage.getItem(addressKey)

        if (savedAddress) {
          this.deliveryaddress = JSON.parse(savedAddress)
          console.log('从本地存储获取的地址:', this.deliveryaddress)
          return
        }

        // 如果本地没有地址，从后端获取地址列表
        const addressResponse = await addressApi.listDeliveryAddressByUserId(this.user.userId)
        console.log('地址查询响应:', addressResponse)

        if (addressResponse && addressResponse.length > 0) {
          this.deliveryaddress = addressResponse[0]
          // 保存到本地存储
          localStorage.setItem(addressKey, JSON.stringify(this.deliveryaddress))
          console.log('已保存默认地址:', this.deliveryaddress)
        }
      } catch (error) {
        console.error('加载地址失败:', error)
        this.$message.error('加载地址信息失败')
      }
    },

    // 加载商家和购物车信息
    async loadBusinessAndCart() {
      try {
        // 获取商家信息
        const businessResponse = await businessApi.getBusinessById(this.businessId)
        if (businessResponse) {
          this.business = businessResponse
          console.log('商家信息:', this.business)
        }

        // 获取购物车信息
        await this.loadCartItems()
      } catch (error) {
        console.error('加载商家信息失败:', error)
        this.$message.error('加载商家信息失败')
      }
    },

    // 加载购物车项
    async loadCartItems() {
      try {
        // 使用查询所有购物车项的接口（包括已加入订单的项）
        const response = await cartApi.listAllCartByBusinessId(this.user.userId, this.businessId)
        console.log('购物车查询响应:', response)

        if (response && Array.isArray(response)) {
          // 处理购物车数据
          const cartItems = response.map(cartItem => {
            return {
              ...cartItem,
              quantity: parseInt(cartItem.quantity) || 0
            }
          })
          
          // 更新购物车数组，过滤掉没有食品信息或数量为0的项
          this.cartArr = cartItems.filter(item => 
            item.food && item.food.foodPrice && item.quantity > 0
          )
          
          console.log('当前商家的购物车数据:', this.cartArr)
        } else {
          this.cartArr = []
          console.warn('购物车数据格式不正确或为空')
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
        this.$message.error('加载购物车信息失败')
        this.cartArr = []
      }
    },

    // 跳转到地址选择页面
    toUserAddress() {
      this.$router.push({
        path: '/userAddress',
        query: {
          businessId: this.businessId,
          fromOrder: true
        }
      })
    },
    async toPayment() {
      if (!this.deliveryaddress) {
        this.$message.warning('请选择送货地址！')
        return
      }

      if (this.cartArr.length === 0) {
        this.$message.warning('购物车为空，无法创建订单！')
        return
      }

      try {
        // 创建订单参数
        const orderData = {
          userId: this.user.userId,
          businessId: this.businessId,
          daId: this.deliveryaddress.daId,
          orderTotal: this.totalPrice,
          orderItems: this.cartArr.map(item => ({
            foodId: item.foodId,
            quantity: item.quantity,
            foodPrice: item.food.foodPrice
          }))
        }

        console.log('创建订单参数:', orderData)

        // 创建订单
        const orderId = await orderApi.createOrders(orderData)
        console.log('创建订单响应，获取到的订单ID:', orderId)

        if (orderId > 0) {
          // 跳转到支付页面，确保传递正确的订单ID
          this.$router.push({
            path: '/payment',
            query: {
              orderId: orderId
            }
          })
        } else {
          this.$message.error('创建订单失败！')
        }
      } catch (error) {
        console.error('创建订单错误:', error.response?.data || error)
        if (error === 'No token' || (error.response && error.response.status === 401)) {
          localStorage.removeItem('token')
          this.$removeSessionStorage('user')
          this.$router.push('/login')
        } else {
          this.$message.error('创建订单时发生错误，请稍后重试！')
        }
      }
    },
    handleImageError(e) {
      e.target.src = this.defaultImage
    }
  },
  activated() {
    if (this.user) {
      const addressKey = `address_${this.user.userId}`
      const savedAddress = localStorage.getItem(addressKey)
      if (savedAddress) {
        const newAddress = JSON.parse(savedAddress)
        // 检查地址是否发生变化
        if (!this.deliveryaddress || this.deliveryaddress.daId !== newAddress.daId) {
          this.deliveryaddress = newAddress
          console.log('地址已更新:', this.deliveryaddress)
        }
      }
    }
  }
}
</script>
<style scoped>
/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  height: 100%;
}
/****************** header部分 ******************/
.wrapper header {
  width: 100%;
  height: 12vw;
  background-color: #0097FF;
  color: #fff;
  font-size: 4.8vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}
/****************** 订单信息部分 ******************/
.wrapper .order-info {
  /*注意这里，不设置高，靠内容撑开。因为地址有可能折行*/
  width: 100%;
  margin-top: 12vw;
  background-color: #0097EF;
  box-sizing: border-box;
  padding: 2vw;
  color: #fff;
}
.wrapper .order-info h5 {
  font-size: 5vw;
  font-weight: 600;
  margin-bottom: 1vw;
  color: rgba(255, 255, 255, 0.95);
}
.wrapper .order-info .order-info-address {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2vw 0;
}
.wrapper .order-info .address-content {
  flex: 1;
  margin-right: 3vw;
}
.wrapper .order-info .address-text {
  font-size: 4.5vw;
  font-weight: 500;
  margin-bottom: 1.5vw;
  word-break: break-all;
}
.wrapper .order-info .contact-info {
  font-size: 3.8vw;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 1.5vw;
}
.wrapper .order-info .no-address {
  font-size: 5.5vw;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  padding: 3vw 0;
  text-align: center;
}
.wrapper .order-info .fa-angle-right {
  font-size: 5vw;
  color: rgba(255, 255, 255, 0.8);
}
.wrapper .order-info p {
  font-size: 3vw;
}
.wrapper h3 {
  box-sizing: border-box;
  padding: 3vw;
  font-size: 4vw;
  color: #666;
  border-bottom: solid 1px #DDD;
}
/****************** 订单明细部分 ******************/
.wrapper .order-detailed {
  width: 100%;
}
.wrapper .order-detailed li {
  width: 100%;
  height: 16vw;
  box-sizing: border-box;
  padding: 3vw;
  color: #666;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.wrapper .order-detailed li .order-detailed-left {
  display: flex;
  align-items: center;
}
.wrapper .order-detailed li .order-detailed-left img {
  width: 10vw;
  height: 10vw;
}
.wrapper .order-detailed li .order-detailed-left .food-info {
  margin-left: 3vw;
}
.wrapper .order-detailed li .order-detailed-left .food-name {
  font-size: 3.8vw;
  color: #333;
  margin-bottom: 1vw;
}
.wrapper .order-detailed li .order-detailed-left .food-quantity {
  font-size: 3.2vw;
  color: #666;
}
.wrapper .order-detailed li .food-price {
  font-size: 3.8vw;
  color: #f60;
  font-weight: 500;
}
.wrapper .order-deliveryfee {
  width: 100%;
  height: 16vw;
  box-sizing: border-box;
  padding: 3vw;
  color: #666;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 3.5vw;
}
/****************** 订单合计部分 ******************/
.wrapper .total {
  width: 100%;
  height: 14vw;
  position: fixed;
  left: 0;
  bottom: 0;
  display: flex;
}
.wrapper .total .total-left {
  flex: 2;
  background-color: #505051;
  color: #fff;
  font-size: 4.5vw;
  font-weight: 700;
  user-select: none;
  display: flex;
  justify-content: center;
  align-items: center;
}
.wrapper .total .total-right {
  flex: 1;
  background-color: #38CA73;
  color: #fff;
  font-size: 4.5vw;
  font-weight: 700;
  user-select: none;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}
.empty-cart {
  width: 100%;
  padding: 40px 20px;
  text-align: center;
  color: #999;
  background: #f8f8f8;
  font-size: 4vw;
}
.user-info {
  font-size: 3.2vw;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 1vw;
}
</style>
