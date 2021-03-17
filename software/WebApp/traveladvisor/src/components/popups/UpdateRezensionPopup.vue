<template>
  <v-dialog v-model="dialog" max-width="800" persistent>
    <v-card>
      <v-card-title>Rezensionen updaten</v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid">
          Bewertung:
          <v-rating v-model="defaultRezension.bewertung" color="amber" dense></v-rating>
          <v-textarea
            v-model="defaultRezension.text"
            :rules="[rules.required, rules.length(400)]"
            label="Beschreibung"
            counter="400"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="green" text @click="$emit('update:dialog', false)">Abbrechen</v-btn>
        <v-btn
          color="green"
          text
          @click="updateRezension"
          :loading="isLoadingRezension"
        >Akutalisieren</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "UpdateRezensionPopup",
  data() {
    return {
      valid: false,
      rules: {
        length: len => v =>
          (v || "").length <= len ||
          `Zu viele Zeichen, es dürfen höchstens ${len} sein`,
        required: v => !!v || "Dieses Feld ist verpflichtend"
      },
      defaultRezension: {
        text: "",
        bewertung: 2.5,
        id: ""
      }
    };
  },
  props: {
    rezension: Object,
    dialog: Boolean
  },
  computed: mapGetters({
    isLoadingRezension: "rezensionen/isLoadingActions"
  }),
  methods: {
    updateRezension() {
      this.$refs.form.validate();

      if (this.valid) {
        this.$store.dispatch(
          "rezensionen/updateRezension",
          this.defaultRezension
        );
      }
    }
  },
  watch: {
    dialog() {
      if (this.dialog) {
        this.defaultRezension = {
          text: this.rezension.text,
          bewertung: this.rezension.bewertung,
          id: this.rezension.id
        };

        if (this.$refs.form != undefined) {
          this.$refs.form.resetValidation();
        }
      }
    },
    isLoadingRezension() {
      if (!this.isLoadingRezension) {
        this.$emit("update:dialog", false);
      }
    }
  }
};
</script>

<style>
</style>