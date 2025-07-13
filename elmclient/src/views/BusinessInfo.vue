<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>商家信息</p>
    </header>
    <!-- 商家logo部分 -->
    <div class="business-logo">
      <img :src="business.businessImg">
    </div>
    <!-- 商家信息部分 -->
    <div class="business-info">
      <h1>{{business.businessName}}</h1>
      <p>&#165;{{business.starPrice}}起送 &#165;{{business.deliveryPrice}}配送</p>
      <p>{{business.businessExplain}}</p>
    </div>
    <!-- 食品列表部分 -->
    <ul class="food">
      <li v-for="(item,index) in foodArr">
        <div class="food-left">
          <img :src="item.foodImg">
          <div class="food-left-info">
            <h3>{{item.foodName}}</h3>
            <p>{{item.foodExplain}}</p>
            <p>&#165;{{item.foodPrice}}</p>
          </div>
        </div>
        <div class="food-right">
          <div>
            <i class="fa fa-minus-circle" @click="minus(index)" v
               show="item.quantity!=0"></i>
          </div>
          <p><span v-show="item.quantity!==0">{{item.quantity}}</span></p>
          <div>
            <i class="fa fa-plus-circle" @click="add(index)"></i>
          </div>
        </div>
      </li>
    </ul>
    <!-- 购物车部分 -->
    <div class="cart">
      <div class="cart-left">
        <div class="cart-left-icon" :style="totalQuantity==0?'background-color:#505051;':'background-color:#3190E8;'">
          <i class="fa fa-shopping-cart"></i>
          <div class="cart-left-icon-quantity" v-show="totalQuantity!=0">
            {{totalQuantity}}</div>
        </div>
        <div class="cart-left-info">
          <p>&#165;{{totalPrice}}</p>
          <p>另需配送费{{business.deliveryPrice}}元</p>
        </div>
      </div>
      <div class="cart-right">
        <!-- 不够起送费 -->
        <div class="cart-right-item" v-show="totalSettle<business.starPrice"
             style="background-color: #535356;cursor: default;">
          &#165;{{business.starPrice}}起送
        </div>
        <!-- 达到起送费 -->
        <div class="cart-right-item" @click="toOrder" v
             show="totalSettle>=business.starPrice">
          去结算
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { businessApi } from '@/api/business'
import { cartApi } from '@/api/cart'

export default {
  name: 'BusinessInfo',
  data() {
    return {
      businessId: this.$route.query.businessId,
      business: {},
      foodArr: [],
      user: {}
    }
  },
  async created() {
    this.user = this.$getSessionStorage('user')

    try {
      // 获取商家信息
      const businessResponse = await businessApi.getBusinessById(this.businessId)

      if (businessResponse) {
        this.business = businessResponse
        console.log('商家信息:', this.business)
      }

      // 获取食品列表
      const foodResponse = await businessApi.listFoodByBusinessId(this.businessId)

      if (foodResponse) {
        this.foodArr = foodResponse.map(item => ({ ...item, quantity: 0 }))
        console.log('食品列表:', this.foodArr)
      }

      // 如果已登录，查询购物车
      if (this.user) {
        await this.listCart()
      }
    } catch (error) {
      console.error('获取商家信息失败:', error)
    }
  },
  methods: {
    async listCart() {
      try {
        const response = await cartApi.listCart(this.user.userId)

        if (response) {
          const cartArr = response
          // 遍历所有食品列表
          this.foodArr.forEach(foodItem => {
            foodItem.quantity = 0
            cartArr.forEach(cartItem => {
              if (cartItem.foodId === foodItem.foodId) {
                foodItem.quantity = cartItem.quantity
              }
            })
          })
          this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
        }
      } catch (error) {
        console.error('获取购物车失败:', error)
      }
    },

    async add(index) {
      console.log('Add method called with index:', index)
      if (!this.user) {
        this.$router.push('/login')
        return
      }

      try {
        console.log('Entering try block in add method')
        if (this.foodArr[index]?.quantity === 0) {
          await this.saveCart(index)
        } else {
          await this.updateCart(index, 1)
        }
        // 移除不必要的listCart调用，因为我们已经在saveCart和updateCart中更新了本地数据
      } catch (error) {
        console.error('添加商品失败:', error)
      }
    },

    async updateCart(index, num) {
      console.log('updateCart called with index:', index, 'num:', num)
      try {
        // 先更新本地UI，提供即时反馈
        const newQuantity = this.foodArr[index].quantity + num
        this.$set(this.foodArr[index], 'quantity', newQuantity)
        this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
        
        const cartResponse = await cartApi.listCart(this.user.userId)
        if (Array.isArray(cartResponse) && cartResponse.length > 0) {
          const cartItem = cartResponse.find(item => item.foodId === this.foodArr[index].foodId)
          if (cartItem) {
            const response = await cartApi.updateCart({
              cartId: cartItem.cartId,
              businessId: this.businessId,
              userId: this.user.userId,
              foodId: this.foodArr[index].foodId,
              quantity: newQuantity
            })
            console.log('更新购物车响应:', response)
            
            // 如果更新失败，回滚UI状态并显示错误
            if (!response || !response.success) {
              this.$set(this.foodArr[index], 'quantity', this.foodArr[index].quantity - num)
              this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
              this.$message.error('更新购物车失败: ' + (response ? response.message : '未知错误'))
            }
          } else {
            await this.saveCart(index)
          }
        } else {
          await this.saveCart(index)
        }
      } catch (error) {
        // 发生错误时回滚UI状态
        this.$set(this.foodArr[index], 'quantity', this.foodArr[index].quantity - num)
        this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
        console.error('更新购物车失败:', error)
        this.$message.error('更新失败：' + error.message)
      }
    },

    async saveCart(index) {
      console.log('saveCart called with index:', index)
      try {
        // 先更新本地UI，提供即时反馈
        this.$set(this.foodArr[index], 'quantity', 1)
        this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
        
        const response = await cartApi.saveCart({
          businessId: this.businessId,
          userId: this.user.userId,
          foodId: this.foodArr[index].foodId,
          quantity: 1
        })
        console.log('保存购物车响应:', response)
        
        // 如果保存失败，回滚UI状态并显示错误
        if (!response || !response.success) {
          this.$set(this.foodArr[index], 'quantity', 0)
          this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
          this.$message.error('添加到购物车失败: ' + (response ? response.message : '未知错误'))
        }
      } catch (error) {
        // 发生错误时回滚UI状态
        this.$set(this.foodArr[index], 'quantity', 0)
        this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
        console.error('保存购物车失败:', error)
        this.$message.error('添加失败：' + error.message)
      }
    },

    async removeCart(index) {
      try {
        // 保存原始数量以便回滚
        const originalQuantity = this.foodArr[index].quantity
        
        // 先更新本地UI，提供即时反馈
        this.$set(this.foodArr[index], 'quantity', 0)
        this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
        
        const cartResponse = await cartApi.listCart(this.user.userId)
        const cartItem = cartResponse.find(item => item.foodId === this.foodArr[index].foodId)
        
        if (cartItem) {
          const response = await cartApi.removeCart({
            cartId: cartItem.cartId,
            businessId: this.businessId,
            userId: this.user.userId,
            foodId: this.foodArr[index].foodId
          })
          
          console.log('删除购物车响应:', response)
          
          // 如果删除失败，回滚UI状态并显示错误
          if (!response || !response.success) {
            this.$set(this.foodArr[index], 'quantity', originalQuantity)
            this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
            this.$message.error('从购物车中删除失败: ' + (response ? response.message : '未知错误'))
          }
        }
      } catch (error) {
        // 发生错误时回滚UI状态
        this.$set(this.foodArr[index], 'quantity', 1)
        this.foodArr.sort((a, b) => a.foodName.localeCompare(b.foodName))
        console.error('删除购物车失败:', error)
        this.$message.error('删除失败：' + error.message)
      }
    },

    async minus(index) {
      if (!this.user) {
        this.$router.push('/login')
        return
      }

      try {
        if (this.foodArr[index].quantity > 1) {
          await this.updateCart(index, -1)
        } else {
          await this.removeCart(index)
        }
        // 移除不必要的listCart调用，因为我们已经在updateCart和removeCart中更新了本地数据
      } catch (error) {
        console.error('减少商品失败:', error)
      }
    },

    toOrder() {
      console.log('跳转订单页面，商家ID:', this.business.businessId)
      
      this.$router.push({
        path: '/orders',
        query: {
          businessId: this.business.businessId
        }
      })
    }
  },
  computed: {
    // 食品总价格
    totalPrice() {
      return this.foodArr.reduce((sum, item) => sum + item.foodPrice * item.quantity, 0)
    },
    // 食品总数量
    totalQuantity() {
      return this.foodArr.reduce((sum, item) => sum + item.quantity, 0)
    },
    // 结算总价格
    totalSettle() {
      return this.totalPrice + this.business.deliveryPrice
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

/****************** 商家logo部分 ******************/
.wrapper .business-logo {
  width: 100%;
  height: 35vw;
  /*使用上外边距避开header部分*/
  margin-top: 12vw;
  display: flex;
  justify-content: center;
  align-items: center;
}

.wrapper .business-logo img {
  width: 40vw;
  height: 30vw;
  border-radius: 5px;
}

/****************** 商家信息部分 ******************/
.wrapper .business-info {
  width: 100%;
  height: 20vw;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.wrapper .business-info h1 {
  font-size: 5vw;
}

.wrapper .business-info p {
  font-size: 3vw;
  color: #666;
  margin-top: 1vw;
}

/****************** 食品列表部分 ******************/
.wrapper .food {
  width: 100%;
  /*使用下外边距避开footer部分*/
  margin-bottom: 14vw;
}

.wrapper .food li {
  width: 100%;
  box-sizing: border-box;
  padding: 2.5vw;
  user-select: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .food li .food-left {
  display: flex;
  align-items: center;
}

.wrapper .food li .food-left img {
  width: 20vw;
  height: 20vw;
}

.wrapper .food li .food-left .food-left-info {
  margin-left: 3vw;
}

.wrapper .food li .food-left .food-left-info h3 {
  font-size: 3.8vw;
  color: #555;
}

.wrapper .food li .food-left .food-left-info p {
  font-size: 3vw;
  color: #888;
  margin-top: 2vw;
}

.wrapper .food li .food-right {
  width: 16vw;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .food li .food-right .fa-minus-circle {
  font-size: 5.5vw;
  color: #999;
  cursor: pointer;
}

.wrapper .food li .food-right p {
  font-size: 3.6vw;
  color: #333;
}

.wrapper .food li .food-right .fa-plus-circle {
  font-size: 5.5vw;
  color: #0097EF;
  cursor: pointer;
}

/****************** 购物车部分 ******************/
.wrapper .cart {
  width: 100%;
  height: 14vw;
  position: fixed;
  left: 0;
  bottom: 0;
  display: flex;
}

.wrapper .cart .cart-left {
  flex: 2;
  background-color: #505051;
  display: flex;
}

.wrapper .cart .cart-left .cart-left-icon {
  width: 16vw;
  height: 16vw;
  box-sizing: border-box;
  border: solid 1.6vw #444;
  border-radius: 8vw;
  background-color: #3190E8;
  font-size: 7vw;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: -4vw;
  margin-left: 3vw;
  position: relative;
}

.wrapper .cart .cart-left .cart-left-icon-quantity {
  width: 5vw;
  height: 5vw;
  border-radius: 2.5vw;
  background-color: red;
  color: #fff;
  font-size: 3.6vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  right: -1.5vw;
  top: -1.5vw;
}

.wrapper .cart .cart-left .cart-left-info p:first-child {
  font-size: 4.5vw;
  color: #fff;
  margin-top: 1vw;
}

.wrapper .cart .cart-left .cart-left-info p:last-child {
  font-size: 2.8vw;
  color: #AAA;
}

.wrapper .cart .cart-right {
  flex: 1;
}

/*达到送费时的样式*/
.wrapper .cart .cart-right .cart-right-item {
  width: 100%;
  height: 100%;
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

/*不够起送费时的样式（只有背景色和鼠标样式的区别）*/
/*
.wrapper .cart .cart-right .cart-right-item{
width: 100%;
height: 100%;
background-color: #535356;
color: #fff;
font-size: 4.5vw;
font-weight: 700;
user-select: none;
display: flex;
justify-content: center;
align-items: center;
}
*/
</style>
