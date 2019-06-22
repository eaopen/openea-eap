<style lang="less">
textarea.ivu-input {
  max-width: 100%;
  height: auto;
  min-height: 32px;
  vertical-align: bottom;
  font-size: 12px;
}
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card>
          <p slot="title">
            <span v-if="type==0">新增请假申请</span>
            <span v-else-if="type==1">编辑请假申请</span>
            <span v-else>查看请假申请详情</span>
          </p>
          <Row>
            <Form
              ref="form"
              :model="form"
              :label-width="95"
              :rules="formValidate"
              style="position:relative"
            >
              <FormItem label="请假申请人" prop="title" v-if="type!=0&&type!=1">{{form.createBy}}</FormItem>
              <FormItem label="请假类型" prop="type">
                <Select v-model="form.type" placeholder="请选择" style="width: 200px">
                  <Option
                    v-for="(item, i) in dictType"
                    :key="i"
                    :value="item.value"
                  >{{item.title}}</Option>
                </Select>
              </FormItem>
              <FormItem label="标题" prop="title">
                <Input v-model="form.title" style="width:550px"/>
              </FormItem>
              <FormItem label="原因" prop="description">
                <Input
                  type="textarea"
                  v-model="form.description"
                  :rows="5"
                  placeholder="请输入详细情况"
                  style="width: 550px"
                />
              </FormItem>
              <FormItem label="请假时间范围" :error="errorMsg">
                <DatePicker
                  v-model="selectDate"
                  :options="options"
                  type="datetimerange"
                  format="yyyy-MM-dd HH:mm"
                  clearable
                  @on-change="selectDateRange"
                  placeholder="选择起始时间"
                  style="width: 550px"
                ></DatePicker>
              </FormItem>
              <FormItem label="请假时长">{{form.duration}} 小时</FormItem>
              <Form-item class="br">
                <Button
                  type="primary"
                  :loading="submitLoading"
                  @click="handelSubmit"
                  v-if="type==0||type==1"
                >保存并关闭</Button>
                <Button @click="handelPrint" v-if="type!=0" type="info">打印</Button>
                <Button @click="handelCancel">关闭</Button>
              </Form-item>
              <Spin size="large" fix v-if="loading"></Spin>
            </Form>
          </Row>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import {
  getLeaveData,
  addLeave,
  updateLeave,
  applyLeave
} from "@/api/activiti";
import printJS from "print-js";
export default {
  name: "leave-notcache",
  data() {
    return {
      type: 0,
      loading: false, // 表单加载状态
      modalVisible: false,
      selectDate: null,
      dictType: this.$store.state.dict.leaveType,
      form: {
        // 添加或编辑表单对象初始化数据
        type: 0,
        title: "",
        description: "",
        startDate: "",
        endDate: "",
        duration: 0
      },
      formValidate: {
        // 表单验证规则
        title: [{ required: true, message: "标题不能为空", trigger: "blur" }],
        description: [
          { required: true, message: "原因不能为空", trigger: "blur" }
        ]
      },
      errorMsg: "",
      submitLoading: false, // 添加或编辑提交状态
      processList: [],
      backRoute: "",
      procDefId: "",
      options: {
        shortcuts: [
          {
            text: "1 天",
            value() {
              const end = new Date();
              const start = new Date();
              end.setTime(start.getTime());
              return [start, end];
            }
          },
          {
            text: "2 天",
            value() {
              const end = new Date();
              const start = new Date();
              end.setTime(start.getTime() + 3600 * 1000 * 24 * 1);
              return [start, end];
            }
          },
          {
            text: "3 天",
            value() {
              const end = new Date();
              const start = new Date();
              end.setTime(start.getTime() + 3600 * 1000 * 24 * 2);
              return [start, end];
            }
          },
          {
            text: "1 周",
            value() {
              const end = new Date();
              const start = new Date();
              end.setTime(start.getTime() + 3600 * 1000 * 24 * 6);
              return [start, end];
            }
          },
          {
            text: "1 个月",
            value() {
              const end = new Date();
              const start = new Date();
              end.setTime(start.getTime() + 3600 * 1000 * 24 * 29);
              return [start, end];
            }
          }
        ]
      }
    };
  },
  methods: {
    init() {
      this.type = this.$route.query.type;
      this.backRoute = this.$route.query.backRoute;
      this.procDefId = this.$route.query.procDefId;
      if (this.type != 0) {
        this.form.id = this.$route.query.id;
        this.getData();
      } else {
        this.$refs.form.resetFields();
        this.selectDate = [];
      }
    },
    selectDateRange(v) {
      if (v[0]) {
        this.form.startDate = v[0];
        this.form.endDate = v[1];
        let date1 = this.stringToDate(this.form.startDate);
        let date2 = this.stringToDate(this.form.endDate);
        let date = date2.getTime() - date1.getTime();
        let hours = Math.round(date / (60 * 60 * 1000));
        this.form.duration = hours;
      } else {
        this.form.startDate = "";
        this.form.endDate = "";
        this.form.duration = 0;
      }
    },
    stringToDate(str) {
      let tempStrs = str.split(" ");
      let dateStrs = tempStrs[0].split("-");
      let year = parseInt(dateStrs[0], 10);
      let month = parseInt(dateStrs[1], 10) - 1;
      let day = parseInt(dateStrs[2], 10);
      let timeStrs = tempStrs[1].split(":");
      let hour = parseInt(timeStrs[0], 10);
      let minute = parseInt(timeStrs[1], 10);
      let date = new Date(year, month, day, hour, minute, 0);
      return date;
    },
    getData() {
      this.loading = true;
      getLeaveData(this.form.id).then(res => {
        this.loading = false;
        if (res.success == true) {
          // 转换null为""
          let v = res.result;
          for (let attr in v) {
            if (v[attr] == null) {
              v[attr] = "";
            }
          }
          let str = JSON.stringify(v);
          let data = JSON.parse(str);
          this.form = data;
          this.selectDate = [];
          this.selectDate[0] = data.startDate;
          this.selectDate[1] = data.endDate;
        }
      });
    },
    handelSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (!this.form.startDate) {
            this.errorMsg = "请选择时间范围";
            return;
          } else {
            this.errorMsg = "";
          }
          this.submitLoading = true;
          if (this.type == 0) {
            // 添加 避免编辑后传入id等数据 记得删除
            delete this.form.id;
            this.form.procDefId = this.procDefId;
            addLeave(this.form).then(res => {
              this.submitLoading = false;
              if (res.success == true) {
                this.$Message.success("操作成功");
                this.closeCurrentPage();
              }
            });
          } else if (this.type == 1) {
            // 编辑
            updateLeave(this.form).then(res => {
              this.submitLoading = false;
              if (res.success == true) {
                this.$Message.success("操作成功");
                this.closeCurrentPage();
              }
            });
          }
        }
      });
    },
    handelPrint() {
      let data = [];
      let leave = {
        申请人: this.form.createBy,
        请假类型: this.form.type,
        标题: this.form.title,
        原因: this.form.description,
        请假开始时间: this.form.startDate,
        请假结束时间: this.form.endDate,
        持续时长: this.form.duration + " 小时"
      };
      data.push(leave);
      printJS({
        printable: data,
        type: "json",
        header: "请假申请表",
        gridStyle: "border: 1px solid lightgray;text-align:center",
        properties: [
          "申请人",
          "请假类型",
          "标题",
          "原因",
          "请假开始时间",
          "请假结束时间",
          "持续时长"
        ]
      });
    },
    handelCancel() {
      this.closeCurrentPage();
    },
    // 关闭当前页面
    closeCurrentPage() {
      this.$store.commit("removeTag", "leave");
      localStorage.pageOpenedList = JSON.stringify(
        this.$store.state.app.pageOpenedList
      );
      this.$router.push({
        name: this.backRoute
      });
    }
  },
  mounted() {
    this.init();
  },
  watch: {
    // 监听路由变化
    $route(to, from) {
      if (to.name == "leave") {
        this.getDictData();
        this.type = this.$route.query.type;
        this.backRoute = this.$route.query.backRoute;
        this.procDefId = this.$route.query.procDefId;
        if (this.type != 0) {
          this.form.id = this.$route.query.id;
          this.getData();
        } else {
          this.$refs.form.resetFields();
          this.selectDate = [];
        }
      }
    }
  }
};
</script>