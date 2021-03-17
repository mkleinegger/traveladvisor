<template>
  <v-dialog v-model="dialog" max-width="1200" persistent>
    <v-card>
      <v-card-title>
        Punkteverlauf:
        <v-spacer></v-spacer>
        <v-btn icon @click="$emit('update:dialog', false)">
          <v-icon>close</v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-timeline v-if="points.length != 0">
          <v-timeline-item v-for="point in points" :key="point" color="green" small>
            <template v-slot:opposite>
              <span :class="`headline font-weight-bold green--text`" v-text="point.location"></span>
            </template>
            <div class="py-4">
              <h3 :class="`headline font-weight-light mb-4`">{{ point.zeitpunkt | formatDate}}</h3>
              <div style="font-size: 17px;">{{ point.beschreibung }}</div>
              <div style="font-size: 17px;">Punkte: {{ point.punkte }}</div>
            </div>
          </v-timeline-item>
        </v-timeline>
        <v-label v-else>Keine Punkte gesammelt oder ausgegeben</v-label>
      </v-card-text>
      <v-progress-linear :active="isLoading" :indeterminate="isLoading" color="green" />
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from "vuex";
import firebase from "firebase";

export default {
  name: "ShowPointsPopup",
  props: {
    dialog: Boolean
  },
  computed: mapGetters({
    points: "users/points",
    isLoading: "users/isLoading"
  }),
  created() {
    this.$store.dispatch(
      "users/getUserPoints",
      firebase.auth().currentUser.uid
    );
  }
};
</script>

<style>
</style>