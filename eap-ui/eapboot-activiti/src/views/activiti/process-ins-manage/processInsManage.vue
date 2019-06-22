<style lang="less">
@import "processInsManage.less";
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card>
          <Row>
            <Form ref="searchForm" :model="searchForm" inline :label-width="70" class="search-form">
              <Form-item label="流程名称" prop="name">
                <Input
                  type="text"
                  v-model="searchForm.name"
                  placeholder="请输入"
                  clearable
                  style="width: 200px"
                />
              </Form-item>
              <Form-item label="标识Key" prop="name">
                <Input
                  type="text"
                  v-model="searchForm.key"
                  placeholder="请输入"
                  clearable
                  style="width: 200px"
                />
              </Form-item>
              <Form-item style="margin-left:-35px;" class="br">
                <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
                <Button @click="handleReset">重置</Button>
              </Form-item>
            </Form>
          </Row>
          <Row class="operation">
            <Button @click="delAll" icon="md-trash">批量删除</Button>
            <Button @click="getDataList" icon="md-refresh">刷新</Button>
            <circleLoading v-if="operationLoading"/>
          </Row>
          <Row>
            <Alert show-icon>
              已选择
              <span class="select-count">{{selectCount}}</span> 项
              <a class="select-clear" @click="clearSelectAll">清空</a>
            </Alert>
          </Row>
          <Row>
            <Table
              :loading="loading"
              border
              :columns="columns"
              sortable="custom"
              :data="data"
              @on-selection-change="showSelect"
              ref="table"
            ></Table>
          </Row>
          <Row type="flex" justify="end" class="page">
            <Page
              :current="searchForm.pageNumber"
              :total="total"
              :page-size="searchForm.pageSize"
              @on-change="changePage"
              @on-page-size-change="changePageSize"
              :page-size-opts="[10,20,50]"
              size="small"
              show-total
              show-elevator
              show-sizer
            ></Page>
          </Row>
        </Card>
      </Col>
    </Row>

    <Modal
      :title="modalTitle"
      v-model="modalVisible"
      :mask-closable="false"
      :width="500"
      :styles="{top: '30px'}"
    >
      <Form ref="form" :model="form" :label-width="70" :rules="formValidate">
        <FormItem label="删除原因" prop="reason">
          <Input type="textarea" v-model="form.reason" :rows="4"/>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="handelCancel">取消</Button>
        <Button type="primary" :loading="submitLoading" @click="handelSubmit">提交</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import {
  getRunningProcess,
  updateInsStatus,
  deleteProcessIns
} from "@/api/activiti";
import circleLoading from "@/views/my-components/circle-loading.vue";
export default {
  name: "process-ins-manage",
  components: {
    circleLoading
  },
  data() {
    return {
      loading: true, // 表单加载状态
      operationLoading: false, // 操作加载状态
      selectCount: 0, // 多选计数
      selectList: [], // 多选数据
      searchForm: {
        // 搜索框对应data对象
        name: "",
        key: "",
        pageNumber: 1, // 当前页数
        pageSize: 10 // 页面大小
      },
      modalType: 0, // 添加或编辑标识
      modalVisible: false, // 添加或编辑显示
      modalTitle: "", // 添加或编辑标题
      form: {
        // 添加或编辑表单对象初始化数据
        reason: ""
      },
      formValidate: {
        // 表单验证规则
      },
      submitLoading: false, // 添加或编辑提交状态
      columns: [
        // 表头
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          type: "index",
          width: 60,
          align: "center"
        },
        {
          title: "流程实例ID",
          key: "id",
          width: 130,
          sortable: true
        },
        {
          title: "流程名称",
          key: "name",
          minWidth: 150,
          sortable: true
        },
        {
          title: "申请人",
          key: "applyer",
          minWidth: 150,
          sortable: true
        },
        {
          title: "标识Key",
          key: "key",
          width: 150,
          sortable: true
        },
        {
          title: "版本",
          key: "version",
          width: 140,
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
          title: "当前环节",
          key: "currTaskName",
          width: 160,
          sortable: true
        },
        {
          title: "状态",
          key: "isSuspended",
          align: "center",
          width: 110,
          render: (h, params) => {
            if (!params.row.isSuspended) {
              return h("div", [
                h(
                  "Badge",
                  {
                    props: {
                      status: "success",
                      text: "已激活"
                    }
                  }
                )
              ]);
            } else if (params.row.isSuspended) {
              return h("div", [
                h(
                  "Badge",
                  {
                    props: {
                      status: "error",
                      text: "已挂起"
                    }
                  }
                )
              ]);
            }
          },
          filters: [
            {
              label: "已激活",
              value: false
            },
            {
              label: "已挂起",
              value: true
            }
          ],
          filterMultiple: false,
          filterMethod(value, row) {
            if (value) {
              return row.isSuspended == true;
            } else if (!value) {
              return row.isSuspended == false;
            }
          }
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 280,
          fixed: "right",
          render: (h, params) => {
            let suspendOrActive = "";
            if (params.row.isSuspended) {
              // 挂起可激活
              suspendOrActive = h(
                "Button",
                {
                  props: {
                    type: "success",
                    size: "small",
                    icon: "md-play"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.editStatus(1, params.row);
                    }
                  }
                },
                "激活"
              );
            } else {
              // 激活可挂起
              suspendOrActive = h(
                "Button",
                {
                  props: {
                    type: "warning",
                    size: "small",
                    icon: "md-pause"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.editStatus(0, params.row);
                    }
                  }
                },
                "挂起"
              );
            }
            return h("div", [
              suspendOrActive,
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
                      this.history(params.row);
                    }
                  }
                },
                "审批详情"
              ),
              h(
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
                      this.detail(params.row);
                    }
                  }
                },
                "表单数据"
              ),
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
      total: 0, // 表单数据总数
      deleteId: ""
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
    getDataList() {
      this.loading = true;
      getRunningProcess(this.searchForm).then(res => {
        this.loading = false;
        if (res.success == true) {
          this.data = res.result.content;
          this.total = res.result.totalElements;
        }
      });
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
      // 重新加载数据
      this.getDataList();
    },
    showSelect(e) {
      this.selectList = e;
      this.selectCount = e.length;
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    },
    handelCancel() {
      this.modalVisible = false;
    },
    handelSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          if (this.modalType == 0) {
            deleteProcessIns(this.deleteId, this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.modalVisible = false;
                this.getDataList();
              }
            });
          } else if (this.modalType == 1) {
            let ids = "";
            this.selectList.forEach(function(e) {
              ids += e.id + ",";
            });
            ids = ids.substring(0, ids.length - 1);
            // 批量删除
            deleteProcessIns(ids, this.form).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$Message.success("操作成功");
                this.modalVisible = false;
                this.clearSelectAll();
                this.getDataList();
              }
            });
          }
        }
      });
    },
    editStatus(status, v) {
      let operation = "";
      if (status == 0) {
        operation = "暂停挂起";
      } else {
        operation = "激活运行";
      }
      this.$Modal.confirm({
        title: "确认" + operation,
        content: `您确认要${operation}流程实例${v.name}?`,
        onOk: () => {
          this.operationLoading = true;
          let params = {
            status: status,
            id: v.id
          };
          updateInsStatus(params).then(res => {
            this.operationLoading = false;
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.getDataList();
            }
          });
        }
      });
    },
    detail(v) {
      let query = {
        id: v.tableId,
        type: 3,
        backRoute: this.$route.name
      };
      this.$router.push({
        name: v.routeName,
        query: query
      });
    },
    history(v){
      let query = { id: v.id, backRoute: this.$route.name };
      this.$router.push({
        name: "historic_detail",
        query: query
      });
    },
    remove(v) {
      this.modalTitle = `确认删除流程 ${v.name}`;
      // 单个删除
      this.deleteId = v.id;
      this.modalType = 0;
      this.modalVisible = true;
    },
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("您还未选择要删除的数据");
        return;
      }
      this.modalTitle = "确认要删除所选的 " + this.selectCount + " 条数据";
      // 批量删除
      this.modalType = 1;
      this.modalVisible = true;
    }
  },
  mounted() {
    this.init();
  },
  watch: {
    // 监听路由变化
    $route(to, from) {
      if (to.name == "process-ins-manage") {
        this.getDataList();
      }
    }
  }
};
</script>