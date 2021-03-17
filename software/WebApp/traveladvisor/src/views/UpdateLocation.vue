<template>
  <div>
    <v-row>
      <v-col cols="12">
        <v-btn class="green" dark @click="$router.go(-1)">
          <v-icon left>arrow_back</v-icon>Zurück
        </v-btn>
        <v-btn class="green ml-1" dark @click="createQRCode">QR-Code erzeugen</v-btn>
        <div id="qrcode" v-show="false"></div>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="4">
        <LocationDetail
          ref="details"
          :selectedLocation.sync="(getSelectedLocation === undefined) ? defaultLocation : getSelectedLocation"
          :readonly="false"
        />
      </v-col>
      <v-col cols="8">
        <v-hover v-slot:default="{ hover }">
          <v-card :elevation="hover ? 12 : 4">
            <Map
              :width="'100%'"
              :height="'676px'"
              :locations="(getSelectedLocation === undefined) ?  new Array(defaultLocation) : new Array(getSelectedLocation)"
              :center.sync="getCoordsFromSelected"
              :mode="mode"
            />
          </v-card>
        </v-hover>
      </v-col>
    </v-row>
    <v-row class="buttons">
      <v-col cols="12">
        <v-btn class="green mr-1" dark @click="$router.go(-1)">Cancel</v-btn>
        <v-btn
          class="green"
          dark
          @click="doUpdateLocation()"
          :loading="isLoadingLocations"
        >Location aktualisieren</v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import Map from "@/components/Map";
import LocationDetail from "@/components/LocationDetail";

export default {
  name: "UpdateLocation",
  components: {
    Map,
    LocationDetail
  },
  data() {
    return {
      mode: "update",
      isDoUpdateButtonPressed: false,
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
  watch: {
    isLoadingLocations() {
      if (!this.isLoadingLocations && this.isDoUpdateButtonPressed) {
        this.isDoUpdateButtonPressed = false;
        this.$router.push({ name: "Locations" });
      }
    }
  },
  methods: {
    doUpdateLocation() {
      this.$refs.details.validate();
      if (this.$refs.details.valid === true) {
        this.$store.dispatch(
          "locations/updateLocationById",
          this.getSelectedLocation
        );
        this.isDoUpdateButtonPressed = true;
      }
    },
    createQRCode() {
      var qrcode = new QRCode($("#qrcode")[0], this.getSelectedLocation.id);

      $(qrcode._el)
        .find("img")
        .on("load", function() {
          var element = $("<a id='toDelete'></a>")
            .attr("href", this.src)
            .attr("download", "qr_code.png");

          $("body").append(element);
          document.getElementById("toDelete").click();
          $("#toDelete").remove();
        });
    }
  },
  computed: {
    ...mapGetters({
      allLocations: "locations/allLocations",
      isLoadingLocations: "locations/isLoadingActions"
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
    if (this.allLocations.length == 0) {
      this.$store.dispatch("locations/loadLocationById", this.$route.params.id);
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
