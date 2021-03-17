import axios from 'axios';

const baseURL = process.env.VUE_APP_API_URL;

const state = {
    bonuses: [],
    isLoading: false,
    isLoadingActions: false,
    error: null
};

const getters = {
    allBonuses: state => state.bonuses,
    allActivatedBoni: state => state.bonuses.filter(bonus => bonus.aktiv === true),
    isLoading: state => state.isLoading,
    isLoadingActions: state => state.isLoadingActions,
    error: state => state.error
};

const actions = {
    loadBonuses({ commit }, id) {
        commit('setBonuses', []);
        commit('setIsLoading', true);
        commit('setError', null)

        axios.get(baseURL + `/TravelAdvisor_WebServices/TravelGuide/locationDetail/${id}/praemien`)
            .then(response => commit('setBonuses', response.data))
            .catch(err => commit('setError', err + ' - Boni konnten nicht geladen werden'))
            .finally(() => commit('setIsLoading', false));
    },
    addBonus({ commit }, bonus) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.post(baseURL + `/TravelAdvisor_WebServices/TravelGuide/praemienDetail`, bonus)
            .then(response => commit('addBonus', response.data))
            .catch(err => commit('setError', err + ' - Bonus konnten nicht erstellt werden'))
            .finally(() => commit('setIsLoadingActions', false));
    },
    updateBonus({ commit }, bonus) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.put(baseURL + `/TravelAdvisor_WebServices/TravelGuide/praemienDetail/${bonus.id}`, bonus)
            .then(response => commit('updateBonus', response.data))
            .catch(err => commit('setError', err + ' - Bonus konnten nicht aktualisiert werden'))
            .finally(() => commit('setIsLoadingActions', false));
    },
    deleteBonus({ commit }, id) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.delete(baseURL + `/TravelAdvisor_WebServices/TravelGuide/praemienDetail/${id}`)
            .then(response => commit('deleteBonus', id))
            .catch(err => commit('setError', err + ' - Bonus konnten nicht gelöscht werden'))
            .finally(() => commit('setIsLoadingActions', false));
    }
};

const mutations = {
    setBonuses: (state, bonuses) => state.bonuses = bonuses,
    addBonus: (state, bonus) => state.bonuses.push(bonus),
    updateBonus: (state, bonus) => {
        const index = state.bonuses.findIndex(b => b.id === bonus.id);
        if (index !== -1) state.bonuses.splice(index, 1, bonus);
    },
    deleteBonus: (state, id) => state.bonuses = state.bonuses.filter(bonus => bonus.id !== id),
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