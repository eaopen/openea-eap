<style lang="less">
@import "./searchTable.less";
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card>
          <Row>
            <Form ref="searchForm" :model="searchForm" inline :label-width="70" class="search-form">
              <Form-item label="名称" prop="name">
                <Input type="text" v-model="searchForm.name" placeholder="请输入" clearable style="width: 200px"/>
              </Form-item>
              <Form-item label="状态" prop="status">
                <Select v-model="searchForm.status" placeholder="请选择" clearable style="width: 200px">
                  <Option value="0">正常</Option>
                  <Option value="-1">禁用</Option>
                </Select>
              </Form-item>
              <Form-item label="创建时间">
                <DatePicker v-model="selectDate" type="daterange" format="yyyy-MM-dd" clearable @on-change="selectDateRange" placeholder="选择起始时间" style="width: 200px"></DatePicker>
              </Form-item>
              <Form-item style="margin-left:-35px;" class="br">
                <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
                <Button @click="handleReset" >重置</Button>
              </Form-item>
            </Form>
          </Row>
          <Row class="operation">
            <Button @click="add" type="primary" icon="md-add">添加</Button>
            <Button @click="delAll" icon="md-trash">批量删除</Button>
            <Button @click="getDataList" icon="md-refresh">刷新</Button>
            <circleLoading v-if="operationLoading"/>
          </Row>
          <Row>
            <Alert show-icon>
              已选择 <span class="select-count">{{selectCount}}</span> 项
              <a class="select-clear" @click="clearSelectAll">清空</a>
            </Alert>
          </Row>
          <Row>
            <Table :loading="loading" border :columns="columns" :data="data" sortable="custom" @on-sort-change="changeSort" @on-selection-change="showSelect" ref="table"></Table>
          </Row>
          <Row type="flex" justify="end" class="page">
            <Page :current="searchForm.pageNumber" :total="total" :page-size="searchForm.pageSize" @on-change="changePage" @on-page-size-change="changePageSize" :page-size-opts="[10,20,50]" size="small" show-total show-elevator show-sizer></Page>
          </Row>
        </Card>
      </Col>
    </Row>

    <Modal :title="modalTitle" v-model="modalVisible" :mask-closable='false' :width="500" :styles="{top: '30px'}">
      <Form ref="form" :model="form" :label-width="70" :rules="formValidate">
        <FormItem label="名称" prop="name">
          <Input v-model="form.name"/>
        </FormItem>
        <FormItem label="邮箱" prop="email">
          <Input v-model="form.email"/>
        </FormItem>
        <FormItem label="手机号" prop="mobile">
          <Input v-model="form.mobile"/>
        </FormItem>
        <FormItem label="性别" prop="sex">
          <RadioGroup v-model="form.sex">
            <Radio :label="1">男</Radio>
            <Radio :label="0">女</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="类型" prop="type">
          <Select v-model="form.type" placeholder="请选择">
            <Option :value="0">类型1</Option>
            <Option :value="1">类型2</Option>
          </Select>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="modalVisible=false">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="handelSubmit">提交</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import circleLoading from "@/views/my-components/circle-loading.vue";
export default {
  name: "search-table",
  components: {
    circleLoading
  },
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error("密码长度不得小于6位"));
      } else {
        callback();
      }
    };
    const validateMobile = (rule, value, callback) => {
      var reg = /^[1][3,4,5,6,7,8][0-9]{9}$/;
      if (!reg.test(value)) {
        callback(new Error("手机号格式错误"));
      } else {
        callback();
      }
    };
    return {
      loading: true, // 表单加载状态
      operationLoading: false, // 操作加载状态
      selectCount: 0, // 多选计数
      selectList: [], // 多选数据
      searchForm: {
        // 搜索框对应data对象
        name: "",
        status: "",
        pageNumber: 1, // 当前页数
        pageSize: 10, // 页面大小
        sort: "createTime", // 默认排序字段
        order: "desc", // 默认排序方式
        startDate: "", // 起始时间
        endDate: "" // 终止时间
      },
      selectDate: null, // 选择日期绑定modal
      modalType: 0, // 添加或编辑标识
      modalVisible: false, // 添加或编辑显示
      modalTitle: "", // 添加或编辑标题
      form: {
        // 添加或编辑表单对象初始化数据
        sex: 1,
        type: 0,
        name: "",
        mobile: "",
        email: ""
      },
      formValidate: {
        // 表单验证规则
        name: [{ required: true, message: "不能为空", trigger: "blur" }],
        mobile: [
          { required: true, message: "手机号不能为空", trigger: "blur" },
          { validator: validateMobile, trigger: "blur" }
        ],
        email: [
          { required: true, message: "请输入邮箱地址" },
          { type: "email", message: "邮箱格式不正确" }
        ]
      },
      submitLoading: false, // 添加或编辑提交状态
      columns: [
        // 表头
        {
          type: "selection",
          width: 60,
          align: "center",
          fixed: "left"
        },
        {
          type: "index",
          width: 60,
          align: "center",
          fixed: "left"
        },
        {
          title: "名称",
          key: "name",
          minWidth: 150,
          sortable: true,
          fixed: "left"
        },
        {
          title: "手机",
          key: "mobile",
          width: 120,
          sortable: true
        },
        {
          title: "邮箱",
          key: "email",
          width: 180,
          sortable: true
        },
        {
          title: "性别",
          key: "sex",
          width: 100,
          align: "center",
          render: (h, params) => {
            let re = "";
            if (params.row.sex == 1) {
              re = "男";
            } else if (params.row.sex == 0) {
              re = "女";
            }
            return h("div", re);
          }
        },
        {
          title: "类型",
          key: "type",
          width: 120,
          align: "center",
          render: (h, params) => {
            let re = "";
            if (params.row.type == 0) {
              re = "类型1";
            } else if (params.row.type == 1) {
              re = "类型2";
            }
            return h("div", re);
          }
        },
        {
          title: "状态",
          key: "status",
          align: "center",
          width: 150,
          render: (h, params) => {
            let re = "";
            if (params.row.status == 0) {
              return h("div", [
                h(
                  "Badge",
                  {
                    props: {
                      status: "success",
                      text: "正常启用"
                    }
                  }
                )
              ]);
            } else if (params.row.status == -1) {
              return h("div", [
                h(
                  "Badge",
                  {
                    props: {
                      status: "error",
                      text: "禁用"
                    }
                  }
                )
              ]);
            }
          },
          filters: [
            {
              label: "正常启用",
              value: 0
            },
            {
              label: "禁用",
              value: -1
            }
          ],
          filterMultiple: false,
          filterMethod(value, row) {
            if (value == 0) {
              return row.status == 0;
            } else if (value == -1) {
              return row.status == -1;
            }
          }
        },
        {
          title: "创建时间",
          key: "createTime",
          width: 150,
          sortable: true,
          sortType: "desc"
        },
        {
          title: "操作",
          key: "action",
          width: 200,
          align: "center",
          fixed: "right",
          render: (h, params) => {
            let enableOrDisable = "";
            if (params.row.status == 0) {
              enableOrDisable = h(
                "Button",
                {
                  props: {
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.disable(params.row);
                    }
                  }
                },
                "禁用"
              );
            } else {
              enableOrDisable = h(
                "Button",
                {
                  props: {
                    type: "success",
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.enable(params.row);
                    }
                  }
                },
                "启用"
              );
            }
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.edit(params.row);
                    }
                  }
                },
                "编辑"
              ),
              enableOrDisable,
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.remove(params.row);
                    }
                  }
                },
                "删除"
              )
            ]);
          }
        }
      ],
      data: [], // 表单数据
      total: 0 // 表单数据总数
    };
  },
  methods: {
    init() {
      this.getDataList();
    },
    changePage(v) {
      this.searchForm.pageNumber = v;
      this.getDataList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      this.searchForm.pageSize = v;
      this.getDataList();
    },
    selectDateRange(v) {
      if (v) {
        this.searchForm.startDate = v[0];
        this.searchForm.endDate = v[1];
      }
    },
    getDataList() {
      this.loading = true;
      // 带多条件搜索参数获取表单数据 请自行修改接口
      // this.getRequest("请求路径", this.searchForm).then(res => {
      //   this.loading = false;
      //   if (res.success == true) {
      //     this.data = res.result.content;
      //     this.total = res.result.totalElements;
      //   }
      // });
      // 以下为模拟数据
      this.data = [
        {
          id: "1",
          name: "XBoot",
          mobile: "12345678901",
          email: "1012139570@qq.com",
          sex: 1,
          type: 0,
          status: 0,
          createTime: "2018-08-08 00:08:00"
        },
        {
          id: "2",
          name: "Exrick",
          mobile: "12345678901",
          email: "1012139570@qq.com",
          sex: 0,
          type: 1,
          status: -1,
          createTime: "2018-08-08 00:08:00"
        }
      ];
      this.total = this.data.length;
      this.loading = false;
    },
    handleSearch() {
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.getDataList();
    },
    handleReset() {
      this.$refs.searchForm.resetFields();
      this.searchForm.pageNumber = 1;
      this.searchForm.pageSize = 10;
      this.selectDate = null;
      this.searchForm.startDate = "";
      this.searchForm.endDate = "";
      // 重新加载数据
      this.getDataList();
    },
    changeSort(e) {
      this.searchForm.sort = e.key;
      this.searchForm.order = e.order;
      if (e.order == "normal") {
        this.searchForm.order = "";
      }
      this.getDataList();
    },
    handelSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          if (this.modalType == 0) {
            // 添加 避免编辑后传入id等数据 记得删除
            delete this.form.id;
            delete this.form.status;
            // this.postRequest("请求路径", this.form).then(res => {
            //   this.submitLoading = false;
            //   if (res.success == true) {
            //     this.$Message.success("操作成功");
            //     this.getDataList();
            //     this.modalVisible = false;
            //   }
            // });
            // 模拟成功
            this.submitLoading = false;
            this.$Message.success("操作成功");
            this.modalVisible = false;
          } else if (this.modalType == 1) {
            // 编辑
            // this.postRequest("请求路径", this.form).then(res => {
            //   this.submitLoading = false;
            //   if (res.success == true) {
            //     this.$Message.success("操作成功");
            //     this.getDataList();
            //     this.modalVisible = false;
            //   }
            // });
            // 模拟成功
            this.submitLoading = false;
            this.$Message.success("操作成功");
            this.modalVisible = false;
          }
        }
      });
    },
    add() {
      this.modalType = 0;
      this.modalTitle = "添加";
      this.$refs.form.resetFields();
      this.modalVisible = true;
    },
    edit(v) {
      this.modalType = 1;
      this.modalTitle = "编辑";
      this.$refs.form.resetFields();
      // 转换null为""
      for (let attr in v) {
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);
      let data = JSON.parse(str);
      this.form = data;
      this.modalVisible = true;
    },
    enable(v) {
      this.$Modal.confirm({
        title: "确认启用",
        // 记得确认修改此处
        content: "您确认要启用 " + v.name + " ?",
        onOk: () => {
          // this.operationLoading = true;
          // this.postRequest("请求路径，如/deleteByIds/" + v.id).then(res => {
          // this.operationLoading = false;
          //   if (res.success == true) {
          //     this.$Message.success("操作成功");
          //     this.getDataList();
          //   }
          // });
          //模拟成功
          this.$Message.success("操作成功");
          this.getDataList();
        }
      });
    },
    disable(v) {
      this.$Modal.confirm({
        title: "确认禁用",
        // 记得确认修改此处
        content: "您确认要禁用 " + v.name + " ?",
        onOk: () => {
          // this.operationLoading = true;
          // this.postRequest("请求路径，如/deleteByIds/" + v.id).then(res => {
          // this.operationLoading = false;
          //   if (res.success == true) {
          //     this.$Message.success("操作成功");
          //     this.getDataList();
          //   }
          // });
          //模拟成功
          this.$Message.success("操作成功");
          this.getDataList();
        }
      });
    },
    remove(v) {
      this.$Modal.confirm({
        title: "确认删除",
        // 记得确认修改此处
        content: "您确认要删除 " + v.name + " ?",
        onOk: () => {
          // 删除
          // this.operationLoading = true;
          // this.deleteRequest("请求地址，如/deleteByIds/" + v.id).then(res => {
          // this.operationLoading = false;
          //   if (res.success == true) {
          //     this.$Message.success("操作成功");
          //     this.getDataList();
          //   }
          // });
          // 模拟请求成功
          this.$Message.success("操作成功");
          this.getDataList();
        }
      });
    },
    showSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("您还未选择要删除的数据");
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
          // 批量删除
          // this.operationLoading = true;
          // this.deleteRequest("请求地址，如/deleteByIds/" + ids).then(res => {
          // this.operationLoading = false;
          //   if (res.success == true) {
          //     this.$Message.success("操作成功");
          //     this.clearSelectAll();
          //     this.getDataList();
          //   }
          // });
          // 模拟请求成功
          this.$Message.success("操作成功");
          this.clearSelectAll();
          this.getDataList();
        }
      });
    }
  },
  mounted() {
    this.init();
  }
};
</script>