<template>
  <div>
    <div class="apply-operation">
      <div>
        <Form
          ref="searchProcessForm"
          :model="searchProcessForm"
          inline
          :label-width="60"
          class="search-form"
        >
          <Form-item label="流程名称" prop="name">
            <Input
              type="text"
              v-model="searchProcessForm.name"
              clearable
              placeholder="请输入流程名"
              style="width: 140px"
            />
          </Form-item>
          <Form-item label="所属分类" prop="category">
            <Cascader
              v-model="selectCat"
              :data="category"
              :load-data="loadData"
              @on-change="handleChangeCat"
              change-on-select
              filterable
              clearable
              placeholder="请选择分类"
              style="width: 140px"
            ></Cascader>
          </Form-item>
          <Form-item style="margin-left:-35px;" class="br">
            <Button @click="getProcessList" type="primary" icon="ios-search">搜索</Button>
            <Button @click="handleResetProcess">重置</Button>
            <i-switch
              size="large"
              v-model="searchProcessForm.showLatest"
              @on-change="getProcessList"
              style="margin:0 5px"
            >
              <span slot="open">最新</span>
              <span slot="close">全部</span>
            </i-switch>
          </Form-item>
        </Form>
      </div>
      <div>
        <RadioGroup v-model="showType" type="button">
          <Radio title="缩略图" label="thumb">
            <Icon type="ios-apps" style="margin-bottom:3px;"></Icon>
          </Radio>
          <Radio title="列表" label="list">
            <Icon type="md-list" style="margin-bottom:3px;"></Icon>
          </Radio>
        </RadioGroup>
      </div>
    </div>
    <div class="process-wrapper" v-if="showType=='thumb'">
      <Card v-for="(item, i) in processData" :key="i" class="process-card">
        <div class="content" @click="showApply(item)">
          <div class="other">
            <div class="name">{{i+1}}. {{item.name}}</div>
            <div class="key">{{item.description}}</div>
            <div class="info">版本：v.{{item.version}} 所属分类：{{item.categoryTitle}}</div>
          </div>
        </div>
      </Card>
      <Spin fix v-if="processLoading"/>
    </div>
    <Table
      :loading="processLoading"
      border
      :columns="processColumns"
      :data="processData"
      ref="processTable"
      v-if="showType=='list'"
    ></Table>

    <Modal title="提交申请" v-model="applyModalVisible" :mask-closable="false" :width="500">
      <Form ref="form" :model="form" :label-width="85" :rules="formValidate">
        <FormItem label="标题" prop="title">
          <Input v-model="form.title" placeholder="请输入标题" clearable/>
        </FormItem>
        <FormItem label="选择审批人" prop="assignees" :error="error">
          <Select
            v-model="form.assignees"
            placeholder="请选择或输入搜索"
            filterable
            clearable
            multiple
            :loading="userLoading"
          >
            <Option v-for="(item, i) in assigneeList" :key="i" :value="item.id">{{item.username}}</Option>
          </Select>
        </FormItem>
        <FormItem label="优先级" prop="priority">
          <Select v-model="form.priority" placeholder="请选择" clearable>
            <Option v-for="(item, i) in dictPriority" :key="i" :value="item.value">{{item.title}}</Option>
          </Select>
        </FormItem>
        <FormItem label="消息通知">
          <Checkbox v-model="form.sendMessage">站内消息通知</Checkbox>
          <Checkbox v-model="form.sendSms">短信通知</Checkbox>
          <Checkbox v-model="form.sendEmail">邮件通知</Checkbox>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="applyModalVisible=false">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="handelSubmit">提交</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import {
  getProcessDataList,
  getFirstNode,
  startBusiness,
  initActCategory,
  loadActCategory
} from "@/api/activiti";
export default {
  name: "processChoose",
  props: {},
  data() {
    return {
      userLoading: false,
      error: "",
      submitLoading: false,
      processLoading: true, // 表单加载状态
      applyModalVisible: false,
      selectCat: [],
      category: [],
      processData: [],
      showType: "thumb",
      searchProcessForm: {
        showLatest: true,
        name: "",
        status: "1", // 激活状态
        pageNumber: 1, // 当前页数
        pageSize: 1000, // 页面大小
        sort: "createTime", // 默认排序字段
        order: "desc" // 默认排序方式
      },
      form: {
        title: "",
        procDefId: "",
        assignees: [],
        priority: "0",
        sendMessage: true,
        sendSms: true,
        sendEmail: true
      },
      formValidate: {
        // 表单验证规则
        title: [{ required: true, message: "不能为空", trigger: "blur" }],
        priority: [{ required: true, message: "不能为空", trigger: "blur" }]
      },
      assigneeList: [],
      dictPriority: this.$store.state.dict.priority,
      processColumns: [
        {
          type: "index",
          width: 60,
          align: "center"
        },
        {
          title: "名称",
          key: "name",
          width: 150,
          sortable: true
        },
        {
          title: "备注描述",
          key: "description",
          width: 150,
          sortable: true
        },
        {
          title: "所属分类",
          key: "categoryTitle",
          width: 150,
          sortable: true
        },
        {
          title: "版本",
          key: "version",
          align: "center",
          sortable: true,
          render: (h, params) => {
            let re = "";
            if (params.row.version) {
              re = "v." + params.row.version;
            }
            return h("div", re);
          }
        },
        {
          title: "操作",
          key: "action",
          width: 135,
          align: "center",
          fixed: "right",
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "info",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.showApply(params.row);
                    }
                  }
                },
                "选择该流程"
              )
            ]);
          }
        }
      ]
    };
  },
  methods: {
    init() {
      this.getProcessList();
      this.initCategoryData();
    },
    getProcessList() {
      this.processLoading = true;
      getProcessDataList(this.searchProcessForm).then(res => {
        this.processLoading = false;
        if (res.success) {
          this.processData = res.result.content;
        }
      });
    },
    initCategoryData() {
      initActCategory().then(res => {
        if (res.success == true) {
          res.result.forEach(function(e) {
            if (e.isParent) {
              e.value = e.id;
              e.label = e.title;
              e.loading = false;
              e.children = [];
            } else {
              e.value = e.id;
              e.label = e.title;
            }
            if (e.status == -1) {
              e.label = "[已禁用] " + e.label;
              e.disabled = true;
            }
          });
          this.category = res.result;
        }
      });
    },
    loadData(item, callback) {
      item.loading = true;
      loadActCategory(item.value).then(res => {
        item.loading = false;
        if (res.success == true) {
          res.result.forEach(function(e) {
            if (e.isParent) {
              e.value = e.id;
              e.label = e.title;
              e.loading = false;
              e.children = [];
            } else {
              e.value = e.id;
              e.label = e.title;
            }
            if (e.status == -1) {
              e.label = "[已禁用] " + e.label;
              e.disabled = true;
            }
          });
          item.children = res.result;
          callback();
        }
      });
    },
    handleChangeCat(value, selectedData) {
      let categoryId = "";
      // 获取最后一个值
      if (value && value.length > 0) {
        categoryId = value[value.length - 1];
      }
      this.searchProcessForm.categoryId = categoryId;
    },
    handleResetProcess() {
      this.$refs.searchProcessForm.resetFields();
      // 重新加载数据
      this.getProcessList();
    },
    showApply(v) {
      if (!v.routeName) {
        this.$Message.error("该流程信息未完善，暂时无法申请");
        return;
      }
      this.form.procDefId = v.id;
      this.form.title = v.name;
      // 加载审批人
      this.userLoading = true;
      getFirstNode(v.id).then(res => {
        this.userLoading = false;
        if (res.success) {
          this.assigneeList = res.result.users;
          // 默认勾选
          let ids = [];
          res.result.users.forEach(e => {
            ids.push(e.id);
          });
          this.form.assignees = ids;
        }
      });
      this.applyModalVisible = true;
    },
    handelSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.assignees.length < 1) {
            this.error = "请至少选择一个审批人";
            return;
          } else {
            this.error = "";
          }
          if (!this.form.id) {
            this.$Message.error("请传入业务表ActBusiness的ID");
            return;
          }
          this.submitLoading = true;
          startBusiness(this.form).then(res => {
            this.submitLoading = false;
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.applyModalVisible = false;
              this.$emit("on-submit", true);
              // 重置
              this.form.id = "";
            }
          });
        }
      });
    },
    setID(v) {
      this.form.id = v;
    }
  },
  created() {
    this.init();
  }
};
</script>

<style lang="less">
.apply-operation {
  display: flex;
  justify-content: space-between;
  width: 100%;
  button {
    margin-right: 5px;
  }
}
.process-wrapper {
  display: flex;
  flex-wrap: wrap;
  position: relative;
}

.process-card {
  margin: 10px 25px 10px 0;
  width: 355px;
  :hover {
    .content .other .name {
      color: #1890ff;
      transition: color 0.3s;
    }
  }
  cursor: pointer;
  .ivu-card-body {
    padding: 0;
  }
  .content {
    display: flex;
    flex-direction: column;
    .other {
      padding: 16px;
      height: 130px;
      .name {
        font-size: 16px;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
        color: rgba(0, 0, 0, 0.85);
        font-weight: 500;
        margin-bottom: 4px;
      }
      .key {
        overflow: hidden;
        text-overflow: ellipsis;
        height: 45px;
        word-break: break-all;
        color: rgba(0, 0, 0, 0.45);
      }
      .info {
        font-size: 12px;
        color: rgba(0, 0, 0, 0.45);
        overflow: hidden;
        text-overflow: ellipsis;
        height: 36px;
        word-break: break-all;
      }
    }
  }
}
</style>

