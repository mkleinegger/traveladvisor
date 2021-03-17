<template>
  <div>
    <v-hover v-slot:default="{ hover }">
      <v-card :elevation="hover ? 12 : 4" class="mx-auto mt-5" width="800">
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-text-field
              v-model="user.firstname"
              label="Vorname"
              :rules="[rules.required]"
              color="green"
            />
            <v-text-field
              v-model="user.lastname"
              label="Nachname"
              :rules="[rules.required]"
              color="green"
            />
            <v-text-field
              v-model="user.email"
              label="Email"
              :rules="[rules.required, rules.emailFormat]"
              color="green"
            />
            <v-text-field
              v-model="user.password"
              type="password"
              label="Password"
              :rules="[rules.required, rules.length(6)]"
              color="green"
            />
            <v-radio-group v-model="user.type" :mandatory="false" row>
              <v-radio label="Besucher" value="besucher" color="green"></v-radio>
              <v-radio label="Besitzer" value="besitzer" color="green"></v-radio>
            </v-radio-group>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green" text @click="$router.go(-1)">Abbrechen</v-btn>
          <v-btn color="green" text @click="submit()" :loading="isLoading">Registrieren</v-btn>
        </v-card-actions>
      </v-card>
    </v-hover>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "Register",
  data() {
    return {
      valid: false,
      user: {
        firstname: "",
        lastname: "",
        email: "",
        password: "",
        id: "",
        type: "besitzer"
      },
      rules: {
        length: len => v =>
          (v || "").length >= len ||
          `Es muss mindestens ${len} Zeichen lang sein`,
        required: v => !!v || "Dieses Feld ist verpflichtend",
        emailFormat: v => /.+@.+/.test(v) || "E-mail muss gültig sein"
      }
    };
  },
  computed: {
    ...mapGetters({
      isLoading: "users/isLoading",
      errorUser: "users/error"
    })
  },
  watch: {
    isLoading() {
      if (!this.isLoading && this.errorUser == null) {
        this.$router.push({ name: "Account" });
      }
    }
  },
  methods: {
    submit() {
      this.$refs.form.validate();

      if (this.valid) {
        this.$store.dispatch("users/register", this.user);
      }
    }
  }
};
</script>

<style>
</style>