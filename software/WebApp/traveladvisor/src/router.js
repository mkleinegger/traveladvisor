import Vue from 'vue'
import Router from 'vue-router'
import firebase from 'firebase';

import MapView from '@/views/MapView.vue'
import Account from '@/views/Account.vue'
import Locations from '@/views/Locations.vue'
import UpdateLocation from '@/views/UpdateLocation.vue'
import CreateLocation from '@/views/CreateLocation.vue'
import ShowLocation from '@/views/ShowLocation.vue'
import PageNotFound from '@/views/PageNotFound.vue'
import Bonuses from '@/views/Bonuses.vue'
import Register from '@/views/Register'
import Login from '@/views/Login'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Map',
      component: MapView
    },
    {
      path: '/account',
      name: 'Account',
      component: Account,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/locations',
      name: 'Locations',
      component: Locations,
      meta: {
        requiresAuth: true,
        requiresTypBesitzer: true
      }
    },
    {
      path: '/locations/view/:id',
      name: 'Location anzeigen',
      component: ShowLocation
    },
    {
      path: '/locations/update/:id',
      name: 'Location aktualisieren',
      component: UpdateLocation,
      meta: {
        requiresAuth: true,
        requiresTypBesitzer: true
      }
    },
    {
      path: '/locations/create',
      name: 'Location erstellen',
      component: CreateLocation,
      meta: {
        requiresAuth: true,
        requiresTypBesitzer: true
      }
    },
    {
      path: '/bonuses',
      name: 'Bonuses',
      component: Bonuses,
      meta: {
        requiresAuth: true,
        requiresTypBesitzer: true
      }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: "*",
      component: PageNotFound
    }
  ]
})

router.beforeEach((to, from, next) => {
  const currentUser = firebase.auth().currentUser;
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresTypBesitzer = to.matched.some(record => record.meta.requiresTypBesitzer);
  const typ = (currentUser != null) ? localStorage.getItem('typ') : null;

  if (requiresAuth && !currentUser) next('/login');
  else if (requiresTypBesitzer && currentUser && typ != 'besitzer') next('*')
  else next();
})

export default router