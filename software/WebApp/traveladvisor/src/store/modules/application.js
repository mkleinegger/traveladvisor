const state = {
    drawer: null,
    isDarkModeOn: false
};

const getters = {
    drawer: state => state.drawer,
    isDarkModeOn: state => state.isDarkModeOn
};

const actions = {
    setDrawer({ commit }, value) {
        commit("setDrawer", value);
    },
    setDarkMode({ commit }, value) {
        commit("setDarkMode", value);
    }
};

const mutations = {
    setDrawer: (state, value) => state.drawer = value,
    setDarkMode: (state, value) => state.isDarkModeOn = value
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
};