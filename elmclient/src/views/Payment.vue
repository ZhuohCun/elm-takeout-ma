<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>在线支付</p>
    </header>
    <!-- 订单信息部分 -->
    <h3>订单信息：</h3>
    <div class="order-info">
      <p>
        {{orders.business.businessName || '加载中...'}}
        <i class="fa fa-caret-down" @click="detailetShow"></i>
      </p>
      <p>&#165;{{orders.orderTotal.toFixed(2)}}</p>
    </div>
    <!-- 订单明细部分 -->
    <ul class="order-detailet" v-show="isShowDetailet">
      <li v-for="item in orders.list" :key="item.foodId">
        <p>{{item.food?.foodName || '未知商品'}} x {{item.quantity}}</p>
        <p>&#165;{{((item.food?.foodPrice || 0) * item.quantity).toFixed(2)}}</p>
      </li>
      <li>
        <p>配送费</p>
        <p>&#165;{{orders.business.deliveryPrice.toFixed(2)}}</p>
      </li>
    </ul>
    <!-- 支付方式部分 -->
    <ul class="payment-type">
      <li @click="selectPayment(1)">
        <img src="../assets/alipay.png">
        <i class="fa fa-check-circle" v-show="selectedPayment === 1"></i>
      </li>
      <li @click="selectPayment(2)">
        <img src="../assets/wechat.png">
        <i class="fa fa-check-circle" v-show="selectedPayment === 2"></i>
      </li>
    </ul>
    <div class="payment-button">
      <button @click="confirmPayment">确认支付</button>
    </div>
    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>

<script>
import Footer from '@/views/Footer.vue';
import { orderApi } from '@/api/order';
import { businessApi } from '@/api/business';

export default {
  name: 'Payment',
  data() {
    return {
      orderId: this.$route.query.orderId,
      orders: {
        business: {
          businessName: '',
          deliveryPrice: 0
        },
        list: [],
        orderTotal: 0
      },
      isShowDetailet: false,
      selectedPayment: 1
    };
  },
  created() {
    if (!this.orderId) {
      this.$message.error('订单ID不能为空');
      this.$router.push('/index');
      return;
    }
    console.log('订单ID:', this.orderId);
    this.loadOrderData();
  },
  mounted() {
    history.pushState(null, null, document.URL);
    window.onpopstate = () => {
      this.$router.push({ path: '/index' });
    };
  },
  destroyed() {
    window.onpopstate = null;
  },
  methods: {
    async loadOrderData() {
      try {
        // 获取订单信息
        const orderData = await orderApi.getOrdersById(this.orderId);
        console.log('获取到的订单数据:', orderData);

        if (!orderData) {
          this.$message.error('获取订单数据失败');
          return;
        }

        // 获取商家信息
        try {
          const businessResponse = await businessApi.getBusinessById(orderData.businessId);
          console.log('获取到的商家信息:', businessResponse);

          if (businessResponse) {
            orderData.business = businessResponse;
          }
        } catch (error) {
          console.error('获取商家信息失败:', error);
        }

        // 更新订单数据
        this.orders = {
          ...orderData,
          business: orderData.business || {
            businessName: '',
            deliveryPrice: 0
          },
          list: orderData.list || [],
          orderTotal: orderData.orderTotal || 0
        };

        console.log('最终处理后的订单数据:', this.orders);
      } catch (error) {
        console.error('获取订单数据失败:', error);
        if (error === 'No token' || (error.response && error.response.status === 401)) {
          localStorage.removeItem('token');
          this.$removeSessionStorage('user');
          this.$router.push('/login');
        } else {
          this.$message.error('获取订单数据失败，请稍后重试');
        }
      }
    },
    detailetShow() {
      this.isShowDetailet = !this.isShowDetailet;
    },
    selectPayment(type) {
      this.selectedPayment = type;
    },
    async confirmPayment() {
      try {
        console.log('确认支付按钮被点击');

        // 确认支付前的提示或验证逻辑（可选）
        const confirm = window.confirm('确定要支付吗？');
        console.log('用户确认支付:', confirm);
        if (!confirm) return;

        console.log('准备更新订单状态');

        // 更新订单状态为已支付
        const params = {
          orderId: this.orderId,
          orderState: 1  // 1表示已支付
        };

        const response = await orderApi.updateOrderState(params);
        console.log('更新订单状态响应:', response);

        // 检查后端返回的 success 字段
        let success = false;
        if (response && response.success === true) {
          success = true;
        }

        if (success) {
          console.log('支付成功，准备跳转到支付成功页面');
          this.$message.success('支付成功！');
          this.$router.push({
            path: '/OrderList',
            query: {
              orderId: this.orderId
            }
          });
        } else {
          console.log('支付失败');
          this.$message.error('支付失败，请稍后重试');
        }
      } catch (error) {
        console.error('更新订单状态失败:', error);
        if (error.response) {
          // 处理 HTTP 错误
          if (error.response.status === 401) {
            localStorage.removeItem('token');
            this.$removeSessionStorage('user');
            this.$router.push('/login');
          } else {
            console.error('服务器返回错误码:', error.response.status);
            this.$message.error(`支付失败，服务器返回错误码: ${error.response.status}`);
          }
        } else if (error.request) {
          // 请求已发出，但没有收到响应（网络问题）
          console.error('网络连接异常:', error.request);
          this.$message.error('支付失败，网络连接异常，请检查您的网络设置');
        } else {
          // 其他错误
          console.error('其他错误:', error);
          this.$message.error('支付失败，请稍后重试');
        }
      }
    }
  },
  components: {
    Footer
  }
};
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
.wrapper h3 {
  margin-top: 12vw;
  box-sizing: border-box;
  padding: 4vw 4vw 0;
  font-size: 4vw;
  font-weight: 300;
  color: #999;
}
.wrapper .order-info {
  box-sizing: border-box;
  padding: 4vw;
  font-size: 4vw;
  color: #666;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  margin-bottom: 2vw;
}
.wrapper .order-info p:first-child {
  display: flex;
  align-items: center;
  gap: 2vw;
}
.wrapper .order-info p:last-child {
  color: #f60;
  font-weight: 500;
}
/****************** 订单明细部分 ******************/
.wrapper .order-detailet {
  background: #fff;
  margin-bottom: 4vw;
}
.wrapper .order-detailet li {
  padding: 3vw 4vw;
  border-bottom: 1px solid #f5f5f5;
}
.wrapper .order-detailet li:last-child {
  border-bottom: none;
  background: #f8f8f8;
}
.wrapper .order-detailet li p {
  font-size: 3vw;
  color: #666;
}
/****************** 支付方式部分 ******************/
.wrapper .payment-type {
  width: 100%;
}
.wrapper .payment-type li {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.wrapper .payment-type li img {
  width: 33vw;
  height: 8.9vw;
}
.wrapper .payment-type li .fa-check-circle {
  font-size: 5vw;
  color: #38CA73;
}
.wrapper .payment-button {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw;
}
.wrapper .payment-button button {
  width: 100%;
  height: 10vw;
  border: none;
  /*去掉外轮廓线*/
  outline: none;
  border-radius: 4px;
  background-color: #38CA73;
  color: #fff;
  font-size: 4.5vw;
}
</style>
