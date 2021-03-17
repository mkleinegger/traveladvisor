<template>
  <div>
    <v-row>
      <v-col cols="12">
        <v-btn class="green" dark @click="$router.go(-1)">
          <v-icon left>arrow_back</v-icon>Zurück
        </v-btn>
        <v-btn class="green ml-1" dark @click="dialog = !dialog">Rezensionen</v-btn>
        <RezensionenPopup :dialog.sync="dialog" :location="getSelectedLocation" />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="4">
        <LocationDetail
          :selectedLocation.sync="(getSelectedLocation === undefined) ? defaultLocation : getSelectedLocation" :readonly="true"
        />
      </v-col>
      <v-col cols="8">
        <v-hover v-slot:default="{ hover }">
          <v-card :elevation="hover ? 12 : 4">
            <Map
              :width="'100%'"
              :height="'606px'"
              :locations="(getSelectedLocation === undefined) ?  new Array(defaultLocation) : new Array(getSelectedLocation)"
              :center.sync="getCoordsFromSelected"
              :mode="mode"
            />
          </v-card>
        </v-hover>
      </v-col>
    </v-row>
    <v-row class="mt-4 ml-0" v-if="isLoadingBoni || allBonuses.length > 0">
      <v-label>Prämien:</v-label>
    </v-row>
    <v-row v-if="isLoadingBoni">
      <v-col v-for="i in 4" :key="i" lg="3" md="4" sm="6">
        <v-skeleton-loader transition="fade-transition" type="card" />
      </v-col>
    </v-row>
    <v-row v-else>
      <v-col v-for="bonus in allBonuses" :key="bonus.id" lg="3" md="4" sm="6">
        <BonusListItem :bonus="bonus" :edit="false" v-if="bonus.aktiv" />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

import Map from "@/components/Map";
import LocationDetail from "@/components/LocationDetail";
import RezensionenPopup from "@/components/popups/ShowRezensionenPopup";
import BonusListItem from "@/components/listItems/BonusListItem";

export default {
  name: "LocationDetails",
  components: {
    Map,
    LocationDetail,
    BonusListItem,
    RezensionenPopup
  },
  data() {
    return {
      dialog: false,
      mode: "showDetails",
      defaultLocation: {
        bezeichnung: "",
        beschreibung: "",
        aktiv: false,
        punkte: 0,
        branchen: [],
        koordinaten: { x: 0, y: 0 }
      }
    };
  },
  computed: {
    ...mapGetters({
      allLocations: "locations/allLocations",
      allBonuses: "bonuses/allActivatedBoni",
      isLoadingBoni: "bonuses/isLoading"
    }),
    getSelectedLocation() {
      return this.allLocations.filter(
        location => location.id == this.$route.params.id
      )[0];
    },
    getCoordsFromSelected() {
      return this.allLocations.length !== 0
        ? [
            this.getSelectedLocation.koordinaten.lon,
            this.getSelectedLocation.koordinaten.lat
          ]
        : [0, 0];
    }
  },
  created() {
    if (this.allLocations.length === 0) {
      this.$store.dispatch("locations/loadLocationById", this.$route.params.id);
    }
    this.$store.dispatch("bonuses/loadBonuses", this.$route.params.id);
  }
};
</script>

<style >
.v-label {
  font-size: 20px;
}
</style>
