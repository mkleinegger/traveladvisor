<template>
  <v-dialog v-model="dialog" max-width="800" persistent>
    <v-card>
      <v-card-title>Rezensionen erstellen</v-card-title>
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
        <v-btn color="green" text @click="saveRezension" :loading="isLoadingRezension">Speichern</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from "vuex";
import firebase from "firebase";

export default {
  name: "AddRezensionPopup",
  data() {
    return {
      defaultRezension: {
        locationid: "",
        besucherid: "",
        bewertung: 2.5,
        text: ""
      },
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
    location: Object,
    dialog: Boolean
  },
  computed: mapGetters({
    isLoadingRezension: "rezensionen/isLoadingActions"
  }),
  watch: {
    dialog() {
      if (this.dialog) {
        this.defaultRezension = {
          locationid: this.location.id,
          besucherid: firebase.auth().currentUser.uid,
          bewertung: 2.5,
          text: ""
        };

        if (this.$refs.form != undefined) {
          this.$refs.form.reset();
        }
      }
    },
    isLoadingRezension() {
      if (!this.isLoadingRezension) {
        this.$emit("update:dialog", false);
      }
    }
  },
  methods: {
    saveRezension() {
      this.$refs.form.validate();

      if (this.valid) {
        this.$store.dispatch("rezensionen/addRezension", this.defaultRezension);
      }
    }
  }
};
</script>

<style>
</style>