<template>
  <div>
    <MglMap
      :style="`width: ${this.width}; height: ${this.height}; z-index=auto`"
      :accessToken="accessToken"
      :mapStyle.sync="mapStyleCurrent"
      @load="onMapLoad"
      @click="onClickMap"
    >
      <MglGeocoderControl
        ref="geocoder"
        position="top-right"
        v-if="mode == 'showAll' || mode == 'create'"
        :accessToken="accessToken"
        :input.sync="defaultInput"
      />
      <MglNavigationControl position="top-right" />
      <MglGeolocateControl position="top-right" v-if="mode == 'showAll'" @geolocate="geoLocate" />

      <!-- v-container is there to help the create mode, because if i want to create a new location a default marker at (0/0) 
      would be displayed, so with that it only displays the marker when the user sets one on the map-->
      <v-container v-if="mode != 'create' || isMarkerSet == true">
        <MglMarker
          v-for="location in locations"
          :key="location.id"
          :coordinates="[location.koordinaten.lon, location.koordinaten.lat]"
          :draggable="mode == 'update'"
          :color="colorMarker"
          @dragend="dragend"
        >
          <MglPopup v-if="mode === 'showAll'">
            <v-card flat elevation="0">
              <v-img
                class="white--text align-end"
                gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
                height="200px"
                :src="'https://picsum.photos/510/300?random'"
                aspect-ratio="2"
              >
                <v-card-title>{{ location.bezeichnung }}</v-card-title>
              </v-img>
              <v-card-actions>
                <v-btn
                  :to="{ name: 'Location anzeigen', params: { id: location.id }}"
                  text
                >Mehr Details</v-btn>
              </v-card-actions>
            </v-card>
          </MglPopup>
        </MglMarker>
      </v-container>
    </MglMap>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

import Mapbox from "mapbox-gl";
import MglGeocoderControl from "vue-mapbox-geocoder";
import {
  MglMap,
  MglNavigationControl,
  MglGeolocateControl,
  MglMarker,
  MglPopup
} from "vue-mapbox";

export default {
  name: "Map",
  components: {
    MglMap,
    MglNavigationControl,
    MglGeolocateControl,
    MglMarker,
    MglPopup,
    MglGeocoderControl
  },
  data() {
    return {
      accessToken: process.env.VUE_APP_MAPKEY,
      mapStyleDay: "mapbox://styles/mkleinegger/ck4movtpa21z71ctalv5mt7e5",
      mapStyleNight: "mapbox://styles/mkleinegger/ck7gqe9oi3ren1imlk29tq0ws",
      mapStyleCurrent: this.mapStyleDay,
      colorMarker: "blue",
      isGeolocateOn: false,
      defaultInput: "",
      valid: true,
      isMarkerSet: false
    };
  },
  props: {
    width: String,
    height: String,
    locations: Array,
    mode: String,
    center: Array
  },
  computed: mapGetters({
    darkMode: "application/isDarkModeOn"
  }),
  created() {
    this.mapbox = Mapbox;
    this.mapStyleCurrent = this.darkMode
      ? this.mapStyleNight
      : this.mapStyleDay;

    this.colorMarker = this.darkMode ? "green" : "blue";
  },
  watch: {
    darkMode() {
      this.mapStyleCurrent = this.darkMode
        ? this.mapStyleNight
        : this.mapStyleDay;

      this.colorMarker = this.darkMode ? "green" : "blue";
    }
  },
  methods: {
    async onMapLoad(event) {
      const asyncActions = event.component.actions;

      const newParams = await asyncActions.flyTo({
        center: this.center,
        zoom: 12,
        speed: 1
      });
    },
    validateForCreate() {
      //check if the Marker has been set, because when the marker has been set
      //you know that the map has now a marker for the create mode so it is OK.
      this.valid = this.isMarkerSet == true ? true : false;
    },
    onClickMap(event) {
      if (this.mode === "create") {
        this.isMarkerSet = true;

        this.locations[0].koordinaten.lat = event.mapboxEvent.lngLat.lat;
        this.locations[0].koordinaten.lon = event.mapboxEvent.lngLat.lng;
      }
    },
    dragend(event) {
      if (this.mode === "update") {
        this.locations[0].koordinaten.lat = event.marker._lngLat.lat;
        this.locations[0].koordinaten.lon = event.marker._lngLat.lng;
      }
    },
    geoLocate(event) {
      //if the isGeolocateOn is true you know the location circle an the nearest locations are shown.
      //if it's false you know all location are shown
      this.isGeolocateOn = !this.isGeolocateOn;

      if (event.map.getSource("polygon")) {
        event.map.removeLayer("polygon");
        event.map.removeSource("polygon");
      }

      //this.emit --> triggered method that is saved under event in MapView.vue with the data passed through
      if (!this.isGeolocateOn) {
        this.$emit("event", null);
      } else {
        this.$emit("event", {
          distanz: 3000,
          x: event.mapboxEvent.coords.latitude,
          y: event.mapboxEvent.coords.longitude,
          loadBranchen: true
        });

        event.map.addSource(
          "polygon",
          this.createGeoJSONCircle(event.mapboxEvent.coords, 3)
        );

        event.map.addLayer({
          id: "polygon",
          type: "fill",
          source: "polygon",
          layout: {},
          paint: {
            "fill-color": "blue",
            "fill-opacity": 0.2
          }
        });
      }
    },
    createGeoJSONCircle(center, radiusInKm, points) {
      if (!points) points = 64;

      var coords = {
        latitude: center.latitude,
        longitude: center.longitude
      };

      var km = radiusInKm;

      var ret = [];
      var distanceX =
        km / (111.32 * Math.cos((coords.latitude * Math.PI) / 180));
      var distanceY = km / 110.574;

      var theta, x, y;
      for (var i = 0; i < points; i++) {
        theta = (i / points) * (2 * Math.PI);
        x = distanceX * Math.cos(theta);
        y = distanceY * Math.sin(theta);

        ret.push([coords.longitude + x, coords.latitude + y]);
      }
      ret.push(ret[0]);

      return {
        type: "geojson",
        data: {
          type: "FeatureCollection",
          features: [
            {
              type: "Feature",
              geometry: {
                type: "Polygon",
                coordinates: [ret]
              }
            }
          ]
        }
      };
    }
  }
};
</script>

<style>
.mapboxgl-popup {
  min-width: 300px;
  min-height: 500px;
}
.mapboxgl-popup-anchor-left {
  margin-left: 10px;
}
.mapboxgl-popup-anchor-right {
  margin-left: -10px;
}
.mapboxgl-popup-content {
  padding: 0%;
}
.mapboxgl-popup-close-button {
  display: none;
}
</style>