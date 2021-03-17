<template>
  <v-dialog v-model="dialog" max-width="300" persistent>
    <v-card>
      <v-card-title class="headline">Bonus löschen</v-card-title>
      <v-card-text>Wollen Sie den Bonus "{{ bonus.bezeichnung }}" wirklich löschen</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="green" text @click="$emit('update:dialog', false)">Abbrechen</v-btn>
        <v-btn color="green" text @click="deleteBonus(bonus.id)" :loading="isLoadingBoni">OK</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "BonusDeletePopup",
  computed: mapGetters({
    isLoadingBoni: "bonuses/isLoadingActions"
  }),
  props: {
    bonus: Object,
    dialog: Boolean
  },
  methods: {
    deleteBonus(id) {
      this.$store.dispatch("bonuses/deleteBonus", id);
    }
  }
};
</script>

<style>
</style>