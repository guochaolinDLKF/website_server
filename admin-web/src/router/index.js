import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/user'

const Layout = () => import('@/layout/index.vue')

// 业务路由统一挂在 Layout 下；path 与后端菜单 path 对应，侧边栏由后端菜单树驱动。
export const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true, title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '主控制台', icon: 'DataLine' }
      },
      {
        path: '/profile/password',
        name: 'ChangePassword',
        component: () => import('@/views/profile/password.vue'),
        meta: { title: '修改密码', hidden: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    children: [
      {
        path: '/user/list',
        name: 'UserList',
        component: () => import('@/views/operation/user/list.vue'),
        meta: { title: '用户列表', perm: 'user:list' }
      },
      {
        path: '/user/detail/:id',
        name: 'UserDetail',
        component: () => import('@/views/operation/user/detail.vue'),
        meta: { title: '用户详情', hidden: true, perm: 'user:detail' }
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    children: [
      { path: '/order/list', name: 'OrderList', component: () => import('@/views/operation/order/list.vue'), meta: { title: '订单管理', perm: 'order:list' } },
      { path: '/order/goods', name: 'GoodsList', component: () => import('@/views/operation/order/goods.vue'), meta: { title: '商品管理', perm: 'goods:list' } },
      { path: '/order/benefit', name: 'BenefitList', component: () => import('@/views/operation/order/benefit.vue'), meta: { title: '权益管理', perm: 'benefit:list' } },
      { path: '/order/payment-failure', name: 'PaymentFailure', component: () => import('@/views/operation/order/paymentFailure.vue'), meta: { title: '支付异常监控', perm: 'payfail:list' } }
    ]
  },
  {
    path: '/analytics',
    component: Layout,
    children: [
      { path: '/analytics/realtime', name: 'AnalyticsRealtime', component: () => import('@/views/operation/analytics/realtime.vue'), meta: { title: '实时数据', perm: 'analytics:realtime' } },
      { path: '/analytics/active', name: 'AnalyticsActive', component: () => import('@/views/operation/analytics/active.vue'), meta: { title: '活跃数据', perm: 'analytics:active' } },
      { path: '/analytics/newdata', name: 'AnalyticsNewData', component: () => import('@/views/operation/analytics/newdata.vue'), meta: { title: '新增数据', perm: 'analytics:newdata' } },
      { path: '/analytics/payment', name: 'AnalyticsPayment', component: () => import('@/views/operation/analytics/payment.vue'), meta: { title: '付费分析', perm: 'analytics:payanalysis' } }
    ]
  },
  {
    path: '/system',
    component: Layout,
    children: [
      { path: '/system/admin', name: 'AdminUser', component: () => import('@/views/operation/system/admin.vue'), meta: { title: '管理员管理', perm: 'admin:list' } },
      { path: '/system/role', name: 'Role', component: () => import('@/views/operation/system/role.vue'), meta: { title: '成员管理', perm: 'role:list' } },
      { path: '/system/login-log', name: 'LoginLog', component: () => import('@/views/operation/system/loginLog.vue'), meta: { title: '登录日志', perm: 'log:login' } },
      { path: '/system/operation-log', name: 'OperationLog', component: () => import('@/views/operation/system/operationLog.vue'), meta: { title: '操作日志', perm: 'log:operation' } }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { hidden: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  // BASE_URL 由 Vite base 注入：开发 '/'，生产 '/admin/'，保证路由前缀与部署路径一致
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 全局前置守卫：未登录跳登录；已登录但信息未加载则拉取一次
router.beforeEach(async (to, from, next) => {
  const hasToken = getToken()
  if (to.path === '/login') {
    return next(hasToken ? '/' : true)
  }
  if (!hasToken) {
    return next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
  }
  const userStore = useUserStore()
  if (!userStore.loaded) {
    try {
      await userStore.fetchInfo()
    } catch (e) {
      userStore.reset()
      return next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    }
  }
  next()
})

export default router
