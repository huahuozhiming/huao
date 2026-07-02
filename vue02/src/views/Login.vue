<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>AI 辅助教学平台</h2>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="账号" prop="id">
          <el-input v-model.number="form.id" placeholder="学号/工号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" :loading="loading">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        role: 'student',
        id: '',
        password: ''
      },
      rules: {
        id: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    login() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const url = this.form.role === 'student' ? '/student/login' : '/teacher/login'
          const res = await request.post(url, {
            id: this.form.id,
            password: this.form.password
          })

          // 保存 token 和用户信息
          this.$store.commit('SET_TOKEN', res.data.token)
          const userInfo = { ...res.data }
          delete userInfo.token
          this.$store.commit('SET_USER_INFO', userInfo)
          this.$store.commit('SET_ROLE', this.form.role)

          this.$message.success('登录成功')
          this.$router.push(this.form.role === 'student' ? '/student' : '/teacher')
        } catch (e) {
          // 错误在拦截器已处理
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.login-card {
  width: 400px;
}
</style>