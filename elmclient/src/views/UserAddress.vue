<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>地址管理</p>
    </header>
    <!-- 地址列表部分 -->
    <ul class="addresslist">
      <li v-for="item in deliveryAddressArr">
        <div class="addresslist-left" @click="setDeliveryAddress(item)">
          <h3>{{item.contactName}}{{item.contactSex | sexFilter}} {{item.contactTel}}
          </h3>
          <p>{{item.address}}</p>
        </div>
        <div class="addresslist-right">
          <i class="fa fa-edit" @click="editUserAddress(item.daId)"></i>
          <i class="fa fa-remove" @click="removeUserAddress(item.daId)"></i>
        </div>
      </li>
    </ul>
    <!-- 新增地址部分 -->
    <div class="addbtn" @click="toAddUserAddress">
      <i class="fa fa-plus-circle"></i>
      <p>新增收货地址</p>
    </div>
    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>
<script>
import Footer from '@/views/Footer.vue'
import { addressApi } from '@/api/address'

export default {
  name: 'UserAddress',
  data() {
    return {
      businessId: this.$route.query.businessId,
      user: {},
      deliveryAddressArr: []
    }
  },
  async created() {
    this.user = this.$getSessionStorage('user')
    if (!this.user) {
      this.$router.push('/login')
      return
    }
    await this.listDeliveryAddressByUserId()
  },
  components: {
    Footer
  },
  filters: {
    sexFilter(value) {
      return value === 1 ? '先生' : '女士'
    }
  },
  methods: {
    async listDeliveryAddressByUserId() {
      try {
        const response = await addressApi.listDeliveryAddressByUserId(this.user.userId)
        if (response) {
          this.deliveryAddressArr = response
          console.log('获取到的地址列表:', this.deliveryAddressArr)
        }
      } catch (error) {
        console.error('获取地址列表失败:', error)
        if (error === 'No token' || (error.response && error.response.status === 401)) {
          localStorage.removeItem('token')
          this.$removeSessionStorage('user')
          this.$router.push('/login')
        }
      }
    },
    setDeliveryAddress(deliveryAddress) {
      const addressKey = `address_${this.user.userId}`
      localStorage.setItem(addressKey, JSON.stringify(deliveryAddress))
      console.log('保存的地址信息:', deliveryAddress)

      const fromOrder = this.$route.query.fromOrder
      if (fromOrder) {
        this.$router.go(-1)
      } else {
        this.$router.push({
          path: '/orders',
          query: { businessId: this.businessId }
        })
      }
    },
    toAddUserAddress() {
      this.$router.push({
        path: '/addUserAddress',
        query: { businessId: this.businessId }
      })
    },
    editUserAddress(daId) {
      this.$router.push({
        path: '/editUserAddress',
        query: {
          businessId: this.businessId,
          daId: daId
        }
      })
    },
    async removeUserAddress(daId) {
      if (!confirm('确认要删除此送货地址吗？')) {
        return
      }

      try {
        const response = await addressApi.removeDeliveryAddress(daId)
        if (response > 0) {
          const addressKey = `address_${this.user.userId}`
          const savedAddress = localStorage.getItem(addressKey)
          if (savedAddress) {
            const currentAddress = JSON.parse(savedAddress)
            if (currentAddress.daId === daId) {
              localStorage.removeItem(addressKey)
            }
          }
          await this.listDeliveryAddressByUserId()
          this.$message.success('删除地址成功')
        } else {
          this.$message.error('删除地址失败')
        }
      } catch (error) {
        console.error('删除地址失败:', error)
        this.$message.error('删除地址时发生错误')
      }
    }
  }
}
</script>
<style scoped>
/*************** 总容器 ***************/
.wrapper {
  width: 100%;
  height: 100%;
  background-color: #F5F5F5;
}
/*************** header ***************/
.wrapper header {
  width: 100%;
  height: 12vw;
  background-color: #0097FF;
  display: flex;
  justify-content: space-around;
  align-items: center;
  color: #fff;
  font-size: 4.8vw;
  position: fixed;
  left: 0;
  top: 0;
  /*保证在最上层*/
  z-index: 1000;
}
/*************** addresslist ***************/
.wrapper .addresslist {
  width: 100%;
  margin-top: 12vw;
  background-color: #fff;
}
.wrapper .addresslist li {
  width: 100%;
  box-sizing: border-box;
  border-bottom: solid 1px #DDD;
  padding: 3vw;
  display: flex;
}
.wrapper .addresslist li .addresslist-left {
  flex: 5;
  /*左边这块区域是可以点击的*/
  user-select: none;
  cursor: pointer;
}
.wrapper .addresslist li .addresslist-left h3 {
  font-size: 4.6vw;
  font-weight: 300;
  color: #666;
}
.wrapper .addresslist li .addresslist-left p {
  font-size: 4vw;
  color: #666;
}
.wrapper .addresslist li .addresslist-right {
  flex: 1;
  font-size: 5.6vw;
  color: #999;
  cursor: pointer;
  display: flex;
  justify-content: space-around;
  align-items: center;
}
/*************** 新增地址部分 ***************/
.wrapper .addbtn {
  width: 100%;
  height: 14vw;
  border-top: solid 1px #DDD;
  border-bottom: solid 1px #DDD;
  background-color: #fff;
  margin-top: 4vw;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 4.5vw;
  color: #0097FF;
  user-select: none;
  cursor: pointer;
}
.wrapper .addbtn p {
  margin-left: 2vw;
}
</style>
