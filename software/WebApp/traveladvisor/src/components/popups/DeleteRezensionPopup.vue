<template>
  <v-dialog v-model="dialog" max-width="300" persistent>
    <v-card>
      <v-card-title class="headline">Rezension löschen</v-card-title>
      <v-card-text>Wollen Sie die Rezension wirklich löschen</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="green" text @click="$emit('update:dialog', false)">Cancel</v-btn>
        <v-btn
          color="green"
          text
          @click="deleteRezension(rezension.id)"
          :loading="isLoadingRezension"
        >OK</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "DeleteRezensionPopup",
  props: {
    rezension: Object,
    dialog: Boolean
  },
  computed: mapGetters({
    isLoadingRezension: "rezensionen/isLoadingActions"
  }),
  methods: {
    deleteRezension(id) {
      this.$store.dispatch("rezensionen/deleteRezension", id);
    }
  }
};
</script>

<style>
</style>