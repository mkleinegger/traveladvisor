<template>
  <v-hover v-slot:default="{ hover }">
    <v-card :class="{ 'on-hover': hover }" :elevation="hover ? 12 : 4">
      <v-img
        class="white--text align-end"
        gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
        height="200px"
        :src="'https://picsum.photos/510/300?random'"
        aspect-ratio="2"
      >
        <v-card-title>{{ location.bezeichnung }}</v-card-title>
      </v-img>
      <v-card-text>{{ location.beschreibung | shorterText }}</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn icon :to="{ name: 'Location anzeigen', params: { id: location.id }}">
          <v-icon>expand_more</v-icon>
        </v-btn>
        <v-btn icon :to="{ name: 'Location aktualisieren', params: { id: location.id }}">
          <v-icon>edit</v-icon>
        </v-btn>
        <v-btn icon @click="dialog = true">
          <v-icon>delete</v-icon>
        </v-btn>
        <LocationDeletePopup :dialog.sync="dialog" :location="location" />
      </v-card-actions>
    </v-card>
  </v-hover>
</template>

<script>
import LocationDeletePopup from "@/components/popups/DeleteLocationPopup.vue";

export default {
  components: {
    LocationDeletePopup
  },
  data() {
    return {
      dialog: false
    };
  },
  props: {
    location: Object
  }
};
</script>

<style scoped>
.v-card {
  transition: opacity 0.4s ease-in-out;
}

.v-card:not(.on-hover) {
  opacity: 0.8;
}
</style>
