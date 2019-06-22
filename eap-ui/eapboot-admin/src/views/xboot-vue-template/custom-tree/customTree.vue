<style lang="less">
@import "./customTree.less";
</style>
<template>
  <div class="search">
    <Card>
      <Row class="operation">
        <Button @click="add" type="primary" icon="md-add">添加子节点</Button>
        <Button @click="addRoot" icon="md-add">添加一级节点</Button>
        <Button @click="delAll" icon="md-trash">批量删除</Button>
        <Button @click="getParentList" icon="md-refresh">刷新</Button>
        <i-switch v-model="strict" size="large" style="margin-left:5px">
          <span slot="open">级联</span>
          <span slot="close">单选</span>
        </i-switch>
      </Row>
      <Row type="flex" justify="start" class="code-row-bg">
        <Col span="6">
          <Alert show-icon>
            当前选择编辑：
            <span class="select-title">{{editTitle}}</span>
            <a class="select-clear" v-if="form.id" @click="cancelEdit">取消选择</a>
          </Alert>
          <Input
            v-model="searchKey"
            suffix="ios-search"
            @on-change="search"
            placeholder="输入节点名搜索"
            clearable
          />
          <div class="custom-tree-bar" :style="{maxHeight: maxHeight}">
            <Tree
              ref="tree"
              :data="data"
              :render="renderContent"
              :load-data="loadData"
              show-checkbox
              @on-check-change="changeSelect"
              :check-strictly="!strict"
            ></Tree>
          </div>
          <Spin size="large" fix v-if="loading"></Spin>
        </Col>
        <Col span="9" style="margin-left:10px">
          <Form ref="form" :model="form" :label-width="85" :rules="formValidate">
            <FormItem label="上级节点" prop="parentTitle">
              <Poptip trigger="click" placement="right-start" title="选择上级节点" width="250">
                <Input v-model="form.parentTitle" readonly/>
                <div slot="content" style="position:relative;min-height:5vh">
                  <Tree :data="dataEdit" :load-data="loadData" @on-select-change="selectTreeEdit"></Tree>
                  <Spin size="large" fix v-if="loadingEdit"></Spin>
                </div>
              </Poptip>
            </FormItem>
            <FormItem label="节点名称" prop="title">
              <Input v-model="form.title"/>
            </FormItem>
            <FormItem label="排序值" prop="sortOrder">
              <InputNumber :max="1000" :min="0" v-model="form.sortOrder"></InputNumber>
              <span style="margin-left:5px">值越小越靠前，支持小数</span>
            </FormItem>
            <FormItem label="是否启用" prop="status">
              <i-switch size="large" v-model="form.status" :true-value="0" :false-value="-1">
                <span slot="open">启用</span>
                <span slot="close">禁用</span>
              </i-switch>
            </FormItem>
            <FormItem label="是否为父节点">
              <i-switch v-model="form.isParent">
                <span slot="open">是</span>
                <span slot="close">否</span>
              </i-switch>
            </FormItem>
            <Form-item>
              <Button
                @click="submitEdit"
                :loading="submitLoading"
                type="primary"
                icon="ios-create-outline"
                style="margin-right:5px"
              >修改并保存</Button>
              <Button @click="handleReset">重置</Button>
            </Form-item>
          </Form>
        </Col>
      </Row>
    </Card>

    <Modal :title="modalTitle" v-model="modalVisible" :mask-closable="false" :width="500">
      <Form ref="formAdd" :model="formAdd" :label-width="85" :rules="formValidate">
        <div v-if="showParent">
          <FormItem label="上级节点：">{{form.title}}</FormItem>
        </div>
        <FormItem label="节点名称" prop="title">
          <Input v-model="formAdd.title"/>
        </FormItem>
        <FormItem label="排序值" prop="sortOrder">
          <InputNumber :max="1000" :min="0" v-model="formAdd.sortOrder"></InputNumber>
          <span style="margin-left:5px">值越小越靠前，支持小数</span>
        </FormItem>
        <FormItem label="是否启用" prop="status">
          <i-switch size="large" v-model="formAdd.status" :true-value="0" :false-value="-1">
            <span slot="open">启用</span>
            <span slot="close">禁用</span>
          </i-switch>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancelAdd">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="submitAdd">提交</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
export default {
  name: "custom-tree",
  data() {
    return {
      loading: false, // 树加载状态
      maxHeight: "500px",
      strict: true,
      loadingEdit: false, // 编辑上级树加载状态
      modalVisible: false, // 添加显示
      selectList: [], // 多选数据
      selectCount: 0, // 多选计数
      showParent: false, // 显示上级标识
      modalTitle: "", // 添加标题
      editTitle: "", // 编辑节点名称
      searchKey: "", // 搜索树
      form: {
        // 编辑对象初始化数据
        id: "",
        parentId: "",
        parentTitle: "",
        sortOrder: 0,
        status: 0
      },
      formAdd: {
        // 添加对象初始化数据
        title: ""
      },
      formValidate: {
        // 表单验证规则
        title: [{ required: true, message: "名称不能为空", trigger: "blur" }]
      },
      submitLoading: false,
      data: [],
      dataEdit: []
    };
  },
  methods: {
    init() {
      // 初始化一级节点
      this.getParentList();
      // 初始化一级节点为编辑上级节点使用
      this.getParentListEdit();
    },
    renderContent(h, { root, node, data }) {
      // 自定义render函数 这里主要通过数据中type字段判断 演示使用 当然你可以自定义业务逻辑
      if (data.type == "1") {
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
              h("Icon", {
                props: {
                  type: data.icon,
                  size: "16",
                  color: data.color
                },
                style: {
                  "margin-right": "8px",
                  "margin-bottom": "3px"
                }
              }),
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
      } else if (data.type == "2") {
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
              h("img", {
                attrs: {
                  src: require("@/assets/vip.png"),
                  alt: "..."
                },
                style: {
                  cursor: "pointer",
                  width: "17px",
                  height: "17px",
                  "margin-right": "5px"
                }
              }),
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
      } else if (data.type == "3") {
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
                    background: data.color,
                    "margin-right": "5px"
                  }
                },
                data.word
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
      } else {
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
        );
      }
    },
    getParentList() {
      // this.loading = true;
      // this.getRequest("一级数据请求路径，如/tree/getByParentId/0").then(res => {
      //   this.loading = false;
      //   if (res.success == true) {
      //     res.result.forEach(function(e) {
      //       if (e.isParent) {
      //         e.loading = false;
      //         e.children = [];
      //       }
      //     });
      //     this.data = res.result;
      //   }
      // });
      // 模拟请求成功
      this.data = [
        {
          title: "自定义图标",
          id: "1",
          type: "1",
          icon: "logo-apple",
          color: "",
          parentId: "0",
          parentTitle: "一级节点",
          status: 0,
          loading: false,
          children: [
            {
              title: "带颜色的自定义图标",
              id: "2",
              type: "1",
              icon: "logo-android",
              color: "#97c03d",
              parentId: "1",
              status: 0,
              parentTitle: "自定义图标"
            }
          ]
        },
        {
          title: "自定义图片",
          id: "4",
          type: "2",
          parentId: "0",
          parentTitle: "一级节点",
          status: 0
        },
        {
          title: "自定义iView组件",
          id: "5",
          type: "3",
          word: "E",
          color: "orange",
          parentId: "0",
          parentTitle: "一级节点",
          status: -1,
          loading: false,
          children: [
            {
              title: "自定义iView组件",
              id: "2",
              type: "3",
              word: "字",
              color: "#c601c6",
              parentId: "1",
              status: 0,
              parentTitle: "自定义iView组件"
            }
          ]
        }
      ];
    },
    getParentListEdit() {
      // this.loadingEdit = true;
      // this.getRequest("/tree/getByParentId/0").then(res => {
      //   this.loadingEdit = false;
      //   if (res.success == true) {
      //     res.result.forEach(function(e) {
      //       if (e.isParent) {
      //         e.loading = false;
      //         e.children = [];
      //       }
      //     });
      //     // 头部加入一级
      //     let first = {
      //       id: "0",
      //       title: "一级节点"
      //     };
      //     res.result.unshift(first);
      //     this.dataEdit = res.result;
      //   }
      // });
      // 模拟请求成功
      this.dataEdit = [
        {
          title: "自定义图标",
          id: "1",
          type: "1",
          icon: "logo-apple",
          color: "",
          parentId: "0",
          parentTitle: "一级节点",
          status: 0,
          loading: false,
          children: [
            {
              title: "带颜色的自定义图标",
              id: "2",
              type: "1",
              icon: "logo-android",
              color: "#97c03d",
              parentId: "1",
              status: 0,
              parentTitle: "自定义图标"
            }
          ]
        },
        {
          title: "自定义图片",
          id: "4",
          type: "2",
          parentId: "0",
          parentTitle: "一级节点",
          status: 0
        },
        {
          title: "自定义iView组件",
          id: "5",
          type: "3",
          word: "E",
          color: "orange",
          parentId: "0",
          parentTitle: "一级节点",
          status: -1,
          loading: false,
          children: [
            {
              title: "自定义iView组件",
              id: "2",
              type: "3",
              word: "字",
              color: "#c601c6",
              parentId: "1",
              status: 0,
              parentTitle: "自定义iView组件"
            }
          ]
        }
      ];
    },
    loadData(item, callback) {
      // 异步加载树子节点数据
      // this.getRequest("请求路径，如/tree/getByParentId/" + item.id).then(res => {
      //   if (res.success == true) {
      //     res.result.forEach(function(e) {
      //       if (e.isParent) {
      //         e.loading = false;
      //         e.children = [];
      //       }
      //     });
      //     callback(res.result);
      //   }
      // });
    },
    search() {
      // 搜索树
      if (this.searchKey) {
        // 模拟请求
        // this.loading = true;
        // this.getRequest("搜索请求路径", { title: this.searchKey }).then(res => {
        //   this.loading = false;
        //   if (res.success == true) {
        //     this.data = res.result;
        //   }
        // });
        // 模拟请求成功
        this.data = [
          {
            title: "这里需要请求后台接口",
            id: "1",
            parentId: "0",
            parentTitle: "一级节点",
            status: 0
          },
          {
            title: "所以这里是假数据",
            id: "4",
            parentId: "0",
            parentTitle: "一级节点",
            status: 0
          },
          {
            title: "我是被禁用的节点",
            id: "5",
            parentId: "0",
            parentTitle: "一级节点",
            status: -1
          }
        ];
      } else {
        // 为空重新加载
        this.getParentList();
      }
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
        this.form = data;
        this.editTitle = data.title;
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
    selectTreeEdit(v) {
      if (v.length > 0) {
        // 转换null为""
        for (let attr in v[0]) {
          if (v[0][attr] == null) {
            v[0][attr] = "";
          }
        }
        let str = JSON.stringify(v[0]);
        let data = JSON.parse(str);
        this.form.parentId = data.id;
        this.form.parentTitle = data.title;
      }
    },
    cancelAdd() {
      this.modalVisible = false;
    },
    handleReset() {
      this.$refs.form.resetFields();
      this.form.status = 0;
    },
    submitEdit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (!this.form.id) {
            this.$Message.warning("请先点击选择要修改的节点");
            return;
          }
          this.submitLoading = true;
          // 避免传入null字符串
          // this.postRequest("请求路径，如/tree/edit", this.form).then(res => {
          //   this.submitLoading = false;
          //   if (res.success == true) {
          //     this.$Message.success("编辑成功");
          //     this.init();
          //     this.modalVisible = false;
          //   }
          // });
          // 模拟成功
          this.submitLoading = false;
          this.$Message.success("编辑成功");
          this.modalVisible = false;
        }
      });
    },
    submitAdd() {
      this.$refs.formAdd.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          // this.postRequest("请求路径，如/tree/add", this.formAdd).then(res => {
          //   this.submitLoading = false;
          //   if (res.success == true) {
          //     this.$Message.success("添加成功");
          //     this.init();
          //     this.modalVisible = false;
          //   }
          // });
          // 模拟成功
          this.submitLoading = false;
          this.$Message.success("添加成功");
          this.modalVisible = false;
        }
      });
    },
    add() {
      if (this.form.id == "" || this.form.id == null) {
        this.$Message.warning("请先点击选择一个节点");
        return;
      }
      this.modalTitle = "添加子节点";
      this.showParent = true;
      this.formAdd = {
        parentId: this.form.id,
        sortOrder: 0,
        status: 0
      };
      this.modalVisible = true;
    },
    addRoot() {
      this.modalTitle = "添加一级节点";
      this.showParent = false;
      this.formAdd = {
        parentId: 0,
        sortOrder: 0,
        status: 0
      };
      this.modalVisible = true;
    },
    changeSelect(v) {
      this.selectCount = v.length;
      this.selectList = v;
    },
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("您还未勾选要删除的数据");
        return;
      }
      this.$Modal.confirm({
        title: "确认删除",
        content: "您确认要删除所选的 " + this.selectCount + " 条数据?",
        onOk: () => {
          let ids = "";
          this.selectList.forEach(function(e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          // this.deleteRequest("请求路径，如/tree/delByIds/" + ids).then(res => {
          //   if (res.success == true) {
          //     this.$Message.success("删除成功");
          //     this.selectList = [];
          //     this.selectCount = 0;
          //     this.cancelEdit();
          //     this.init();
          //   }
          // });
          // 模拟成功
          this.$Message.success("删除成功");
          this.selectList = [];
          this.selectCount = 0;
          this.cancelEdit();
        }
      });
    }
  },
  mounted() {
    // 计算高度
    let height = document.documentElement.clientHeight;
    this.maxHeight = Number(height - 287) + "px";
    this.init();
  }
};
</script>