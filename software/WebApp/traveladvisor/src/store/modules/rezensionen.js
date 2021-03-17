import axios from 'axios';

const baseURL = process.env.VUE_APP_API_URL;

const state = {
    rezensionen: [],
    isLoading: false, //for normal loading
    isLoadingActions: false, //For CRUD operations
    error: null
};

const getters = {
    allRezensionen: state => state.rezensionen,
    isLoading: state => state.isLoading,
    isLoadingActions: state => state.isLoadingActions,
    error: state => state.error
};

const actions = {
    loadRezensionen({ commit }, id) {
        commit('setRezensionen', []);
        commit('setIsLoading', true);
        commit('setError', null)

        axios.get(baseURL + `/TravelAdvisor_WebServices/TravelGuide/locationDetail/${id}/rezensionen`)
            .then(response => commit('setRezensionen', response.data))
            .catch(err => commit('setError', err + ' - Rezensionen konnten nicht geladen werden'))
            .finally(() => commit('setIsLoading', false));
    },
    addRezension({ commit }, rezension) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.post(baseURL + `/TravelAdvisor_WebServices/TravelGuide/rezensionenDetail`, rezension)
            .then(response => commit('addRezension', response.data))
            .catch(err => commit('setError', err + ' - Rezension konnten nicht geaddet werden'))
            .finally(() => commit('setIsLoadingActions', false));
    },
    updateRezension({ commit }, rezension) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.put(baseURL + `/TravelAdvisor_WebServices/TravelGuide/rezensionenDetail/${rezension.id}`, rezension)
            .then(response => commit('updateRezension', response.data))
            .catch(err => commit('setError', err + ' - Rezension konnten nicht upgedatet werden'))
            .finally(() => commit('setIsLoadingActions', false));
    },
    deleteRezension({ commit }, id) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.delete(baseURL + `/TravelAdvisor_WebServices/TravelGuide/rezensionenDetail/${id}`)
            .then(response => commit('deleteRezension', id))
            .catch(err => commit('setError', err + ' - Rezension konnten nicht gelöscht werden'))
            .finally(() => commit('setIsLoadingActions', false));
    }
};

const mutations = {
    setRezensionen: (state, rezensionen) => state.rezensionen = rezensionen,
    addRezension: (state, rezension) => state.rezensionen.push(rezension),
    updateRezension: (state, rezension) => {
        const index = state.rezensionen.findIndex(r => r.id === rezension.id);
        if (index !== -1) state.rezensionen.splice(index, 1, rezension);
    },
    deleteRezension: (state, id) => state.rezensionen = state.rezensionen.filter(rezension => rezension.id !== id),
    setIsLoading: (state, updateLoading) => state.isLoading = updateLoading,
    setIsLoadingActions: (state, updateLoading) => state.isLoadingActions = updateLoading,
    setError: (state, err) => state.error = err
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
};