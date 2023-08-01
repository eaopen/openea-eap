<template>
  <div>
    <div :id="'uploader-'+uuid">
      <!--用来存放item-->
      <div>
        <button
            :id="'pick_'+uuid"
            class="el-button el-button--mini"
            v-show="!disabled && (showSelectFileBtn && (abPermission === 'b' || abPermission === 'w'))"
            :disabled="abPermission !== 'b' && abPermission !== 'w'"
        >
          选择文件
        </button>
        <div
            class="el-button el-button--mini"
            @click="clickAllUploadBtn"
            v-if="
            (abPermission === 'b' || abPermission === 'w') && showAllUploadBtn
          "
        >
          上传
        </div>
        <el-popover
            v-if="
            (abPermission === 'b' || abPermission === 'w') && showClearAllBtn
          "
            placement="top"
            width="160"
            v-model="visible"
        >
          <p>确认清空吗？</p>
          <div style="text-align: right; margin: 0">
            <el-button size="mini" type="text" @click="visible = false"
            >取消
            </el-button
            >
            <el-button type="primary" size="mini" @click="clickClearAllBtn"
            >确定
            </el-button
            >
          </div>
          <el-button size="mini" type="danger" slot="reference" v-show="abPermission === 'b' || abPermission === 'w'">全部清空
          </el-button
          >
        </el-popover>
      </div>
      <div class="fileModel">
        <div class="file-item" v-for="(item, index) in files" :key="index">
          <span @click="toPreview(item)" style="cursor:pointer; color:#2d8cf0;text-decoration: underline;">{{ item.name }}</span>
          <i
              v-show="!disabled && (abPermission === 'b' || abPermission === 'w')"
              class="el-icon-circle-close"
              @click="delFile(index, item.id)"
              style="color: #2d8cf0; cursor: pointer"
          ></i>
        </div>
      </div>
      <div :id="uuid" class="uploader-list"></div>

    </div>
  </div>
</template>
<script>
import Vue from "vue";
export default {
  name: "abUploadDialog",
  props: {
    value: {
      type: [String, Number],
      default: "",
    },
    disabled: {
      type: [Boolean, String],
      default: false,
    },
    abPermission: {
      type: String,
      default: "b",
    },
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
    module: {
      type: String,
      default: "",
    },
    uniqueIdentifier: {
      type: String,
      default: "",
    },
    beforeUploadRule: {
      type: Object
    },
    newFileName: {
      type: String,
    },
  },
  data() {
    return {
      uuid: generateUUID(),
      visible: false,
      files: [],
      uploader: {},
      options: {
        animation: 500, //动画时间
        sort: true, //是否可拖动
      },
      currentValue: "",
      currentUserId: "", //当前登录人的ID
      currentAccount: "", //当前登录人的账号
      showSelectFileBtn: true,
      showAllUploadBtn: false,
      showClearAllBtn: false,
      fileMap: {},
    };
  },
  mounted() {
    console.log(this.beforeUploadRule);
    let $list = $("#" + this.uuid); //这几个初始化全局的百度文档上没说明，好蛋疼。
    // 初始化Web Uploader
    let GUID = WebUploader.Base.guid();
    let vm = this;

    WebUploader.Uploader.register(
        {
          "add-file": "addFiles",
          "before-send-file": "beforeSendFile",
          "before-send": "beforeSend",
          "after-send-file": "afterSendFile",
        },
        {
          addFiles: function (files) {

          },
          beforeSendFile: function (file) {
            //在文件发送之前request，此时还没有分片（如果配置了分片的话），可以用来做文件整体md5验证
            console.info("beforeSendFile", file);
            let deferred = WebUploader.Deferred();
            vm.checkFileMd5(file, deferred);
            return deferred.promise();
          },
          beforeSend: function (block) {
            //在分片发送之前request，可以用来做分片验证，如果此分片已经上传成功了，可返回一个rejected promise来跳过此分片上传
            console.info("beforeSend", block);
          },
          afterSendFile: function (file) {
            //在所有分片都上传完毕后，且没有错误后request，用来做分片验证，此时如果promise被reject，当前文件上传会触发错误。
            console.info("afterSendFile", file);
          },
        }
    );

    let uploader = WebUploader.create({
      swf: "/static/vendor/webuploader/Uploader.swf",

      // 文件接收服务端。
      server: Vue.__ctx + "/sys/etechFile/webuploader/upload",
      withCredentials: true,
      auto: false,
      // 选择文件的按钮。可选。
      // 内部根据当前运行是创建，可能是input元素，也可能是flash.
      pick: {
        id: "#pick_" + this.uuid,
        innerHTML: vm.buttonName,
        multiple: vm.multiple,
      },
      // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
      resize: false,
      chunked: true,
      chunkSize: 20971520, //如果要分片，分多大一片？ 默认大小为5M 这里改为20M
      chunkRetry: 2,//如果某个分片由于网络问题出错，允许自动重传多少次？
      prepareNextFile: true,
      method: "POST",
      accept: {
        // extensions: "txt,gif,png,jpg,doc,zip,rar,docx,pdf,prt,tif,tiff",
        extensions: vm.accept
      },
      formData: {
        guid: GUID, //自定义参数，待会儿解释
      },
    });
    uploader.on("beforeFileQueued", function (file) {
      if (vm.newFileName){
        file.name = vm.newFileName;
      }

      console.log(file)
      let flag = true
      if (vm.beforeUploadRule && Object.keys(vm.beforeUploadRule).length) {
        Object.keys(vm.beforeUploadRule).forEach(k => {
          if (file[k] != vm.beforeUploadRule[k]) {
            flag = false
            $.Dialog.error(`上传文件的名称必须为 ${vm.beforeUploadRule[k]},请确认`)
          }
        })
      }
      if (!flag) return false
    })
    // 当有文件被添加进队列的时候
    uploader.on("fileQueued", function (file) {
      $list.append(
          '<div id="' +
          file.id +
          '" class="item row">' +
          '<div class="file-name">' +
          file.name +
          '<div class="progress-inner">' +
          '<div class="progress progress-striped active">' +
          '<div class="progress-bar" role="progressbar" style="width: 0%">' +
          "</div>" +
          "</div>" +
          '<div class="operation">' +
          // '<i class="el-icon-video-pause"></i>' +
          // '<i class="el-icon-video-play"></i>' +
          "</div>" +
          "</div>" +
          "</div>"
      );
      vm.showAllUploadBtn = true;
      vm.showSelectFileBtn = false;
      file.md5Status = 1; //1: 生成中
      if (!Array.isArray(file)) vm.clickAllUploadBtn()
      uploader
          .md5File(file)
          // 及时显示进度
          .progress(function (percentage) {
            if (percentage == 1) {
              console.log("Percentage:", percentage);
            }
          })
          // 完成
          .then(function (val) {
            console.log("md5 result:", val);
            file.md5Status = 2; //1: 生成成功
            file.md5 = val;
          })
          .fail(function () {
            //生成MD5异常
            file.md5Status = 0; //0: 生成失败
          });
    });
    // '<i class="el-icon-circle-close"></i>' +
    // 文件上传过程中创建进度条实时显示。
    uploader.on("uploadProgress", function (file, percentage) {
      let $li = $("#" + file.id),
          $percent = $li.find(".progress .progress-bar");
      $percent.css("width", percentage * 100 + "%");
    });
    uploader.on("error", function (type) {
      if (type == "Q_TYPE_DENIED") {
        $.Dialog.error("您上传的文件内容为空，或不支持该文件格式，请确认！");
      }
    });
    uploader.on("uploadAccept", function (object, ret) {
      console.log(object, ret);
      if (ret.code == 200) {
        // console.log("上传成功！");
        let md5 = ret.data.md5;
        vm.fileMap[md5] = ret.data;

      } else {
        // console.log("上传失败！");

        // 文件上传失败
        uploader.on("uploadError", function (file) {
          console.log(file, "uploadError");
          if (ret && ret.msg) {
            $("#" + file.id)
                .find("div.state")
                .text(ret.msg);
          } else {
            $("#" + file.id)
                .find("div.state")
                .text("上传失败");
          }
          //重新上传文件
          file.reUpload = true;
          // uploader.retry(file);
        });
      }
    });

    // 文件成功
    uploader.on("uploadSuccess", function (file) {
      console.log(file, "uploadSuccess");
      $("#" + file.id)
          .find("div.operation")
          .empty();
      $("#" + file.id)
          .find("div.operation")
          .html('<i class="el-icon-circle-close"></i>');
    });

    // 文件上传完成
    uploader.on("uploadComplete", function (file) {
      console.log(file, "uploadComplete");
      $("#" + file.id).remove();
      vm.showAllUploadBtn = false;
      vm.showClearAllBtn = true;
      vm.showSelectFileBtn = true;
      //发送合并请求
      let filedata = vm.fileMap[file.md5];
      let m = 1;
      let timer = setInterval(function () {
        filedata = vm.fileMap[file.md5];
        if (filedata || m > 30) {
          clearInterval(timer);
          //分片数大于1说明分片发送了需要调用合并接口合并文件
          if (filedata.chunks && filedata.chunks > 1) {
            let url = Vue.__ctx + "/sys/etechFile/webuploader/merge";
            Vue.baseService.post(url, filedata).then(function (data) {
              if (data.isOk) {
                if (!vm.multiple) {
                  vm.files = [data.data];
                  vm.showSelectFileBtn = false;
                } else {
                  vm.files.unshift(data.data);
                }
                vm.$parent.addFileHandler(data.data.id);
              } else {
                console.error(data.msg);
              }
            });
          } else {
            if (!vm.multiple) {
              vm.files = [filedata];
              vm.showSelectFileBtn = false;
            } else {
              vm.files.unshift(filedata);
            }
            vm.$parent.addFileHandler(filedata.id);
          }
        }
        m++;
      }, 100);
    });

    uploader.on("uploadBeforeSend", function (object, data, headers) {
      // block为分块数据。
      // file为分块对应的file对象。
      let file = object.file;
      // 修改data可以控制发送哪些携带数据。
      data.dataType = $("#dataType option:selected").val();
      data.fileType = $("#fileType option:selected").val();
      data.uploadType = $("#uploadType option:selected").val();

      data.module = vm.module;
      data.uniqueIdentifier = vm.uniqueIdentifier;
      data.md5 = file.md5;

      data.start = object.blob.start;
      data.end = object.blob.end;
      data.ruid = object.blob.ruid;
      data.uid = object.blob.uid;

      console.info("发送请的数据：", object);
      console.info("发送请的数据：", data);
    });
    uploader.on("uploadFinished", function () {
      //清空队列
      vm.uploader.reset();
      console.log("reset");
    });
    this.uploader = uploader;
    this.options.sort = this.drag;
    if (this.value) {
      this.getFileList();
    }
    // if (!this.multiple && this.value) {
    //   this.showSelectFileBtn = false;
    // }
  },
  methods: {
    //点击全部上传
    clickAllUploadBtn() {
      if ($("#dataType option:selected").val() == "none") {
        $("#bad_info").html("");
        $("#bad_info").append(
            '<label style="color:red">错误信息：文件格式必须选择一个</label>'
        );
      } else if ($("#fileType option:selected").val() == "none") {
        $("#bad_info").html("");
        $("#bad_info").append(
            '<label style="color:red">错误信息：数据类型必须选择一个</label>'
        );
      } else if ($("#uploadType option:selected").val() == "none") {
        $("#bad_info").html("");
        $("#bad_info").append(
            '<label style="color:red">错误信息：上传类型必须选择一个</label>'
        );
      } else {
        //必须等到生成了md5之后才开始上传
        //可上传的文件状态，只有在队列中等待上传，上传出错可重试、上传中断的的状态
        // let files = this.uploader.getFiles(['queued', 'error', 'interrupt']);
        let files = this.uploader.getFiles();
        console.info("files:", files);
        let vm = this;
        files.forEach((file) => {
          if (!file.md5 && file.md5Status === 1) {
            let percentage = 0;
            let $li = $("#" + file.id),
                $percent = $li.find(".progress .progress-bar");
            let interval = setInterval(function () {
              if (file.md5 && file.md5Status === 2) {
                clearInterval(interval);
                console.log("开始上传");
                vm.uploader.upload(file);
              } else if (file.md5Status === 0) {
                console.log("md5获取出错");
                $percent.css("width", "0%");
                clearInterval(interval);
              }
              console.log("等待md5");
              //加个假的进度
              if (percentage < 20) {
                if (percentage < 5) {
                  percentage += 1;
                } else if (percentage < 10) {
                  percentage += 0.5;
                } else if (percentage < 20) {
                  percentage += 0.1;
                }
                $percent.css("width", percentage + "%");
              }
            }, 100);
          } else {
            console.log("开始上传");
            vm.uploader.upload(file);
          }
        });
      }
    },
    clickClearAllBtn() {
      this.showClearAllBtn = false;
      for (let i = 0; i < this.uploader.getFiles().length; i++) {
        // 将图片从上传序列移除
        this.uploader.removeFile(this.uploader.getFiles()[i]);
        //this.uploader.removeFile(uploader.getFiles()[i], true);
        //delete this.uploader.getFiles()[i];
        // 将图片从缩略图容器移除
        let $li = $("#" + this.uploader.getFiles()[i].id);
        $li.off().remove();
      }
      this.visible = false;
      // 重置文件总个数和总大小
      // fileCount = 0;
      // fileSize = 0;
      // 重置uploader，目前只重置了文件队列
      this.uploader.reset();
      this.files = [];
      this.showSelectFileBtn = true
    },
    checkFileMd5(file, deferred) {
      let vm = this;
      file.module = vm.module;
      file.uniqueIdentifier = vm.uniqueIdentifier;
      Vue.baseService
          .post(Vue.__ctx + "/sys/etechFile/webuploader/checkmd5", {
            name: file.name,
            size: file.size,
            type: file.type,
            ext: file.ext,
            md5: file.md5,
            module: file.module,
            uniqueIdentifier: file.uniqueIdentifier,
          })
          .then(function (data) {
            if (data.isOk) {
              if (!data.data) {
                //文件不存在继续上传
                deferred.resolve();
              } else {
                //文件已存在，无需上传
                vm.fileMap[file.md5] = data.data;
                vm.$forceUpdate();
                deferred.reject();
                vm.uploader.skipFile(file);
              }
            } else {
              console.error("验证MD5失败：" + data.msg);
              deferred.resolve();
            }
          });
    },
    delFile(index, id) {
      this.files.splice(index, 1);
      this.$parent.delFileHandler(id);
      this.showSelectFileBtn = true;
    },
    toPreview(file){
      window.open(`/sys/sysFile/preview.html?fileId=${file.id}&withWatermark=1`)
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
                data.forEach((i) => {
                  vm.$set(i, "state", false);
                });
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
  },
  watch: {
    //数据更新时 在td 下增加按钮
    value: function (newVal, oldVal) {
      if (newVal != oldVal && newVal != this.currentValue) {
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
<style>

.progress {
  height: 10px !important;
}
</style>
