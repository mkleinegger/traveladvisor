import axios from 'axios';
import firebase from 'firebase'

const baseURL = process.env.VUE_APP_API_URL;

const state = {
  user: null,
  points: [],
  isLoading: false,
  error: null
}

const getters = {
  user: state => state.user,
  isLoading: state => state.isLoading,
  points: state => state.points,
  error: state => state.error
}

const actions = {
  register({ commit, dispatch }, user) {
    commit('setIsLoading', true);
    commit('setError', null)

    firebase
      .auth()
      .createUserWithEmailAndPassword(user.email, user.password)
      .then(data => {
        user.id = data.user.uid;
        return user;
      })
      .then(user => {
        dispatch('registerUser', user);
        return user;
      })
      .then(user => {
        return firebase.firestore().collection("users")
          .doc(user.id)
          .set({
            vorname: user.firstname,
            email: user.email,
            nachname: user.lastname,
            typ: user.type,
            uid: user.id
          })
      })
      .catch(err => commit('setError', err + ' - User konnte bei firebase nicht registriert werden'))
      .finally(() => commit('setIsLoading', false));
  },
  registerUser({ commit }, user) {
    commit('setError', null);
    let type = (user.typ == 'besitzer') ? "besitzerDetail" : "besucherDetail";

    axios.post(baseURL + `/TravelAdvisor_WebServices/TravelGuide/${type}`, user)
      .catch(err => commit('setError', err + ' - User konnte bei oracle nicht registriert werden'));
  },
  fetchUser({ commit }, user) {
    commit('setError', null);
    let docRef = firebase.firestore().collection("users").doc(user.uid);

    docRef
      .get()
      .then(function (doc) {
        if (doc.exists) {
          commit("setUser", {
            displayName: doc.data().vorname,
            email: doc.data().email,
            id: doc.data().uid,
            vorname: doc.data().vorname,
            nachname: doc.data().nachname,
            typ: doc.data().typ
          })

          localStorage.setItem('typ', doc.data().typ);
        } else {
          commit('setError', 'Document konnte bei firestore nicht gefunden werden');
        }
      })
      .catch(err => commit('setError', err + ' - User konnte bei firestore nicht geladen werden'));
  },
  signIn({ commit }, user) {
    commit('setIsLoading', true);
    commit('setError', null);

    firebase.auth().setPersistence(firebase.auth.Auth.Persistence.SESSION)
      .then(() => { return firebase.auth().signInWithEmailAndPassword(user.email, user.password); })
      .catch(err => commit('setError', err + ' - User konnte bei firebase nicht angemeldet werden'))
      .finally(() => commit('setIsLoading', false));
  },
  signOut({ commit }) {
    commit('setError', null);

    firebase
      .auth()
      .signOut()
      .then(() => commit("setUser", null))
      .catch(err => commit('setError', err + ' - User konnte bei firebase nicht abgemeldet werden'));
  },
  resetPasswortViaEmail({ commit }, email) {
    commit('setError', null);

    firebase
      .auth()
      .sendPasswordResetEmail(email)
      .catch(err => commit('setError', err + ' - Email konnte nicht gesendet werden'));
  },
  updateUser({ commit }, userData) {
    commit('setError', null);
    commit('setIsLoading', true);

    let db = firebase.firestore();
    let user = firebase.auth().currentUser;

    db.collection("users")
      .doc(user.uid)
      .update({
        vorname: userData.vorname,
        nachname: userData.nachname
      })
      .catch(err => commit('setError', err + ' - Userdaten konnte nicht upgedatet werden'))
      .finally(() => commit('setIsLoading', false));
  },
  getUserPoints({ commit }, userId) {
    commit('setError', null);
    commit('setIsLoading', true);
    commit('setPoints', []);

    axios.get(baseURL + `/TravelAdvisor_WebServices/TravelGuide/besucherDetail/verlauf/${userId}`)
      .then(response => commit('setPoints', response.data.aktionen))
      .catch(err => commit('setError', err + ' - Userpunkte konnten nicht geladen werden'))
      .finally(() => commit('setIsLoading', false));
  }
}

const mutations = {
  setUser: (state, data) => state.user = data,
  setPoints: (state, data) => state.points = data,
  setIsLoading: (state, updateLoading) => state.isLoading = updateLoading,
  setError: (state, err) => state.error = err
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};