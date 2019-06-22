<template>
  <div>
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
import { getProcessByKey, getFirstNode, startBusiness } from "@/api/activiti";
export default {
  name: "processStart",
  props: {},
  data() {
    return {
      userLoading: false,
      error: "",
      submitLoading: false,
      applyModalVisible: false,
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
      routeName: ""
    };
  },
  methods: {
    init() {
    },
    showApply() {
      if (!this.routeName) {
        this.$Message.error("该流程信息未完善，暂时无法申请");
        return;
      }
      // 加载审批人
      this.userLoading = true;
      getFirstNode(this.form.procDefId).then(res => {
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
          this.submitLoading = true;
          startBusiness(this.form).then(res => {
            this.submitLoading = false;
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.applyModalVisible = false;
              this.$emit("on-submit", true);
              // 重置
              this.routeName = "";
            }
          });
        }
      });
    },
    show(key, id) {
      if (!key) {
        this.$Message.error("请先传入要申请的流程key");
        return;
      }
      if (!id) {
        this.$Message.error("请传入业务表ActBusiness的ID");
        return;
      }
      this.form.id = id;
      this.$emit("on-loading", true);
      getProcessByKey(key).then(res => {
        this.$emit("on-loaded", false);
        if (res.success) {
          this.routeName = res.result.routeName;
          this.form.procDefId = res.result.id;
          this.form.title = res.result.name;
          this.showApply();
        }
      });
    },
    close() {
      this.applyModalVisible = false;
    }
  },
  created() {
    this.init();
  }
};
</script>

<style lang="less">
</style>

