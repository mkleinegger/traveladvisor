<template>
  <v-dialog v-model="dialog" max-width="1200" persistent>
    <v-card>
      <v-card-title>
        Rezensionen
        <v-spacer></v-spacer>
        <v-btn
          v-if="user != null && user.typ == 'besucher' && !doIhaveARezension"
          class="mr-2"
          color="green"
          @click="dialogAdd = !dialogAdd"
          text
        >Erstellen</v-btn>
        <v-btn icon @click="$emit('update:dialog', false)">
          <v-icon>close</v-icon>
        </v-btn>
      </v-card-title>
      <Rezensionen v-if="allRezensionen.length != 0" :rezensionen="allRezensionen" />
      <v-card-text  v-else>
        <v-label>Es gibt noch keine Rezensionen</v-label>
      </v-card-text>
      <v-progress-linear
        :active="isLoadingRezensionen"
        :indeterminate="isLoadingRezensionen"
        color="green"
      />
    </v-card>
    <AddRezensionPopup :location="location" :dialog.sync="dialogAdd" />
  </v-dialog>
</template>

<script>
import { mapGetters } from "vuex";
import Rezensionen from "@/components/Rezensionen";
import AddRezensionPopup from "@/components/popups/AddRezensionPopup";

export default {
  name: "ShowRezensionPopup",
  components: {
    Rezensionen,
    AddRezensionPopup
  },
  data() {
    return {
      dialogAdd: false
    };
  },
  props: {
    location: Object,
    dialog: Boolean
  },
  computed: {
    ...mapGetters({
      allRezensionen: "rezensionen/allRezensionen",
      isLoadingRezensionen: "rezensionen/isLoading",
      user: "users/user"
    }),
    doIhaveARezension() {
      return this.allRezensionen.some(r => r.besucherid == this.user.id);
    }
  },
  watch: {
    location() {
      //start loading rezensionen first, when the location data is ready
      if (this.location) {
        this.$store.dispatch("rezensionen/loadRezensionen", this.location.id);
      }
    }
  },
  created() {
    if (this.location) {
      this.$store.dispatch("rezensionen/loadRezensionen", this.location.id);
    }
  }
};
</script>