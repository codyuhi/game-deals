import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import Home from '../views/Home.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/SearchResults/:searchString',
    name: 'SearchResults',
    component: () => import('../views/SearchResults.vue')
  },
  {
    path: '/Games/:gameid',
    name: 'Games',
    props: true,
    component: () => import('../views/GameDetails.vue')
  },
  {
    path: '/Deals/:dealid',
    name: 'Deals',
    component: () => import('../views/DealDetails.vue')
  },
  {
    path: '/Favorites',
    name: 'Favorites',
    component: () => import('../views/Favorites.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
