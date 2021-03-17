<template>
  <div class="home">
    <v-hover v-slot:default="{ hover }">
      <v-card :elevation="hover ? 12 : 4">
        <Map
          width="100%"
          height="87vh"
          :locations.sync="allActivatedLocations"
          :center="[13.844549, 46.614073]"
          :mode="mode"
          @event="searchLocations"
        />
        <v-progress-linear
          :active="isLoadingLocations"
          :indeterminate="isLoadingLocations"
          color="green"
        />
      </v-card>
    </v-hover>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

import Map from "@/components/Map";

export default {
  name: "home",
  components: {
    Map
  },
  data() {
    return {
      mode: "showAll"
    };
  },
  computed: {
    ...mapGetters({
      allActivatedLocations: "locations/allActivatedLocations",
      isLoadingLocations: "locations/isLoading"
    })
  },
  methods: {
    searchLocations(data) {
      this.$store.dispatch("locations/loadLocations", data);
    }
  },
  created() {
    this.$store.dispatch("locations/loadLocations");
  }
};
</script>