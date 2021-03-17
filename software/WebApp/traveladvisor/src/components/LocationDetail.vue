<template>
  <div>
    <v-hover v-slot:default="{ hover }">
      <v-card :elevation="hover ? 12 : 4">
        <v-img
          class="white--text align-end"
          height="200px"
          gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
          :src="'https://picsum.photos/510/300?random' || `https://x.kinja-static.com/assets/images/logos/placeholders/default.png`"
          :key="selectedLocation.img"
          @click="pickFile"
          aspect-ratio="2"
        ></v-img>
        <input
          type="file"
          style="display: none"
          ref="image"
          accept="image/*"
          @change="onFilePicked"
        />
      </v-card>
    </v-hover>
    <v-hover v-slot:default="{ hover }">
      <v-card :elevation="hover ? 12 : 4" class="mx-auto mt-5">
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-text-field
              v-model="selectedLocation.bezeichnung"
              @change="$emit('update:selectedLocation.bezeichnung', selectedLocation.bezeichnung)"
              label="Bezeichnung"
              :rules="[rules.required, rules.length(100)]"
              counter="100"
              :readonly="readonly"
            />
            <v-textarea
              v-model="selectedLocation.beschreibung"
              @change="$emit('update:selectedLocation.beschreibung', selectedLocation.beschreibung)"
              label="Beschreibung"
              :rules="[rules.required, rules.length(400)]"
              counter="400"
              :readonly="readonly"
            />
            <v-select
              v-model="selectedLocation.branchen"
              @change="$emit('update:selectedLocation.branchen', selectedLocation.branchen)"
              :items="allBranchen"
              item-text="bezeichnung"
              label="Branch(en)"
              multiple
              return-object
              :loading="isLoadingBranchen"
              :rules="[rules.emptyArray]"
              :readonly="readonly"
            />
            <v-text-field
              v-model="selectedLocation.punkte"
              @change="$emit('update:selectedLocation.punkte', selectedLocation.punkte)"
              label="Punkte pro Besuch"
              type="number"
              :rules="[rules.required]"
              :readonly="readonly"
            />
            <v-checkbox
              v-model="selectedLocation.aktiv"
              color="green"
              @change="$emit('update:selectedLocation.aktiv', selectedLocation.aktiv)"
              :label="`Die Location ist ${(selectedLocation.aktiv === true) ? 'aktiviert' : 'deaktiviert'}`"
              type="checkbox"
              v-if="!readonly"
            ></v-checkbox>
          </v-form>
        </v-card-text>
      </v-card>
    </v-hover>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "LocationDetail",
  data() {
    return {
      valid: false,
      rules: {
        length: len => v =>
          (v || "").length <= len ||
          `Zu viele Zeichen, es dürfen höchstens ${len} sein`,
        required: v => !!v || "Dieses Feld ist verpflichtend",
        emptyArray: v => v.length != 0 || "Dieses Feld ist verpflichtend"
      }
    };
  },
  props: {
    selectedLocation: Object,
    readonly: Boolean
  },
  computed: mapGetters({
    allBranchen: "branchen/allBranchen",
    isLoadingBranchen: "branchen/isLoading"
  }),
  methods: {
    pickFile() {
      this.$refs.image.click();
    },
    onFilePicked(e) {
      const files = e.target.files;
      let filename = files[0].name;
      if (filename.lastIndexOf(".") <= 0) {
        return;
      }
      const fileReader = new FileReader();
      fileReader.addEventListener("load", () => {
        this.selectedLocation.img = fileReader.result;
        this.$forceUpdate();
      });
      fileReader.readAsDataURL(files[0]);
    },
    validate() {
      this.$refs.form.validate();
    }
  },
  created() {
    this.$store.dispatch("branchen/loadBranchen");
  }
};
</script>