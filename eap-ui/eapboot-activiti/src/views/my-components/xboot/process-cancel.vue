<template>
  <div>
    <Modal title="确认撤回" v-model="modalCancelVisible" :mask-closable="false" :width="500">
      <Form ref="delForm" v-model="cancelForm" :label-width="70">
        <FormItem label="撤回原因" prop="reason">
          <Input type="textarea" v-model="cancelForm.reason" :rows="4"/>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="modalCancelVisible=false">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="handelSubmitCancel">提交</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import { cancelApply } from "@/api/activiti";
export default {
  name: "processCancel",
  props: {},
  data() {
    return {
      submitLoading: false,
      modalCancelVisible: false,
      cancelForm: {
        reason: ""
      }
    };
  },
  methods: {
    init() {},
    show(id, procInstId) {
      if (!id) {
        this.$Message.error("请传入ActBusiness的ID");
        return;
      }
      if (!procInstId) {
        this.$Message.error("请传入流程实例的ID");
        return;
      }
      this.cancelForm.id = id;
      this.cancelForm.procInstId = procInstId;
      this.modalCancelVisible = true;
    },
    handelSubmitCancel() {
      this.submitLoading = true;
      cancelApply(this.cancelForm).then(res => {
        this.submitLoading = false;
        if (res.success == true) {
          this.$Message.success("操作成功");
          this.$emit("on-submit", true);
          this.modalCancelVisible = false;
        }
      });
    },
    close() {
      this.modalCancelVisible = false;
    }
  },
  created() {
    this.init();
  }
};
</script>

<style lang="less">
</style>

