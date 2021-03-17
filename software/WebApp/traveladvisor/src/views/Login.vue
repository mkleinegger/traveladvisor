<template>
  <div>
    <v-hover v-slot:default="{ hover }">
      <v-card :elevation="hover ? 12 : 4" class="mx-auto mt-5" width="600">
        <v-card-text>
          <v-img
            :src="'https://image.shutterstock.com/image-vector/thin-line-user-icon-on-260nw-519039097.jpg' || 'https://static.vecteezy.com/system/resources/previews/000/575/331/non_2x/login-sign-icon-vector.jpg'"
            height="300"
            contain
          />
          <v-form ref="form" v-model="valid">
            <v-text-field
              prepend-icon="email"
              v-model="user.email"
              label="Email"
              name="email"
              :rules="[rules.required]"
              color="green"
            />
            <v-text-field
              prepend-icon="lock"
              v-model="user.password"
              label="Password"
              type="password"
              :rules="[rules.required]"
              color="green"
            />
          </v-form>
          <v-row :class="`d-flex justify-center`">
            Passwort vergessen?&nbsp;
            <a align="center" @click="resetPasswortViaEmail">Klick hier</a>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-row :class="`d-flex justify-center`">
            <v-btn color="green" dark text :to="{ name: 'Register' }">Registrieren</v-btn>
            <v-btn color="green" dark text @click="submit()" :loading="isLoading">Anmelden</v-btn>
          </v-row>
        </v-card-actions>
      </v-card>
    </v-hover>
    <v-snackbar color="orange" :timeout="4000" v-model="snackbar">{{ text }}</v-snackbar>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import firebase from "firebase";

export default {
  name: "Login",
  data() {
    return {
      snackbar: false,
      text: "",
      valid: false,
      rules: {
        required: v => !!v || "Dieses Feld ist verpflichtend"
      },
      user: {
        email: "",
        password: ""
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
        this.$store.dispatch("users/signIn", this.user);
      }
    },
    resetPasswortViaEmail() {
      if (this.user.email == "") {
        throw Error("Bitte geben Sie ihre Email-Adresse ins Email-Feld ein!");
      }

      this.$store.dispatch("users/resetPasswortViaEmail", this.user.email);

      this.text = "Eine Email mit dem Reset-Link wurde an " + this.user.email + " gesendet";
      this.snackbar = true;
    }
  }
};
</script>