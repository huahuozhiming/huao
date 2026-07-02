<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2>注册</h2>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学号/工号" prop="id">
          <el-input v-model.number="form.id" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别" v-if="form.role === 'student'">
          <el-radio-group v-model="form.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="专业班级" v-if="form.role === 'student'" prop="clazz">
          <el-input v-model="form.clazz" />
        </el-form-item>
        <el-form-item label="性别" v-if="form.role === 'teacher'">
          <el-radio-group v-model="form.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="register" :loading="loading">注册</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Register',
  data() {
    return {
      form: {
        role: 'student',
        id: '',
        password: '',
        name: '',
        sex: 1,
        clazz: ''
      },
      rules: {
        id: [{ required: true, message: '必填', trigger: 'blur' }],
        password: [{ required: true, message: '必填', trigger: 'blur' }],
        name: [{ required: true, message: '必填', trigger: 'blur' }],
        clazz: [{ required: true, message: '必填', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    register() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const url = this.form.role === 'student' ? '/student/register' : '/teacher/register'
          const payload = {
            password: this.form.password,
            name: this.form.name,
            sex: this.form.sex
          }
          if (this.form.role === 'student') {
            // 学生注册：后端 Student 实体使用 id 字段（学号）
            payload.id = this.form.id
            payload.clazz = this.form.clazz
          } else {
            // 教师注册：后端 Teacher 实体使用 id 字段（工号）
            payload.id = this.form.id
          }
          await request.post(url, payload)
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        } catch (e) {
          // 错误已由 request 拦截器处理，无需额外操作
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.register-card {
  width: 450px;
}
</style>