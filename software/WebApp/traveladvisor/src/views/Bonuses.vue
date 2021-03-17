<template>
  <div class="bonuses">
    <v-container fluid>
      <v-row>
        <v-col cols="3">
          <v-combobox
            v-model="selectedLocation"
            :items="allLocations"
            item-text="bezeichnung"
            return-object
            label="Location auswählen"
            @change="selectLocation"
            :loading="isLoadingLocations"
          ></v-combobox>
        </v-col>
        <v-col>
          <v-btn
            class="green"
            dark
            @click="openDialog"
            :disabled="!selectedLocation"
          >Bonus hinzufügen</v-btn>
          <PopupAddBoni :dialog.sync="dialog" :bonus="bonus" />
        </v-col>
      </v-row>
      <v-row v-if="selectedLocation && isLoadingBoni">
        <v-col v-for="i in 8" :key="i" lg="3" md="4" sm="6">
          <v-skeleton-loader transition="fade-transition" type="card" />
          <v-skeleton-loader transition="fade-transition" type="actions" />
        </v-col>
      </v-row>
      <v-row v-else-if="allBonuses.length > 0">
        <v-col v-for="bonus in allBonuses" :key="bonus.id" lg="3" md="4" sm="6">
          <BonusListItem :bonus="bonus" :edit="true" />
        </v-col>
      </v-row>
      <v-row v-else justify="center">
        <v-label v-if="!selectedLocation">Keine Location ausgewählt</v-label>
        <v-label v-else>Keine Prämien vorhanden</v-label>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import firebase from 'firebase';
import BonusListItem from "@/components/listItems/BonusListItem";
import PopupAddBoni from "@/components/popups/AddBonusPopup";

export default {
  components: {
    BonusListItem,
    PopupAddBoni
  },
  data() {
    return {
      bonus: {
        bezeichnung: null,
        punkte: null,
        aktiv: false,
        locationId: null
      },
      dialog: false,
      selectedLocation: null
    };
  },
  computed: mapGetters({
    allBonuses: "bonuses/allBonuses",
    allLocations: "locations/allLocations",
    isLoadingLocations: "locations/isLoading",
    isLoadingBoni: "bonuses/isLoading"
  }),
  methods: {
    openDialog() {
      this.bonus = {
        bezeichnung: null,
        punkte: null,
        aktiv: false,
        locationId: this.selectedLocation.id
      };

      this.dialog = !this.dialog;
    },
    selectLocation() {
      this.$store.dispatch("bonuses/loadBonuses", this.selectedLocation.id);
    }
  },
  created() {
    this.$store.dispatch("locations/loadLocations", {
      besitzer: firebase.auth().currentUser.uid,
      loadBranchen: true
    });
    this.$store.commit("bonuses/setBonuses", []);
  }
};
</script>

<style scoped>
.locations {
  margin-left: 20px;
  padding-right: 20px;
}

.v-label {
  font-size: 30px;
}
</style>