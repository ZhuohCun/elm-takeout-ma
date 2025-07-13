<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>新增送货地址</p>
    </header>
    <!-- 表单部分 -->
    <ul class="form-box">
      <li>
        <div class="title">联系人：</div>
        <div class="content">
          <input 
            type="text" 
            v-model.trim="deliveryAddress.contactName" 
            placeholder="请输入联系人姓名"
            maxlength="20"
          >
        </div>
      </li>
      <li>
        <div class="title">性别：</div>
        <div class="content" style="font-size: 3vw;">
          <label>
            <input 
              type="radio" 
              v-model="deliveryAddress.contactSex" 
              value="1"
              style="width:6vw;height:3.2vw;"
            >男
          </label>
          <label style="margin-left: 20px;">
            <input 
              type="radio" 
              v-model="deliveryAddress.contactSex" 
              value="0"
              style="width:6vw;height:3.2vw;"
            >女
          </label>
        </div>
      </li>
      <li>
        <div class="title">电话：</div>
        <div class="content">
          <input 
            type="tel" 
            v-model.trim="deliveryAddress.contactTel" 
            placeholder="请输入手机号码"
            maxlength="11"
            @input="validatePhone(deliveryAddress.contactTel)"
          >
        </div>
      </li>
      <li>
        <div class="title">收货地址：</div>
        <div class="content">
          <input 
            type="text" 
            v-model.trim="deliveryAddress.address" 
            placeholder="请输入详细收货地址"
            maxlength="100"
          >
        </div>
      </li>
    </ul>
    <div class="button-add">
      <button @click="addUserAddress">保存</button>
    </div>
    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>

<script>
import Footer from '@/views/Footer.vue';
import { addressApi } from '@/api/address';

export default {
  name: 'AddUserAddress',
  data() {
    return {
      businessId:this.$route.query.businessId,
      user:{},
      deliveryAddress:{
        contactName:'',
        contactSex:1,
        contactTel:'',
        address:''
      }
    }
  },
  created() {
    this.user = this.$getSessionStorage('user');
  },
  components: {
    Footer
  },
  methods: {
    async addUserAddress() {
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
      if (!this.deliveryAddress.address.trim()) {
        this.$message.warning('联系人地址不能为空！')
        return
      }

      try {
        // 构造请求参数
        const addressData = {
          userId: this.user.userId,
          contactName: this.deliveryAddress.contactName.trim(),
          contactSex: parseInt(this.deliveryAddress.contactSex),
          contactTel: this.deliveryAddress.contactTel.trim(),
          address: this.deliveryAddress.address.trim()
        }

        console.log('发送地址数据:', addressData)
        const response = await addressApi.saveDeliveryAddress(addressData)
        console.log('保存地址响应:', response)
        
        if (response > 0) {
          this.$message.success('新增地址成功！')
          
          // 保存到本地存储
          const addressKey = `address_${this.user.userId}`
          const savedAddress = {
            ...addressData,
            daId: response
          }
          localStorage.setItem(addressKey, JSON.stringify(savedAddress))

          // 返回上一页
          this.$router.push({
            path: '/userAddress',
            query: { 
              businessId: this.businessId,
              fromOrder: true
            }
          })
        } else {
          this.$message.error('新增地址失败！')
        }
      } catch (error) {
        console.error('保存地址错误:', error)
        this.$message.error('保存地址时发生错误，请稍后重试！')
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

/* 添加一些新的样式 */
.content input {
  border: none;
  outline: none;
  width: 100%;
  height: 4vw;
  font-size: 3vw;
  background: transparent;
  border-bottom: 1px solid #eee;
}

.content input:focus {
  border-bottom-color: #0097FF;
}

.content label {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
}

.button-add button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  font-weight: 700;
  border: none;
  outline: none;
  border-radius: 4px;
  color: #fff;
  background-color: #38CA73;
  cursor: pointer;
  transition: opacity 0.3s;
}

.button-add button:active {
  opacity: 0.8;
}
</style>
