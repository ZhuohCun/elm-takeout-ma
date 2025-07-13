<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>我的订单</p>
    </header>

    <!-- 加载中提示 -->
    <div class="loading" v-if="loading">
      加载中...
    </div>

    <!-- 订单列表部分 -->
    <template v-else>
      <h3>未支付订单信息：</h3>
      <ul class="order">
        <li v-for="item in orderArr" :key="item.orderId" v-if="item.orderState === 0 && item.business">
          <div class="order-info">
            <p>
              {{item.business.businessName}}
              <i class="fa fa-caret-down" @click="detailetShow(item)"></i>
            </p>
            <div class="order-info-right">
              <p>&#165;{{item.orderTotal.toFixed(2)}}</p>
              <div class="order-info-right-icon" @click="goToPayment(item)">去支付</div>
            </div>
          </div>
          <ul class="order-detailet" v-show="item.isShowDetailet">
            <li v-for="odItem in item.orderDetailetList" :key="odItem.odId" v-if="odItem.food">
              <p>{{odItem.food?.foodName || '未知商品'}} x {{odItem.quantity}}</p>
              <p>&#165;{{((odItem.food?.foodPrice || 0) * odItem.quantity).toFixed(2)}}</p>
            </li>
            <li>
              <p>配送费</p>
              <p>&#165;{{item.business.deliveryPrice.toFixed(2)}}</p>
            </li>
          </ul>
        </li>
      </ul>

      <h3>已支付订单信息：</h3>
      <ul class="order">
        <li v-for="item in orderArr" :key="item.orderId" v-if="item.orderState === 1 && item.business">
          <div class="order-info">
            <p>
              {{item.business.businessName}}
              <i class="fa fa-caret-down" @click="detailetShow(item)"></i>
            </p>
            <div class="order-info-right">
              <p>&#165;{{item.orderTotal.toFixed(2)}}</p>
            </div>
          </div>
          <ul class="order-detailet" v-show="item.isShowDetailet">
            <li v-for="odItem in item.orderDetailetList" :key="odItem.odId" v-if="odItem.food">
              <p>{{odItem.food?.foodName || '未知商品'}} x {{odItem.quantity}}</p>
              <p>&#165;{{((odItem.food?.foodPrice || 0) * odItem.quantity).toFixed(2)}}</p>
            </li>
            <li>
              <p>配送费</p>
              <p>&#165;{{item.business.deliveryPrice.toFixed(2)}}</p>
            </li>
          </ul>
        </li>
      </ul>
    </template>

    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>

<script>
import Footer from '@/views/Footer.vue';
import { orderApi } from '@/api/order';

export default {
  name: 'OrderList',
  data() {
    return {
      orderArr: [],
      user: null,
      loading: false
    }
  },
  created() {
    this.user = this.$getSessionStorage('user');
    if (!this.user) {
      this.$router.push('/login');
      return;
    }
    this.loadOrders();
  },
  methods: {
    async loadOrders() {
      this.loading = true;
      try {
        // 1. 获取订单基本信息
        const response = await orderApi.listOrdersByUserId({
          userId: this.user.userId
        });
        console.log('获取到的订单列表:', response);

        let orders = response || [];

        // 2. 为每个订单获取详细信息
        const ordersWithDetails = await Promise.all(
          orders.map(async (order) => {
            try {
              // 获取订单详情（包含商家信息）
              const orderDetail = await orderApi.getOrdersById(order.orderId);
              console.log(`订单 ${order.orderId} 的详细信息:`, orderDetail);
              
              // 获取订单明细（包含食品信息）
              const orderDetailetList = await orderApi.getOrderDetailetByOrderId(order.orderId);
              console.log(`订单 ${order.orderId} 的明细列表:`, orderDetailetList);

              return {
                ...order,
                ...orderDetail,
                isShowDetailet: false,
                business: orderDetail?.business || null,
                orderDetailetList: orderDetailetList || []
              };
            } catch (error) {
              console.error(`获取订单 ${order.orderId} 的详情失败:`, error);
              return {
                ...order,
                isShowDetailet: false,
                business: null,
                orderDetailetList: []
              };
            }
          })
        );

        // 过滤掉没有商家信息的订单
        this.orderArr = ordersWithDetails.filter(order => order.business);
        console.log('最终处理后的订单数组:', this.orderArr);
      } catch (error) {
        console.error('加载订单失败:', error);
        this.$message.error('加载订单失败，请稍后重试');
      } finally {
        this.loading = false;
      }
    },
    detailetShow(orders) {
      orders.isShowDetailet = !orders.isShowDetailet;
    },
    goToPayment(order) {
      console.log('跳转到支付页面的订单ID:', order.orderId)
      this.$router.push({
        path: '/payment',
        query: {
          orderId: order.orderId
        }
      })
    }
  },
  components: {
    Footer
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
/****************** 历史订单列表部分 ******************/
.wrapper h3 {
  margin-top: 12vw;
  box-sizing: border-box;
  padding: 4vw;
  font-size: 4vw;
  font-weight: 300;
  color: #999;
}
.wrapper .order {
  width: 100%;
}
.wrapper .order li {
  width: 100%;
}
.wrapper .order li .order-info {
  box-sizing: border-box;
  padding: 2vw 4vw;
  font-size: 4vw;
  color: #666;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.wrapper .order li .order-info .order-info-right {
  display: flex;
}
.wrapper .order li .order-info .order-info-right .order-info-right-icon {
  background-color: #f90;
  color: #fff;
  border-radius: 3px;
  margin-left: 2vw;
  user-select: none;
  cursor: pointer;
}
.wrapper .order li .order-detailet {
  width: 100%;
}
.wrapper .order li .order-detailet li {
  width: 100%;
  box-sizing: border-box;
  padding: 1vw 4vw;
  color: #666;
  font-size: 3vw;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}
</style>
