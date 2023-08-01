<template>
  <div id="app">
    <router-view />
    <theme-picker />
    <custom-component-dialog :visible="dialogInfo.visible" :title="dialogInfo.title" :width="dialogInfo.width" :height="dialogInfo.height" :innerComponent="dialogInfo.component"/>
  </div>
</template>

<script>
import ThemePicker from "@/components/ThemePicker";
import customComponentDialog from "@/components/obpm/easyForm/customComponentDialog.vue";
export default {
  name: "App",
  components: { ThemePicker,customComponentDialog },
  data(){
    return {
      dialogInfo: {
        title: this.$store.state.customDialog.title,
        visible: this.$store.state.customDialog.visible,
        width: this.$store.state.customDialog.width,
        height: this.$store.state.customDialog.height,
        component: this.$store.state.customDialog.component,
        params: this.$store.state.customDialog.params,
      }
    }
  },
  metaInfo() {
    return {
      title: this.$store.state.settings.dynamicTitle && this.$store.state.settings.title,
      titleTemplate: title => {
        return title ? `${title} - ${process.env.VUE_APP_TITLE}` : process.env.VUE_APP_TITLE
      }
    }
  }
};
</script>
<style scoped>
#app .theme-picker {
  display: none;
}
</style>
