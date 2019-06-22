<style lang="less">
@import "processFinishManage.less";
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
              <Form-item label="结束时间">
                <DatePicker v-model="selectDate" type="daterange" format="yyyy-MM-dd" clearable @on-change="selectDateRange" placeholder="选择起始时间" style="width: 200px"></DatePicker>
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
              :data="data"
              sortable="custom"
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
  </div>
</template>

<script>
import { getFinishedProcess, delHistoricIns } from "@/api/activiti";
import circleLoading from "@/views/my-components/circle-loading.vue";
import util from "@/libs/util";
export default {
  name: "process-finish-manage",
  components: {
    circleLoading
  },
  data() {
    return {
      loading: true, // 表单加载状态
      operationLoading: false, // 操作加载状态
      selectCount: 0, // 多选计数
      selectList: [], // 多选数据
      selectDate: null, // 选择日期绑定modal
      searchForm: {
        // 搜索框对应data对象
        name: "",
        key: "",
        pageNumber: 1, // 当前页数
        pageSize: 10 // 页面大小
      },
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
          width: 120,
          sortable: true
        },
        {
          title: "流程名称",
          key: "name",
          minWidth: 130,
          sortable: true
        },
        {
          title: "申请人",
          key: "applyer",
          minWidth: 130,
          sortable: true
        },
        {
          title: "标识Key",
          key: "key",
          width: 120,
          sortable: true
        },
        {
          title: "版本",
          key: "version",
          width: 120,
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
          title: "审批结果",
          key: "result",
          align: "center",
          width: 120,
          sortable: true,
          render: (h, params) => {
            let text = "未知",
              color = "";
            if (params.row.result == 4) {
              text = "发起人撤回";
              color = "default";
            } else if (params.row.result == 5) {
              text = "已删除";
              color = "orange";
            } else if (params.row.result == 2) {
              text = "已通过";
              color = "green";
            } else if (params.row.result == 3) {
              text = "已驳回";
              color = "red";
            }
            return h("div", [
              h(
                "Tag",
                {
                  props: {
                    color: color
                  }
                },
                text
              )
            ]);
          }
        },
        {
          title: "原因详情",
          key: "deleteReason",
          width: 150
        },
        {
          title: "总耗时",
          key: "duration",
          align: "center",
          width: 110,
          sortable: true,
          render: (h, params) => {
            return h("div", util.millsToTime(params.row.duration))
          }
        },
        {
          title: "开始时间",
          key: "startTime",
          width: 150,
          sortable: true
        },
        {
          title: "结束时间",
          key: "endTime",
          width: 150,
          sortable: true,
          sortType: "desc"
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 230,
          fixed: "right",
          render: (h, params) => {
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
                      this.history(params.row);
                    }
                  }
                },
                "审批历史"
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
    selectDateRange(v) {
      if (v) {
        this.searchForm.startDate = v[0];
        this.searchForm.endDate = v[1];
      }
    },
    getDataList() {
      this.loading = true;
      getFinishedProcess(this.searchForm).then(res => {
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
    history(v) {
      let query = { id: v.id, backRoute: this.$route.name };
      this.$router.push({
        name: "historic_detail",
        query: query
      });
    },
    remove(v) {
      this.$Modal.confirm({
        title: "确认删除",
        // 记得确认修改此处
        content: "您确认要删除流程历史 " + v.name + " ?",
        onOk: () => {
          // 删除
          this.operationLoading = true;
          delHistoricIns(v.id).then(res => {
            this.operationLoading = false;
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.getDataList();
            }
          });
        }
      });
    },
    delAll() {
      if (this.selectCount <= 0) {
        this.$Message.warning("您还未选择要删除的数据");
        return;
      }
      this.modalTitle = "确认要删除所选的 " + this.selectCount + " 条数据";
      // 批量删除
      this.$Modal.confirm({
        title: "确认删除",
        content: "您确认要删除所选的 " + this.selectCount + " 条数据?",
        onOk: () => {
          let ids = "";
          this.selectList.forEach(function(e) {
            ids += e.id + ",";
          });
          ids = ids.substring(0, ids.length - 1);
          this.operationLoading = true;
          delHistoricIns(ids).then(res => {
            this.operationLoading = false;
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.clearSelectAll();
              this.getDataList();
            }
          });
        }
      });
    }
  },
  mounted() {
    this.init();
  },
  watch: {
    // 监听路由变化
    $route(to, from) {
      if (to.name == "process-finish-manage") {
        this.getDataList();
      }
    }
  }
};
</script>