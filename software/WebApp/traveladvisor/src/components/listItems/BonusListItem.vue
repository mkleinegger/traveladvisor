<template>
  <v-hover v-slot:default="{ hover }">
    <v-card class="mx-auto" max-width="600">
      <v-img
        :aspect-ratio="16/9"
        :src="'https://banner2.cleanpng.com/20180421/btq/kisspng-computer-icons-discounts-and-allowances-price-tag-discounted-5adb9998873486.5906501115243411445538.jpg' || 'https://www.in-australien.com/img/large/reisen/vorbereitung/rabatte-internationaler-studentenausweis/Rabatte.jpg'"
      >
        <v-expand-transition>
          <div
            v-if="hover"
            :class="`d-flex transition-fast-in-fast-out ${ (bonus.aktiv) ? 'green': 'red' } darken-2 v-card--reveal display-3 white--text`"
            style="height: 100%;"
          >{{ bonus.punkte }}</div>
        </v-expand-transition>
      </v-img>
      <v-card-text>{{ bonus.bezeichnung }}</v-card-text>
      <v-card-actions v-if="edit==true">
        <v-spacer></v-spacer>
        <v-btn icon @click="updateDialog=true">
          <v-icon>edit</v-icon>
        </v-btn>
        <v-btn icon @click="deleteDialog = true">
          <v-icon>delete</v-icon>
        </v-btn>
      </v-card-actions>
      <DeleteBonusPopup :dialog.sync="deleteDialog" :bonus="bonus" />
      <PopupUpdateBoni :dialog.sync="updateDialog" :bonus="bonus" />
    </v-card>
  </v-hover>
</template>

<script>
import PopupUpdateBoni from "@/components/popups/UpdateBonusPopup.vue";
import DeleteBonusPopup from "@/components/popups/DeleteBonusPopup.vue";

export default {
  components: {
    DeleteBonusPopup,
    PopupUpdateBoni
  },
  data() {
    return {
      deleteDialog: false,
      updateDialog: false
    };
  },
  props: {
    bonus: Object,
    edit: Boolean
  }
};
</script>

<style scoped>
.v-card--reveal {
  align-items: center;
  bottom: 0;
  justify-content: center;
  opacity: 0.7;
  position: absolute;
  width: 100%;
}
</style>
