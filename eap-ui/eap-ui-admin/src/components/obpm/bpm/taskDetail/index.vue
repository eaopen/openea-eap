<template>
  <div style="padding: 0 12px">
    <h2>{{ task.subject || task.name }}</h2>
    <div>
      <task-button
        v-for="button in buttonList"
        :data="button"
        :key="button.alias"
        @openDialog="openDialog"
      />
    </div>
    <easy-form ref="flowForm" style="margin-top: 12px;" :form-html="form.formHtml" :form-data="formData" :permission="permission" :init-data="initData" :table-permission="tablePermission" v-if="showForm"></easy-form>
    <approve-history :instId="instanceId" v-if="instanceId" />
    <flow-image
      :image-src="flowImageSrc"
      :image-info="imageInfo"
      v-if="showImage"
    ></flow-image>
  </div>
</template>
<script>
import {
  getInstanceId,
  getTaskDetail,
  getFlowImage,
} from "@/api/obpm/bpm.js";
import {
  getOperationTypeEnum,
} from "@/api/obpm/sys.js";
// import { mapActions } from "vuex";
import taskButton from "./components/taskButton.vue";
import approveHistory from "./components/approveHistory.vue";
import flowImage from "./components/flowImage.vue";
import easyForm from "@/components/obpm/easyForm/form.vue"
import carbonCopyAction from "./components/carbonCopyAction.vue";
export default {
  name: "taskDetail",
  props: ["taskid"],
  components: { taskButton, approveHistory, flowImage, easyForm, carbonCopyAction },
  data() {
    return {
      instanceId: "",
      bpmFlowUrl: "",
      bpmHistoryUrl: "",
      task: {},
      buttonList: [],
      operation: {},
      flowImageSrc: "",
      imageInfo: [],
      form:{
        formHtml: "",
        formValue: "",
        name: "",
        type: ""
      },
      formData: {},
      initData: {},
      permission: {},
      tablePermission: {},
      opinionConfig: {
        awaiting_check: {
          name: "待审批",
          color: "#ed4014",
          icon: "el-icon-s-check",
        },
        agree: {
          name: "提交/同意",
          color: "#19be6b",
          icon: "el-icon-success",
        },
        oppose: {
          name: "反对",
          color: "#ff9900",
          icon: "el-icon-error",
        },
        abandon: {
          name: "弃权",
          icon: "fa-unlink",
          color: "#808695",
        },
        reject: {
          name: "驳回",
          icon: "fa-reply",
          color: "#ff9900",
        },
        rejectToStart: {
          name: "驳回到发起人",
          icon: "fa-reply-all",
          color: "#ff9900",
        },
        revoke: {
          name: "撤销",
          icon: "fa-stop",
          color: "#ed4014",
        },
        recall: {
          name: "撤回",
          icon: "fa-reply",
          color: "#ff9900",
        },
        revoker_to_start: {
          name: "撤回到发起人",
          icon: "fa-reply-all",
          color: "#ff9900",
        },
        signPass: {
          name: "会签通过",
          icon: "fa-check-circle",
          color: "#3c763d",
        },
        signNotPass: {
          name: "会签不通过",
          icon: "fa-close",
          color: "#ed4014",
        },
        signRecycle: {
          name: "会签回收",
          icon: "fa-check-circle",
          color: "#3c763d",
        },
        skip: {
          name: "跳过执行",
          icon: "fa-arrow-circle-right",
          color: "#3c763d",
        },
        manualEnd: {
          name: "人工终止",
          icon: "fa-stop",
          color: "#ed4014",
        },
        cancelled: {
          name: "任务取消",
          icon: "fa-arrow-circle-right",
          color: "#808695",
        },
      },
      showImage: false,
      showForm: false
    };
  },
  methods: {
    openDialog(name){
      let component = carbonCopyAction
      this.$store.dispatch('dialog/showd1',{
        component: component,
        width: '50%'
      })

      // this.$dlg.modal(
      //   component, {
      //     width: 800,
      //     height: 600,
      //     title: '测试弹窗',
      //     callback: data => {
      //       this.$dlg.alert(`Received message: ${data}`)
      //     }
      //   })
    },
    checkForm(){
      this.$refs.flowForm.validateForm()
    },
    _getInstanceId() {
      getInstanceId(this.taskid).then((res) => {
        this.instanceId = res.data;
        this.bpmFlowUrl = `/bpm/instance/instanceImageDialog.html?openedFlag=true&instanceId=${this.instanceId}`;
        this.bpmHistoryUrl = `/bpm/instance/taskOpinionHistoryDialog.html?openedFlag=true&instanceId=${this.instanceId}`;
        this.flowImageSrc =
          "/api/bpm/instance/flowImage?instId=" +
          res.data +
          "&defId=&taskId=" +
          this.taskid;
        this._getFlowImage({
          instanceId: res.data,
          defId: "",
          taskId: this.taskid,
        });
      });
    },
    _getFlowImage(id) {
      getFlowImage(id).then((res) => {
        if (res.code == 200) {
          this.imageInfo = this.formatFlowNodes(res.data);
          this.showImage = true;
        }
      });
    },
    formatFlowNodes(data) {
      let arr = [];
      data.nodeIds.forEach((k) => {
        let item = Object.assign(
          {
            id: k,
          },
          data.nodeMap[k]
        );
        if (data.opinionMap[k]) {
          item.operations = data.opinionMap[k];
          let status = item.operations[item.operations.length-1].status
          let statusObj = this.opinionConfig[status]
          item = Object.assign(item, statusObj)
          arr.push(item);
        }
      });
      return arr;
    },
    _getTaskDetail() {
      getTaskDetail(this.taskid).then((res) => {
        this.task = res.data.task;
        this.buttonList = res.data.buttonList;
        this.form = res.data.form
        this.initData = res.data.initData
        this.permission = res.data.permission
        this.tablePermission = res.data.tablePermission
        this.formData = res.data.data
        this.showForm = true
      });
    },
    _getOperationTypeEnum() {
      getOperationTypeEnum().then((res) => {
        this.operation = res;
      });
    },
  },
  created() {
    console.log('详情初始化')
    this._getInstanceId();
    this._getTaskDetail();
    this._getOperationTypeEnum();
  },
};
</script>

