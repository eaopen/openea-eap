<template>
  <div style="width: 100%" :id="'div_' + uuid">
    <div style="width: 100%; min-width: 250px">
      <draggable v-model="files" :options="options">
        <!-- <transition-group type="transition"> -->
        <div v-for="(item, index) in files" :key="index" class="drag-item">
          <a
            :href="'/sys/sysFile/preview.html?fileId=' + item.id"
            target="_blank"
            style="color: #191919"
            :title="item.name"
            class="upload-item"
          >
           <i class="el-icon-document"></i>
            {{ item.name }}
          </a>
          <span class="upload-option">
            <a href="#" @click="downloadFile(item.id)">
              <el-button size="mini" type="primary" icon="el-icon-download" circle></el-button>
              </a>
            <el-popover
              v-if="abPermission == 'b' || abPermission == 'w'"
              placement="top"
              width="160"
              v-model="item.state"
            >
              <p>确认删除?</p>
              <div style="text-align: right; margin: 0">
                <el-button size="mini" type="text" @click="item.state = false"
                  >取消</el-button
                >
                <el-button type="primary" size="mini" @click="delItem(index)"
                  >确定</el-button
                >
              </div>
              <!-- <el-button slot="reference">删除</el-button> -->
              <el-button slot="reference" size="mini" type="danger" icon="el-icon-delete" circle></el-button>
            </el-popover>
            
          </span>
        </div>
        <!-- </transition-group> -->
      </draggable>
    </div>
    <!-- <el-button>上传</el-button> -->
    <div
      class="btn btn-primary fa-upload"
      @click="clickFileInput"
      v-if="!files.length &&(abPermission =='b' || abPermission== 'w')"
    >
      <span
        class="label-orange fileinput-button"
      >
        <span> {{ buttonName }} </span>
        <input
          type="file"
          name="file"
          :id="uuid"
          ref="referenceUpload"
          class="hidden"
          @change="changeFileUpload"
          :multiple="multiple"
        />
      </span>
    </div>
  </div>
</template>

<script>
import Vue from "vue";
import draggable from "vuedraggable";

export default {
  components: {
    //调用组件
    draggable,
  },
  props: {
    value: {
      type: [String, Number],
      default: "",
    },
    abPermission: {},
    multiple: {
      type: Boolean,
      default: false,
    },
    drag: {
      type: Boolean, //是否可拖动
      default: true,
    },
    accept: {
      type: String,
      default: "*",
      // 所有文件类型列表可参考 https://www.cnblogs.com/eedc/p/10069696.html
    },
    buttonName: {
      type: String,
      default: "添加附件...",
    },
  },
  data: function () {
    return {
      style: {
        border: "",
      },
      files: [],
      currentValue: "",
      currentUserId: "", //当前登录人的ID
      currentAccount: "", //当前登录人的账号
      options: {
        animation: 500, //动画时间
        sort: true, //是否可拖动
      },
      uuid: generateUUID(),
    };
  },
  computed: {
    domainBase() {
      return Vue.__ctx;
    },
  },
  created() {
    this.currentUserId = JSON.parse(window.localStorage.currentUser).userId;
    this.currentAccount = JSON.parse(window.localStorage.currentUser).account;
  },
  mounted: function () {
    this.options.sort = this.drag;
    this.handleStyle();
    if (this.value) {
      this.getFileList();
    }
    let classs = $("#div_" + this.uuid).attr("class");
    if (classs && classs.trim() === "btn btn-primary fa-upload") {
      $("#div_" + this.uuid).attr("class", "");
    }
  },
  methods: {
    onUpdate() {
      // console.log("onUpdate");
      // console.log(this.files);
    },
    onEnd() {
      // console.log("onEnd");
      // console.log(this.files);
    },
    delItem(index) {
      let file = this.files[index];
      if (this.abPermission == "b" || this.abPermission == "w") {
        this.files.splice(index, 1);
      }
    },
    removeFile(id) {
      this.files = this.files.filter((item) => {
        return item.id != id;
      });
    },
    getFileList() {
      if (this.value) {
        let vm = this;
        let defer = Vue.baseService["get"](
          Vue.__ctx + "/v1/file/" + this.value,
          {}
        );
        Vue.tools.getResultData(
          defer,
          function (data) {
            if (data && data.length > 0) {
              //按照ids的顺序排序
              data.forEach(i=>{
                  vm.$set(i, 'state', false)
              })
              let ids = vm.value.split(",");
              data.sort(function (a, b) {
                let indexa = ids.indexOf(a.id + "");
                let indexb = ids.indexOf(b.id + "");
                return indexa - indexb;
              });
            } else {
              data = [];
            }
            vm.files = data;
          },
          "alert"
        );
      }
    },
    clickFileInput() {
      $("#" + this.uuid).click();
    },
    changeFileUpload: function (e) {
      let that = this;
      let maxsize = 1024;
      for (let i = 0; i < e.target.files.length; i++) {
        let formData = new FormData();
        formData.append("id", "WU_FILE_0");
        formData.append("lastModifiedDate", new Date());
        let file = e.target.files[i];
        formData.append("file", e.target.files[i]);
        formData.append("name", file.name);
        formData.append("type", file.type);
        formData.append("size", file.size);
        let flag = false;
        if (this.accept === "*" || this.accept.split(",").indexOf("*") > -1) {
          flag = true;
        } else {
          if (file.name.indexOf(".") === -1) {
            flag = false;
          } else {
            const filename = file.name.toLowerCase();
            const accepts = this.accept.toLowerCase().split(",");
            const filetype = filename.substr(filename.lastIndexOf(".") + 1);
            flag = accepts.indexOf(filetype) > -1;
          }
        }
        if (!flag) {
          $.Dialog.warning(
            file.name +
              `不支持的文件类型${
                file.name.lastIndexOf(".") == -1
                  ? ""
                  : file.name.substr(file.name.lastIndexOf("."))
              }`
          );
          $("#" + that.uuid).val("");
          return;
        }
        if (file.size > maxsize * 1024 * 1024) {
          $.Dialog.warning(file.name + `超过${maxsize}M最大限制`);
          $("#" + that.uuid).val("");
          return;
        }
        $.ajax({
          type: "POST",
          url: Vue.__ctx + "/sys/etechFile/upload",
          contentType: false,
          processData: false,
          data: formData,
          dataType: "json",
          success: function (map) {
            $("#" + that.uuid).val("");
            if (map.isOk) {
              let f = {
                id: map.data,
                name: file.name,
                postuid: that.currentUserId,
                state: false
              };
              that.files.push(f);
            }
          },
        });
      }
    },
    referenceUpload: function (e) {
      console.log(e);
    },
    downloadFile(fileId){
      if(fileId){
        let url = Vue.__ctx + '/sys/etechFile/download?fileId=' + fileId;
        window.downloadFile(url, function () {
          console.log("下载请求结束");
        });
      }
    },
    handleStyle: function () {
      if (this.abPermission == "b") {
        if (!this.value || this.value == "[]") {
          this.$vnode.context.$validity[this.$attrs.desc] = "必填";
          this.style.border = "1px solid red";
        } else {
          delete this.$vnode.context.$validity[this.$attrs.desc];
          this.style.border = "";
        }
      }
    },
  },
  destroyed: function () {
    delete this.$vnode.context.$validity[this.$attrs.desc];
  },
  watch: {
    //数据更新时 在td 下增加按钮
    value: function (newVal, oldVal) {
      if (newVal != oldVal && newVal != this.currentValue) {
        this.handleStyle();
        this.getFileList();
      }
    },
    files: function (newVal, oldVal) {
      const ids = newVal
        .map((item) => {
          return item.id;
        })
        .toString();
      const names = newVal.map((item) => {
        return item.name;
      });
      this.currentValue = ids;
      this.$emit("input", ids);
      this.$emit("update:name", this.multiple ? names : names.toString());
      this.$emit("update:fileList", this.files);
    },
    drag: function (newVal) {
      this.options.sort = newVal;
    },
  },
};
</script>
