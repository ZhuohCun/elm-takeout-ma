<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>我的信息</p>
    </header>

    <!-- 用户信息部分 -->
    <div class="user-info" v-if="user">
      <div class="avatar-section">
        <i class="fa fa-user-circle-o"></i>
        <h2>{{user.userName}}</h2>
      </div>

      <div class="info-list">
        <div class="info-item">
          <span class="label">手机号：</span>
          <span class="value">{{user.userId}}</span>
        </div>
        <div class="info-item">
          <span class="label">用户名：</span>
          <span class="value">{{user.userName}}</span>
        </div>
        <div class="info-item">
          <span class="label">性别：</span>
          <span class="value">{{user.userSex === 1 ? '男' : '女'}}</span>
        </div>
      </div>

      <div class="action-buttons">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>

<script>
import Footer from '@/views/Footer.vue';
import { userApi } from '@/api/user';

export default {
  name: 'UserInfo',
  data() {
    return {
      user: null
    }
  },
  async created() {
    const userStorage = this.$getSessionStorage('user');
    if (!userStorage) {
      this.$router.push('/login');
      return;
    }

    try {
      const response = await userApi.getUserInfo(userStorage.userId);
      if (response) {
        this.user = response;
      } else {
        this.$message.error('获取用户信息失败');
        this.$router.push('/login');
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
      if (error.response) {
        if (error.response.status === 401) {
          this.$message.error('请重新登录');
        } else if (error.response.status === 405) {
          this.$message.error('接口不支持该请求方法');
        } else {
          this.$message.error('获取用户信息失败');
        }
      } else {
        this.$message.error('网络错误');
      }
      this.$router.push('/login');
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('token');
      this.$removeSessionStorage('user');
      this.$message.success('退出成功');
      this.$router.push('/login');
    }
  },
  components: {
    Footer
  }
}
</script>

<style scoped>
.wrapper {
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
}

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

.user-info {
  margin-top: 12vw;
  padding: 4vw;
}

.avatar-section {
  text-align: center;
  padding: 6vw 0;
  background-color: #fff;
  border-radius: 2vw;
  margin-bottom: 3vw;
}

.avatar-section i {
  font-size: 20vw;
  color: #0097FF;
}

.avatar-section h2 {
  margin-top: 2vw;
  font-size: 4.5vw;
  color: #333;
}

.info-list {
  background-color: #fff;
  border-radius: 2vw;
  padding: 2vw 4vw;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 3vw 0;
  border-bottom: 1px solid #eee;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #666;
  font-size: 3.8vw;
}

.info-item .value {
  color: #333;
  font-size: 3.8vw;
}

.action-buttons {
  margin-top: 6vw;
  padding: 0 4vw;
}

.logout-btn {
  width: 100%;
  height: 11vw;
  background-color: #ff4646;
  color: #fff;
  border: none;
  border-radius: 1vw;
  font-size: 4vw;
  cursor: pointer;
}

.logout-btn:active {
  opacity: 0.8;
}
</style> 