import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/views/Login'
import Register from '@/views/Register'
import StudentDashboard from '@/views/student/Dashboard'
import TeacherDashboard from '@/views/teacher/Dashboard'
import store from '@/store'

Vue.use(Router)

const router = new Router({
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    {
      path: '/student',
      component: StudentDashboard,
      meta: { requiresAuth: true, role: 'student' }
    },
    {
      path: '/teacher',
      component: TeacherDashboard,
      meta: { requiresAuth: true, role: 'teacher' }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const role = to.meta.role
  const isLoggedIn = store.getters.isLoggedIn

  if (requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (requiresAuth && role && store.state.role !== role) {
    next('/login')
  } else {
    next()
  }
})

export default router