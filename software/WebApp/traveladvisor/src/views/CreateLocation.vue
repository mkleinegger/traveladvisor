<template>
  <div>
    <v-row>
      <v-col cols="12">
        <v-btn class="green" dark @click="$router.go(-1)">
          <v-icon left>arrow_back</v-icon>Zurück
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="4">
        <LocationDetail ref="details" :selectedLocation.sync="defaultLocation" :readonly="false" />
      </v-col>
      <v-col cols="8">
        <v-hover v-slot:default="{ hover }">
          <v-card :elevation="hover ? 12 : 4">
            <Map
              ref="map"
              :width="'100%'"
              :height="'676px'"
              :locations="new Array(defaultLocation)"
              :center="[13.844549, 46.614073]"
              :mode.sync="mode"
            />
          </v-card>
        </v-hover>
        <p v-show="!valid" class="red--text mt-1 mb-0">Es muss eine Location ausgewählt sein!</p>
      </v-col>
    </v-row>
    <v-row class="buttons">
      <v-col cols="12">
        <v-btn class="green mr-1" dark @click="$router.go(-1)">Cancel</v-btn>
        <v-btn
          class="green"
          dark
          @click="doAddLocation()"
          :loading="isLoadingActions"
        >Location hinzufügen</v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import firebase from "firebase";
import Map from "@/components/Map";
import LocationDetail from "@/components/LocationDetail";

export default {
  name: "CreateLocation",
  components: {
    Map,
    LocationDetail
  },
  data() {
    return {
      defaultLocation: {
        bezeichnung: "",
        beschreibung: "",
        aktiv: false,
        punkte: 0,
        branchen: [],
        besitzer: { id: "" },
        koordinaten: { lon: 0, lat: 0 }
      },
      mode: "create",
      isButtonPressed: false, //because the isLoading could be change somewhere else
      valid: true //for the <p>-errormessage of the map
    };
  },
  computed: mapGetters({
    isLoadingActions: "locations/isLoadingActions"
  }),
  watch: {
    isLoadingActions() {
      if (!this.isLoadingActions && this.isButtonPressed) {
        this.isButtonPressed = false;
        this.$router.push({ name: "Locations" });
      }
    }
  },
  methods: {
    doAddLocation() {
      this.$refs.details.validate();
      this.$refs.map.validateForCreate();
      this.valid = this.$refs.map.valid;

      if (this.$refs.details.valid && this.$refs.map.valid) {
        this.isButtonPressed = true;

        this.defaultLocation.besitzer.id = firebase.auth().currentUser.uid;
        this.$store.dispatch("locations/addLocation", this.defaultLocation);
      }
    }
  }
};
</script>

<style>
.buttons {
  float: right;
}
.v-expansion-panel {
  box-shadow: none;
}
</style>
