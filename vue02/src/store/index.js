import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    role: localStorage.getItem('role') || '' // 'student' 或 'teacher'
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER_INFO(state, info) {
      state.userInfo = info
      localStorage.setItem('userInfo', JSON.stringify(info))
    },
    SET_ROLE(state, role) {
      state.role = role
      localStorage.setItem('role', role)
    },
    CLEAR_ALL(state) {
      state.token = ''
      state.userInfo = null
      state.role = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('role')
    }
  },
  actions: {
    logout({ commit }) {
      commit('CLEAR_ALL')
    }
  },
  getters: {
    isLoggedIn: state => !!state.token,
    currentId: state => {
      if (state.role === 'student') return state.userInfo?.studentId
      if (state.role === 'teacher') return state.userInfo?.teacherId
      return null
    }
  }
})