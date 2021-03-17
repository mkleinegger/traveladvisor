<template>
  <v-expansion-panels accordion>
    <v-expansion-panel v-for="r in rezensionen" :key="r.id">
      <v-expansion-panel-header>
        <v-row no-gutters align="center" align-content="start">
          <v-col cols="12">
            <v-rating :value="r.bewertung" color="amber" dense half-increments readonly></v-rating>
          </v-col>
        </v-row>
      </v-expansion-panel-header>
      <v-expansion-panel-content>
        <v-row>
          <v-col cols="12">{{ r.text }}</v-col>
        </v-row>
        <v-row v-if="r.besucherid == user.id">
          <v-col cols="1" offset="11">
            <v-spacer></v-spacer>
            <v-btn icon @click="dialogUpdate = !dialogUpdate">
              <v-icon>edit</v-icon>
            </v-btn>
            <v-btn icon @click="dialogDelete = !dialogDelete">
              <v-icon>delete</v-icon>
            </v-btn>
            <UpdateRezensionPopup :rezension="r" :dialog.sync="dialogUpdate" />
            <DeleteRezensionPopup :rezension="r" :dialog.sync="dialogDelete" />
          </v-col>
        </v-row>
      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script>
import { mapGetters } from "vuex";
import DeleteRezensionPopup from "@/components/popups/DeleteRezensionPopup";
import UpdateRezensionPopup from "@/components/popups/UpdateRezensionPopup";

export default {
  name: "Rezensionen",
  components: {
    DeleteRezensionPopup,
    UpdateRezensionPopup
  },
  data() {
    return {
      dialogDelete: false,
      dialogUpdate: false
    };
  },
  props: {
    rezensionen: Array
  },
  computed: mapGetters({ user: "users/user" })
};
</script>