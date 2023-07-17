<template>
  <div>
    <i-frame v-if="!loading" :src="url" />
  </div>
</template>
<script>
import iFrame from "@/components/iFrame/index";
import { getConfigKey } from "@/api/infra/config";
export default {
  name: "ObpmWeb",
  components: { iFrame },
  data() {
    return {
      // TODO path => url
      // sso
      // obpmWebBaseUrl = /obpm-web/  规避跨域问题
      // obpmBaseUrl = http://localhost:3002/  dev only
      // url = obpmBaseUrl + path + ssoAuth
      url: "http://localhost:3002/bpm/definition/definitionList.html",
      loading: true
    };
  },
  created() {
    // getConfigKey("url.skywalking").then(response => {
    //   if (!response.data || response.data.length === 0) {
    //     return
    //   }
    //   this.url = response.data;
    // }).finally(() => {
    //   this.loading = false;
    // })

    this.loading = false;
  }
};
</script>
