import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/SearchResults/:searchString',
    name: 'Search Results',
    // route level code-splitting
    // this generates a separate chunk (search-results.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "SearchResults" */ '../views/SearchResults/_search-string.vue')
  },
  {
    path: '/Games/:gameid',
    name: 'Games',
    component: () => import('../views/Games/_id.vue')
  },
  {
    path: '/Deals/:dealid',
    name: 'Deals',
    component: () => import('../views/Deals/_id.vue')
  }
]

const router = new VueRouter({
  routes
})

export default router
