<style lang="less">
@import "./processManage.less";
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card>
          <p slot="title">{{processName}} 流程节点设置</p>
          <Row type="flex" justify="start" class="code-row-bg">
            <Col span="6">
              <Alert show-icon>
                当前选择编辑：
                <span class="select-title">{{editTitle}}</span>
                <a class="select-clear" v-if="form.id" @click="cancelEdit">取消选择</a>
              </Alert>
              <div class="tree-bar">
                <Tree
                  ref="tree"
                  :data="data"
                  :render="renderContent"
                  @on-select-change="selectTree"
                ></Tree>
              </div>
              <Spin size="large" fix v-if="loading"></Spin>
            </Col>
            <Col span="9">
              <Form
                ref="form"
                :model="form"
                :label-width="90"
                :rules="formValidate"
                style="position:relative"
              >
                <FormItem label="节点名称" prop="title">{{form.title}}</FormItem>
                <Form-item label="节点类型" prop="type">
                  <Select v-model="form.type" disabled placeholder>
                    <Option
                      v-for="(item, i) in dictNodeType"
                      :key="i"
                      :value="item.value"
                    >{{item.title}}</Option>
                  </Select>
                </Form-item>
                <div v-show="form.type==1">
                  <FormItem label="可审批人员">
                    <Checkbox v-model="chooseRole" label="0" @on-change="clickRole">
                      <Icon type="md-people" size="14" style="margin:0 2px 2px 0"></Icon>
                      <span>根据角色选择</span>
                    </Checkbox>
                    <Checkbox v-model="chooseDepartment" label="1" @on-change="clickDepartment">
                      <Icon type="ios-people" style="margin:0 2px 2px 0"></Icon>
                      <span>部门负责人</span>
                    </Checkbox>
                    <Checkbox v-model="chooseUser" label="1" @on-change="clickUser">
                      <Icon type="md-person" style="margin:0 2px 2px 0"></Icon>
                      <span>直接选择人员</span>
                    </Checkbox>
                  </FormItem>
                  <FormItem label="选择角色" v-if="chooseRole">
                    <Select v-model="form.roles" multiple>
                      <Option
                        v-for="item in roleList"
                        :value="item.id"
                        :key="item.id"
                        :label="item.name"
                      >
                        <span style="margin-right:10px;">{{ item.name }}</span>
                        <span style="color:#ccc;">{{ item.description }}</span>
                      </Option>
                    </Select>
                  </FormItem>
                  <div v-show="chooseDepartment">
                    <FormItem label="选择部门">
                      <department-tree-choose
                        multiple
                        width="250px"
                        @on-change="handleSelectDepTree"
                        ref="depTree"
                      ></department-tree-choose>
                    </FormItem>
                  </div>
                  <div v-show="chooseUser">
                    <FormItem label="选择用户">
                      <user-choose @on-change="handleSelectUser" ref="user"></user-choose>
                    </FormItem>
                  </div>
                </div>
                <Form-item class="br">
                  <Button
                    type="primary"
                    :loading="submitLoading"
                    @click="handelSubmit"
                    icon="ios-create-outline"
                    :disabled="form.type!=1"
                  >保存并提交</Button>
                  <Button @click="closeCurrentPage">关闭</Button>
                </Form-item>
                <Spin size="large" fix v-if="nodeLoading"></Spin>
              </Form>
            </Col>
          </Row>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import { getProcessNode, editNodeUser } from "@/api/activiti";
import { getAllRoleList, getDictDataByType } from "@/api/index";
import userChoose from "@/views/my-components/xboot/user-choose";
import departmentTreeChoose from "@/views/my-components/xboot/department-tree-choose";
export default {
  name: "process_node_edit",
  components: {
    userChoose,
    departmentTreeChoose
  },
  data() {
    return {
      loading: false, // 表单加载状态
      nodeLoading: false,
      data: [],
      editTitle: "",
      selectUsers: [],
      userModalVisible: false,
      modalTitle: "", // 添加或编辑标题
      form: {
        // 添加或编辑表单对象初始化数据
        title: "",
        content: "",
        type: null,
        roles: [],
        departmentIds: []
      },
      roleList: [],
      formValidate: {
        // 表单验证规则
      },
      submitLoading: false, // 添加或编辑提交状态
      chooseRole: false,
      chooseUser: false,
      chooseDepartment: false,
      dictNodeType: [],
      backRoute: "",
      processName: "",
      processId: ""
    };
  },
  methods: {
    init() {
      this.getDictDataType();
      this.getRoleList();
      this.backRoute = this.$route.query.backRoute;
      this.processId = this.$route.query.id;
      this.processName = this.$route.query.name;
    },
    getDictDataType() {
      getDictDataByType("process_node_type").then(res => {
        if (res.success) {
          this.dictNodeType = res.result;
          this.getData();
        }
      });
    },
    getRoleList() {
      getAllRoleList().then(res => {
        if (res.success == true) {
          this.roleList = res.result;
        }
      });
    },
    getData() {
      this.loading = true;
      getProcessNode(this.processId).then(res => {
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
          data.forEach(e => {
            this.dictNodeType.forEach(t => {
              t.value = Number(t.value);
              if (!e.title && e.type == t.value) {
                e.title = t.title;
              }
            });
          });
          this.data = data;
        }
      });
    },
    renderContent(h, { root, node, data }) {
      let color = "",
        word = "";
      if (data.type == 0) {
        color = "#47cb89";
        word = "开";
      } else if (data.type == 1) {
        color = "#2db7f5";
        word = "审";
      } else if (data.type == 2) {
        word = "结";
      } else {
        color = "#f90";
        word = "其他";
      }
      return h(
        "span",
        {
          style: {
            display: "inline-block",
            cursor: "pointer"
          },
          on: {
            click: () => {
              this.selectTree(data);
            }
          }
        },
        [
          h("span", [
            h(
              "Avatar",
              {
                props: {
                  size: "small"
                },
                style: {
                  background: color,
                  "margin-right": "5px"
                }
              },
              word
            ),
            h(
              "span",
              {
                class: {
                  "ivu-tree-title": true,
                  "ivu-tree-title-selected": data.id == this.form.id
                }
              },
              data.title
            )
          ])
        ]
      );
    },
    selectTree(v) {
      if (v && v.id != this.form.id) {
        // 转换null为""
        for (let attr in v) {
          if (v[attr] == null) {
            v[attr] = "";
          }
        }
        let str = JSON.stringify(v);
        let data = JSON.parse(str);
        this.selectUsers = data.users;
        this.editTitle = data.title;
        // 回显用户
        if (data.users && data.users.length > 0) {
          this.chooseUser = true;
          this.$refs.user.setData(data.users);
        } else {
          this.chooseUser = false;
          this.$refs.user.setData([]);
        }
        // 回显角色
        if (data.roles && data.roles.length > 0) {
          this.chooseRole = true;
          let roleIds = [];
          data.roles.forEach(e => {
            roleIds.push(e.id);
          });
          data.roles = roleIds;
        } else {
          this.chooseRole = false;
        }
        // 回显部门
        if (data.departments && data.departments.length > 0) {
          this.chooseDepartment = true;
          let departmentIds = [],
            title = "";
          data.departments.forEach(e => {
            departmentIds.push(e.id);
            if (title == "") {
              title = e.title;
            } else {
              title = title + "、" + e.title;
            }
          });
          this.$refs.depTree.setData(departmentIds, title);
          data.departmentIds = departmentIds;
        } else {
          this.chooseDepartment = false;
          this.$refs.depTree.setData([], "");
        }
        this.form = data;
      } else {
        this.cancelEdit();
      }
    },
    cancelEdit() {
      let data = this.$refs.tree.getSelectedNodes()[0];
      if (data) {
        data.selected = false;
      }
      this.$refs.form.resetFields();
      this.form.id = "";
      delete this.form.id;
      this.editTitle = "";
    },
    handelSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          // 用户id数据
          let ids = [];
          this.selectUsers.forEach(e => {
            ids += e.id + ",";
          });
          if (ids.length > 0) {
            ids = ids.substring(0, ids.length - 1);
          }
          this.form.nodeId = this.form.id;
          if (this.chooseUser) {
            this.form.userIds = ids;
          } else {
            this.form.userIds = [];
          }
          if (this.chooseRole) {
            this.form.roleIds = this.form.roles;
          } else {
            this.form.roleIds = [];
          }
          if (!this.chooseDepartment) {
            this.form.departmentIds = [];
          }
          editNodeUser(this.form).then(res => {
            this.submitLoading = false;
            if (res.success) {
              this.$Message.success("操作成功");
              this.getData();
            }
          });
        }
      });
    },
    clickRole(v) {
      this.chooseRole = v;
    },
    clickUser(v) {
      this.chooseUser = v;
    },
    clickDepartment(v) {
      this.chooseDepartment = v;
    },
    handleSelectUser(v) {
      this.selectUsers = v;
    },
    handleSelectDepTree(v) {
      this.form.departmentIds = v;
    },
    // 关闭当前页面
    closeCurrentPage() {
      this.$store.commit("removeTag", "process_node_edit");
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
      if (to.name == "process_node_edit") {
        this.init();
      }
    }
  }
};
</script>