<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" persistent max-width="600px">
      <v-card>
        <v-card-title>Add Bonus</v-card-title>
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-text-field
              v-model="bonus.bezeichnung"
              label="Bezeichnung"
              :rules="[rules.required, rules.length(100)]"
              counter="100"
            ></v-text-field>
            <v-text-field
              v-model="bonus.punkte"
              label="Punkte"
              type="number"
              :rules="[rules.required]"
            ></v-text-field>
            <v-switch v-model="bonus.aktiv" :label="`Bonus aktiv`" color="green"></v-switch>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green" text @click="$emit('update:dialog', false)">Abbrechen</v-btn>
          <v-btn color="green" text @click="doAddBonus(bonus)" :loading="isLoadingBoni">Speichern</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>
<script>
import { mapGetters } from "vuex";

export default {
  name: "AddBonusPopup",
  data() {
    return {
      valid: false,
      rules: {
        length: len => v =>
          (v || "").length <= len ||
          `Zu viele Zeichen, es dürfen höchstens ${len} sein`,
        required: v => !!v || "Dieses Feld ist verpflichtend"
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
  methods: {
    doAddBonus() {
      this.$refs.form.validate();

      if (this.valid) {
        this.$store.dispatch("bonuses/addBonus", this.bonus);
        this.$emit("update:dialog", false);
      }
    }
  },
  watch: {
    dialog() {
      if (this.dialog && this.$refs.form != undefined) {
        this.$refs.form.reset();
      }
    },
    isLoadingBoni() {
      if (!this.isLoadingBoni) {
        this.$emit("update:dialog", false);
      }
    }
  }
};
</script>

<style>
</style>