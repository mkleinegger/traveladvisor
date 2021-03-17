<template>
  <div>
    <v-hover v-slot:default="{ hover }">
      <v-card :elevation="hover ? 12 : 4" class="mx-auto mt-5" width="800">
        <v-card-title>
          Profil {{ ( update ) ? 'bearbeiten' : '' }}
          <v-spacer></v-spacer>
          <v-btn v-if="!update && user != null && user.typ == 'besucher' " @click="dialog = !dialog" text>Punkte</v-btn>
          <v-btn icon @click="update = !update">
            <v-icon>edit</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-form ref="form" v-if="user != null">
            <v-text-field v-model="user.displayName" label="Displayname" color="green" readonly />
            <v-text-field v-model="user.vorname" label="Vorname" :readonly="!update" color="green" />
            <v-text-field
              v-model="user.nachname"
              label="Nachname"
              :readonly="!update"
              color="green"
            />
            <v-text-field v-model="user.email" label="Email" readonly color="green" />
            <v-text-field
              v-if="update"
              v-model="password"
              type="password"
              label="Password"
              readonly
              color="green"
              append-outer-icon="mail"
              @click:append-outer="resetPassword"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn v-if="update" color="green" @click="update = !update" text>Cancel</v-btn>
          <v-btn
            v-if="update"
            color="green"
            @click="updateUser"
            text
            :loading="isLoading"
          >Aktualisieren</v-btn>
          <v-btn v-if="!update" color="green" @click="signOut" text>Abmelden</v-btn>
        </v-card-actions>
      </v-card>
    </v-hover>
    <ShowPointsPopup :dialog.sync="dialog" />
  </div>
</template>
<script>
import { mapGetters } from "vuex";
import firebase from "firebase";

import ShowPointsPopup from "@/components/popups/ShowPointsPopup";

export default {
  name: "Account",
  components: {
    ShowPointsPopup
  },
  data() {
    return {
      update: false,
      password: "123456",
      dialog: false
    };
  },
  computed: {
    ...mapGetters({
      user: "users/user",
    })
  },
  watch: {
    update() {
      if (!this.update) {
        this.$store.dispatch("users/fetchUser", firebase.auth().currentUser);
      }
    }
  },
  methods: {
    signOut() {
      this.$store.dispatch("users/signOut");
      this.$router.replace({ name: "Login" });
    },
    resetPassword() {
      this.$store.dispatch("users/resetPasswortViaEmail", this.user.email);
    },
    updateUser() {
      this.$store.dispatch("users/updateUser", this.user);
      this.update = !this.update;
    }
  }
};
</script>