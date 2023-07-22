<template>
  <div>
    <el-button type="primary" size="mini" v-if="!isComplete && !isUploading" @click="uploadhandler"
      >上传文件</el-button
    >
    <el-progress
      :percentage="percentage"
      :color="customColorMethod"
      v-if="isUploading"
      style="max-width: 200px"
    ></el-progress>
    <el-link
      v-if="notice && !isUploading"
      :underline="false"
      :type="linkType"
      :icon="linkIcon"
      >{{ notice }}</el-link
    >
  </div>
</template>

<script>
export default {
  props: {
    uploadurl: "",
    stateurl: "",
    filelistid: "",
    id: "",
  },
  data: function () {
    return {
      isComplete: false,
      isUploading: false,
      notice: "",
      percentage: 0,
      linkType: "success",
      linkIcon: "el-icon-circle-check",
    };
  },
  created: function () {
    this.getStatus();
  },
  methods: {
    uploadCompleted() {
      clearInterval(this.timer)
      this.isUploading = false;
      this.isComplete = true;
      this.notice = "已上传";
      this.linkType = "success";
      this.linkIcon = "el-icon-circle-check";
    },
    uploadError(msg) {
      this.isUploading = false;
      clearInterval(this.timer)
      this.notice = msg;
      this.linkType = "danger";
      this.linkIcon = "el-icon-circle-close";
    },
    getStatus() {
      let vm = this;
      Vue.baseService
        .get(Vue.__ctx + this.stateurl + this.filelistid)
        .then((res) => {
          if (res.isOk) {
            if (res.data.drawingflag == 1) {
              vm.uploadCompleted();
            }
          }
        });
    },
    uploadhandler() {
      let vm = this;
      Vue.baseService.get(Vue.__ctx + this.uploadurl + this.id).then((res) => {
        if (!res.isOk) {
          vm.uploadError(res.msg);
        }
      });
      vm.uploading();
    },
    uploading() {
      this.isUploading = true;
      let vm = this;
      this.timer = setInterval(() => {
        Vue.baseService
          .get(Vue.__ctx + this.stateurl + this.filelistid)
          .then((res) => {
            if (res.isOk) {
              if (res.data.drawingflag === 2) {
                vm.percentage = (
                  (res.data.uploadednum / res.data.totalnum) *
                  100
                ).toFixed(0);
              } else {
                vm.uploadCompleted();
              }
            } else {
              vm.uploadError(res.msg);
            }
          });
      }, 1000);
    },
    customColorMethod(percentage) {
      if (percentage < 30) {
        return "#909399";
      } else if (percentage < 70) {
        return "#e6a23c";
      } else {
        return "#67c23a";
      }
    },
  },
};
</script>
