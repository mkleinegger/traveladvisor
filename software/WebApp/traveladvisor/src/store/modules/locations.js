import axios from 'axios';

const baseURL = process.env.VUE_APP_API_URL;

const state = {
    locations: [],
    isLoading: false, //for normal loading
    isLoadingActions: false, //For CRUD operations
    error: null
};

const getters = {
    allLocations: state => state.locations,
    allActivatedLocations: state => state.locations.filter(location => location.aktiv === true),
    isLoading: state => state.isLoading,
    isLoadingActions: state => state.isLoadingActions,
    error: state => state.error
};

const actions = {
    loadLocations({ commit }, searchParams) {
        commit('setLocations', []);
        commit('setIsLoading', true);
        commit('setError', null)

        axios.get(baseURL + '/TravelAdvisor_WebServices/TravelGuide/locationList', { params: searchParams })
            .then(response => commit('setLocations', response.data))
            .catch(err => commit('setError', err + ' - Locations konnten nicht geladen werden'))
            .finally(() => commit('setIsLoading', false));
    },
    loadLocationById({ commit }, id) {
        axios.get(baseURL + `/TravelAdvisor_WebServices/TravelGuide/locationDetail/${id}`)
            .then(response => commit('setLocations', new Array(response.data)))
            .catch(err => commit('setError', err + ' - Location konnten nicht geladen werden'));
    },
    addLocation({ commit }, location) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.post(baseURL + `/TravelAdvisor_WebServices/TravelGuide/locationDetail`, location)
            .then(response => commit('addLocation', response.data))
            .catch(err => commit('setError', err + ' - Location konnte nicht erstellt werden'))
            .finally(() => commit('setIsLoadingActions', false));
    },
    updateLocationById({ commit }, location) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.put(baseURL + `/TravelAdvisor_WebServices/TravelGuide/locationDetail/${location.id}`, location)
            .then(response => commit('updateLocation', response.data))
            .catch(err => commit('setError', err + " - Location konnte nicht aktualisiert werden"))
            .finally(() => commit('setIsLoadingActions', false));
    },
    deleteLocation({ commit }, id) {
        commit('setIsLoadingActions', true);
        commit('setError', null)

        axios.delete(baseURL + `/TravelAdvisor_WebServices/TravelGuide/locationDetail/${id}`)
            .then(response => commit('deleteLocation', id))
            .catch(err => commit('setError', err + ' - Location konnte nicht gelöscht werden'))
            .finally(() => commit('setIsLoadingActions', false));
    }
};

const mutations = {
    setLocations: (state, locations) => state.locations = locations,
    addLocation: (state, location) => state.locations.push(location),
    updateLocation: (state, location) => {
        const index = state.locations.findIndex(l => l.id === location.id);
        if (index !== -1) state.locations.splice(index, 1, location);
    },
    deleteLocation: (state, id) => state.locations = state.locations.filter(location => location.id !== id),
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