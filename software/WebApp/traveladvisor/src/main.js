import Vue from 'vue';
import App from './App.vue';
import store from './store';
import vuetify from './plugins/vuetify';
import router from './router';
import firebase from 'firebase';
import moment from 'moment';
import './firebase';
import 'material-design-icons-iconfont/dist/material-design-icons.css';

Vue.config.productionTip = false;
let app;

Vue.filter('shorterText', (value) => {
  if (value.length <= 51) return value;
  return value.slice(0, 48) + "...";
});

Vue.filter('formatDate', (value) => {
  if (value) {
    let test = Math.round(new Date(value).getTime())
    return moment((test)).format('DD.MM.YYYY');
  }
})

firebase.auth().onAuthStateChanged(user => {
  if (user) {
    store.dispatch("users/fetchUser", user);
  } else {
    store.commit("users/setUser", null);
    localStorage.removeItem('typ');
  }

  if (!app) {
    app = new Vue({
      vuetify,
      router,
      store,
      render: h => h(App)
    }).$mount('#app')
  }
})
