<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" persistent max-width="600px">
      <v-card>
        <v-card-title>Update Bonus</v-card-title>
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-text-field
              v-model="defaultBonus.bezeichnung"
              label="Bezeichnung"
              :rules="[rules.required, rules.length(100)]"
              counter="100"
            ></v-text-field>
            <v-text-field
              v-model="defaultBonus.punkte"
              label="Punkte"
              type="number"
              :rules="[rules.required]"
            ></v-text-field>
            <v-switch v-model="defaultBonus.aktiv" :label="`Bonus aktiv`" color="green"></v-switch>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green" text @click="$emit('update:dialog', false)">Abbrechen</v-btn>
          <v-btn color="green" text @click="doUpdateBonus" :loading="isLoadingBoni">Speichern</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>
<script>
import { mapGetters } from "vuex";

export default {
  name: "UpdateBonusPopup",
  data() {
    return {
      valid: false,
      rules: {
        length: len => v =>
          (v || "").length <= len ||
          `Zu viele Zeichen, es dürfen höchstens ${len} sein`,
        required: v => !!v || "Dieses Feld ist verpflichtend"
      },
      defaultBonus: {
        bezeichnung: null,
        punkte: null,
        aktiv: false,
        locationId: null,
        id: null
      }
    };
  },
  props: {
    bonus: Object,
    dialog: Boolean
  },
  computed: mapGetters({
    isLoadingBoni: "bonuses/isLoadingActions"
  }),
  watch: {
    dialog() {
      if (this.dialog) {
        this.defaultBonus = {
          bezeichnung: this.bonus.bezeichnung,
          punkte: this.bonus.punkte,
          aktiv: this.bonus.aktiv,
          locationId: this.bonus.locationId,
          id: this.bonus.id
        };
      }
    },
    isLoadingBoni() {
      if (!this.isLoadingBoni) {
        this.$emit("update:dialog", false);
      }
    }
  },
  methods: {
    doUpdateBonus() {
      this.$refs.form.validate();

      if (this.valid) {
        this.$store.dispatch("bonuses/updateBonus", this.defaultBonus);
      }
    }
  }
};
</script>

<style>
</style>