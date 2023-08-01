<template>
  <div style="padding: 10px 20px" v-cloak>
    <el-row :gutter="20" style="margin-bottom: 10px">
      <el-button style="margin-left: 10px" size="mini" @click="closeDialog" icon="el-icon-close">关闭窗口</el-button>
      <el-button type="primary" size="mini" plain
                 style="margin-left: 10px"
                 icon="el-icon-download" @click="downloadTemplate"
                 :loading="downloadTemplateButtonLoading">
        下载模板
      </el-button>
      <el-popover
          style="margin-left: 10px"
          placement="bottom-end"
          v-model="popoverVisible"
          width="400"
          trigger="click">
        <el-upload drag :action="UploadTemplateURL" :show-file-list="false" :before-upload="beforeUpload"
                   :on-success="uploadFile" with-credentials="" :multiple="false" accept=".xls,.xlsx"
                   style="float: right" v-loading="uploadLoading"
                   element-loading-text="拼命解析数据中"
                   element-loading-spinner="el-icon-loading"
                   element-loading-background="rgba(0, 0, 0, 0.8)">
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">
            <div>注意：只允许上传excel文件</div>
          </div>
        </el-upload>
        <el-button type="warning" size="mini" icon="el-icon-upload" slot="reference">批导入数据</el-button>
      </el-popover>
      <el-badge :value="count" class="item" style="margin-left: 10px">
        <el-button type="success" size="mini" icon="el-icon-document"
                   :disabled="submitButtonDisabled"
                   :loading="submitButtonLoading"
                   @click="submitData">
          提交数据
        </el-button>
      </el-badge>
    </el-row>
    <el-table
        :data="tableData"
        border
        :row-style="{height:'0px'}"
        :cell-style="{padding:'7px'}"
        :height="tableHeight"
        stripe
    >
      <el-table-column
          fixed="left"
          label="序号"
          type="index"
          width="80">
      </el-table-column>
      <el-table-column
          v-for="(item,index) in tableColumns"
          :key="index"
          :label="item.label"
          :prop="item.prop"
          :width="item.width"
          :min-width="item.minWidth"
          :show-overflow-tooltip="true"
          :fixed="item.fixed"
      ></el-table-column>
      <el-table-column label="错误信息提示" min-width="240" fixed="right">
        <template slot-scope="scope">
          <span style="color:#F56C6C;">{{ scope.row.errorMsg }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: "batchImportForm",
  props: {
    templateDownloadUrl: {
      type: String,
      required: true
    },
    templateUploadUrl: {
      type: String,
      required: true
    },
    tableColumns: {
      type: Array,
      required: true
    },
    submitDataUrl: {
      type: String,
      required: true
    },
    tableHeight: {
      type: Number,
      default: window.parent.innerHeight - 140
    }
  },
  data: function () {
    return {
      downloadTemplateButtonLoading: false,
      uploadLoading: false,
      submitButtonDisabled: true,
      submitButtonLoading: false,
      tableData: [],
      count: null,
      popoverVisible: false
    }
  },
  computed: {
    UploadTemplateURL() {
      return Vue.__ctx + this.templateUploadUrl
    }
  },
  methods: {
    closeDialog() {
      window.$.Dialog.close(window);
    },
    downloadTemplate() {
      this.downloadTemplateButtonLoading = true
      window.downloadFile(Vue.__ctx + this.templateDownloadUrl, () => {
        this.downloadTemplateButtonLoading = false
      })
    },
    beforeUpload() {
      this.uploadLoading = true
      this.tableData = []
      this.count = null
    },
    uploadFile(res) {
      this.uploadLoading = false
      this.tableData = res.data
      this.popoverVisible = false
      if (res.isOk) {
        this.submitButtonDisabled = false
        this.count = this.tableData.length
        this.$notify({
          title: '成功',
          message: res.msg,
          type: 'success',
          duration : 8000
        })
      } else {
        this.submitButtonDisabled = true
        this.$notify.error({
          title: '错误',
          message: res.msg,
          duration : 8000
        });
      }
    },
    submitData() {
      this.submitButtonLoading = true
      const that = this
      $.ajax({
        url: __ctx + this.submitDataUrl,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data: JSON.stringify(this.tableData),
        async: true,
        error: res => {
          that.submitButtonLoading = false
          that.$notify.error({
            title: '错误',
            message: res.msg,
            duration: 0
          });
        },
        success: res => {
          that.submitButtonLoading = false
          if (res.isOk) {
            $.Toast.success(res.msg);
            window.opener.location.reload()
            that.closeDialog();
          } else {
            that.$notify.error({
              title: '错误',
              message: res.msg,
              duration: 0
            });
          }
        }
      });
    }
  }
}
</script>