import axios from 'axios';

const baseURL = process.env.VUE_APP_API_URL;

const state = {
    branchen: [],
    isLoading: false,
    error: null
};

const getters = {
    allBranchen: state => state.branchen,
    isLoading: state => state.isLoading,
    error: state => state.error
};

const actions = {
    loadBranchen({ commit }) {
        commit('setBranchen', []);
        commit('setIsLoading', true);
        commit('setError', null)

        axios.get(baseURL + `/TravelAdvisor_WebServices/TravelGuide/brancheList`)
            .then(response => {
                commit('setBranchen', response.data);
            })
            .catch(err => commit('setError', err + ' - Branchen konnten nicht geladen werden'))
            .finally(() => commit('setIsLoading', false));
    }
};

const mutations = {
    setBranchen: (state, branchen) => state.branchen = branchen,
    setIsLoading: (state, updateLoading) => state.isLoading = updateLoading,
    setError: (state, err) => state.error = err
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
};