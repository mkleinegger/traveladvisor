<template>
  <div class="locations">
    <v-container fluid>
      <v-row>
        <v-col cols="12">
          <v-btn :to="{ name: 'Location erstellen' }" class="green" dark>Location hinzufügen</v-btn>
        </v-col>
      </v-row>
      <v-row v-if="isLoadingLocations">
        <v-col v-for="i in 8" :key="i" lg="3" md="4" sm="6">
          <v-skeleton-loader transition="fade-transition" type="card" />
          <v-skeleton-loader transition="fade-transition" type="actions" />
        </v-col>
      </v-row>
      <v-row v-else-if="allLocations.length > 0">
        <v-col v-for="location in allLocations" :key="location.id" lg="3" md="4" sm="6">
          <LocationListItem :location="location" />
        </v-col>
      </v-row>
      <v-row v-else justify="center">
        <v-label>Keine locations vorhanden</v-label>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import firebase from 'firebase';
import LocationListItem from "@/components/listItems/LocationListItem";

export default {
  name: "Locations",
  components: {
    LocationListItem
  },
  computed: mapGetters({
    allLocations: "locations/allLocations",
    isLoadingLocations: "locations/isLoading"
  }),
  created() {
    this.$store.dispatch("locations/loadLocations", {
      besitzer: firebase.auth().currentUser.uid,
      loadBranchen: true
    });
  }
};
</script>

<style scoped>
.locations {
  margin-left: 20px;
  padding-right: 20px;
}

.v-label {
  font-size: 30px;
}
</style>