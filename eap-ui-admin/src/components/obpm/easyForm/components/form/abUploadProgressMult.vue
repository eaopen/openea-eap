<template>
  <div>
    <div style="display: flex">
      <el-button
        type="primary"
        size="mini"
        :loading="isUploading"
        @click="uploadhandler"
        >同步文件</el-button
      >
      <div style="width: 150px; margin-left: 10px" v-if="isUploading">
        <el-progress :percentage="percentage" style="margin-top: 5px"></el-progress></div>
      <span v-if="notice" style="color: #f56c6c; margin-left: 10px">{{notice}}</span>
    </div>
    <el-row style="padding: 10px 0">
      <el-col v-for="(item,index) in list" :key="item.partsid" style="width: 270px; line-height: 24px; color: #191919">
        <b>{{ index + 1 }}.</b>
        <span title="item.filename">{{ item.filename.length> 28?item.filename.slice(0,28)+'..':item.filename }}</span>
        <span style="font-size: 12px" :underline="false" :style="{'color': item.color}">
          [{{
          item.notice
        }}]</span>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: {
    uploadurl: "",
    stateurl: "",
    filelistid: "",
    id: "",
    once: false
  },
  data: function () {
    return {
      list: [],
      percentage: 0,
      isUploading: false,
      notice: '',
      disabled: false,
      btnName: '同步图纸文件'
    };
  },
  created: function () {
    this.getStatus();
  },
  methods: {
    getStatus() {
      let vm = this;
      Vue.baseService
        .get(Vue.__ctx + this.stateurl + this.filelistid)
        .then((res) => {
          if (res.isOk) {
            if(vm.once){
              if(res.data.drawingflag == 1){
                vm.disabled = true
                vm.btnName = '已同步'
              }
            }
            if (res.data.uploadList && res.data.uploadList.length) {
              res.data.uploadList.forEach((item) => {
                if (item.status === 0) {
                    item.notice = "未上传";
                    item.color = "#999";
                    item.icon = "el-icon-circle-plus-outline"
                  } else if (item.status === 1) {
                    item.notice = "已上传";
                    item.color = "#4cae4c";
                    item.icon = "el-icon-circle-check"
                  } else if (item.status === 2) {
                    item.notice = "上传失败";
                    item.color = "#f6685e";
                    item.icon = "el-icon-circle-close"
                  }
              });
              vm.$set(vm, "list", res.data.uploadList);
            } else {
              vm.$set(vm, "list", []);
            }
          }
        });
    },
    uploadhandler() {
      let vm = this;
      vm.isUploading = true
      Vue.baseService.get(Vue.__ctx + this.uploadurl + this.id).then((res) => {
        if (!res.isOk) {
          vm.isUploading = false
          vm.uploadError(res.msg);
        }else {
          vm.notice = ''
          vm.uploading();
        }
      });
    },
    uploadError(err){
      this.notice = err
    },
    uploading() {
      let vm = this;
      this.timer = setInterval(() => {
        Vue.baseService
          .get(Vue.__ctx + this.stateurl + this.filelistid)
          .then((res) => {
            if (res.isOk) {
              if (res.data.uploadList && res.data.uploadList.length) {
                let n = 0;
                res.data.uploadList.forEach((item) => {
                  if (item.status === 0) {
                    item.notice = "未上传";
                    item.color = "#999";
                    item.icon = "el-icon-circle-plus-outline"
                  } else if (item.status === 1) {
                    item.notice = "已上传";
                    item.color = "#4cae4c";
                    item.icon = "el-icon-circle-check"
                    n++;
                  } else if (item.status === 2) {
                    item.notice = "上传失败";
                    item.color = "#f6685e";
                    item.icon = "el-icon-circle-close"
                  }
                });
                vm.$set(vm, "list", res.data.uploadList);
                vm.percentage = (n * 100) / res.data.uploadList.length;
                if (n == res.data.uploadList.length) {
                  clearInterval(vm.timer);
                  vm.isUploading = false
                }
              } else {
                vm.$set(vm, "list", []);
                vm.percentage = 0;
                clearInterval(vm.timer);
                vm.isUploading = false
              }
            }
          });
      }, 1000);
    },
  },
};
</script>
<style>
  .grey {
    color: #999;
  }
  .red {
    color: #f6685e;
  }
  .green {
    color: #4cae4c;
  }
</style>
