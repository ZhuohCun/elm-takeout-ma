<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>编辑送货地址</p>
    </header>
    <!-- 表单部分 -->
    <ul class="form-box">
      <li>
        <div class="title">
          联系人：
        </div>
        <div class="content">
          <input type="text" v-model="deliveryAddress.contactName" placeholder="联系人
姓名">
        </div>
      </li>
      <li>
        <div class="title">
          性别：
        </div>
        <div class="content" style="font-size: 3vw;">
          <input type="radio" v-model="deliveryAddress.contactSex" value="1"
                 style="width:6vw;height:3.2vw;" checked>男
          <input type="radio" v-model="deliveryAddress.contactSex" value="0"
                 style="width:6vw;height:3.2vw;">女
        </div>
      </li>
      <li>
        <div class="title">
          电话：
        </div>
        <div class="content">
          <input type="tel" v-model="deliveryAddress.contactTel" placeholder="电话">
        </div>
      </li>
      <li>
        <div class="title">
          收货地址：
        </div>
        <div class="content">
          <input type="text" v-model="deliveryAddress.address" placeholder="收货地址">
        </div>
      </li>
    </ul>
    <div class="button-add">
      <button @click="editUserAddress">更新</button>
    </div>
    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>
<script>
import Footer from '@/views/Footer.vue'
import { addressApi } from '@/api/address'

export default {
  name: 'EditUserAddress',
  data() {
    return {
      businessId: this.$route.query.businessId,
      daId: this.$route.query.daId,
      user: {},
      deliveryAddress: {}
    }
  },
  async created() {
    this.user = this.$getSessionStorage('user')
    try {
      const response = await addressApi.getDeliveryAddressById(this.daId)
      this.deliveryAddress = response
      console.log('获取到的地址信息:', this.deliveryAddress)
    } catch (error) {
      console.error('获取地址信息失败:', error)
      this.$message.error('获取地址信息失败')
    }
  },
  components: {
    Footer
  },
  methods: {
    async editUserAddress() {
      // 表单验证
      if (!this.deliveryAddress.contactName?.trim()) {
        this.$message.warning('联系人姓名不能为空！')
        return
      }
      if (!this.deliveryAddress.contactTel?.trim()) {
        this.$message.warning('联系人电话不能为空！')
        return
      }
      if (!this.validatePhone(this.deliveryAddress.contactTel)) {
        this.$message.warning('请输入正确的手机号码！')
        return
      }
      if (!this.deliveryAddress.address?.trim()) {
        this.$message.warning('联系人地址不能为空！')
        return
      }

      try {
        // 构造更新数据
        const updateData = {
          daId: this.daId,
          userId: this.user.userId,
          contactName: this.deliveryAddress.contactName.trim(),
          contactSex: parseInt(this.deliveryAddress.contactSex),
          contactTel: this.deliveryAddress.contactTel.trim(),
          address: this.deliveryAddress.address.trim()
        }

        console.log('更新地址数据:', updateData)
        const response = await addressApi.updateDeliveryAddress(updateData)
        console.log('更新地址响应:', response)

        if (response > 0) {
          this.$message.success('更新地址成功！')
          // 更新本地存储的地址信息
          const addressKey = `address_${this.user.userId}`
          localStorage.setItem(addressKey, JSON.stringify(updateData))
          
          this.$router.push({
            path: '/userAddress',
            query: { businessId: this.businessId }
          })
        } else {
          this.$message.error('更新地址失败！')
        }
      } catch (error) {
        console.error('更新地址错误:', error)
        this.$message.error('更新地址时发生错误，请稍后重试！')
      }
    },

    // 添加手机号码验证方法
    validatePhone(phone) {
      const reg = /^1[3-9]\d{9}$/
      return reg.test(phone)
    }
  }
}
</script>
<style scoped>
/*************** 总容器 ***************/
.wrapper {
  width: 100%;
  height: 100%;
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
/*************** （表单信息） ***************/
.wrapper .form-box {
  width: 100%;
  margin-top: 12vw;
}
.wrapper .form-box li {
  box-sizing: border-box;
  padding: 4vw 3vw 0 3vw;
  display: flex;
}
.wrapper .form-box li .title {
  flex: 0 0 18vw;
  font-size: 3vw;
  font-weight: 700;
  color: #666;
}
.wrapper .form-box li .content {
  flex: 1;
  display: flex;
  align-items: center;
}
.wrapper .form-box li .content input {
  border: none;
  outline: none;
  width: 100%;
  height: 4vw;
  font-size: 3vw;
}
.wrapper .button-add {
  box-sizing: border-box;
  padding: 4vw 3vw 0 3vw;
}
.wrapper .button-add button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  font-weight: 700;
  border: none;
  outline: none;
  border-radius: 4px;
  color: #fff;
  background-color: #38CA73;
}
</style>
