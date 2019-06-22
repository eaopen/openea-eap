<style lang="less">
@import "./socialManage.less";
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card>
          <Tabs v-model="tabName" :animated="false">
            <TabPane label="Github" name="github">
              <Row @keydown.enter.native="handleSearch">
                <Form ref="githubForm" :model="github" inline :label-width="85" class="search-form">
                  <Form-item label="社交账号昵称" prop="username">
                    <Input
                      type="text"
                      v-model="github.username"
                      placeholder="请输入第三方社交账号昵称"
                      clearable
                      style="width: 200px"
                    />
                  </Form-item>
                  <Form-item label="绑定用户名" prop="relateUsername">
                    <Input
                      type="text"
                      v-model="github.relateUsername"
                      placeholder="请输入绑定系统用户名"
                      clearable
                      style="width: 200px"
                    />
                  </Form-item>
                  <Form-item label="创建时间">
                    <DatePicker
                      v-model="github.selectDate"
                      type="daterange"
                      format="yyyy-MM-dd"
                      clearable
                      @on-change="selectDateRange"
                      placeholder="选择起始时间"
                      style="width: 200px"
                    ></DatePicker>
                  </Form-item>
                  <Form-item style="margin-left:-35px;" class="br">
                    <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
                    <Button @click="handleReset">重置</Button>
                  </Form-item>
                </Form>
              </Row>
              <Row class="operation">
                <Button @click="delAll" icon="md-trash">批量删除解绑</Button>
                <Button @click="getDataList" icon="md-refresh">刷新</Button>
                <circleLoading v-if="operationLoading"/>
              </Row>
              <Row>
                <Alert show-icon>
                  已选择
                  <span class="select-count">{{github.selectCount}}</span> 项
                  <a class="select-clear" @click="clearSelectAll">清空</a>
                </Alert>
              </Row>
              <Row>
                <Table
                  :loading="github.loading"
                  border
                  :columns="columns"
                  :data="github.data"
                  sortable="custom"
                  @on-sort-change="changeSort"
                  @on-selection-change="showSelect"
                  ref="githubTable"
                ></Table>
              </Row>
              <Row type="flex" justify="end" class="page">
                <Page
                  :current="github.pageNumber"
                  :total="github.total"
                  :page-size="github.pageSize"
                  @on-change="changePage"
                  @on-page-size-change="changePageSize"
                  :page-size-opts="[10,20,50]"
                  size="small"
                  show-total
                  show-elevator
                  show-sizer
                ></Page>
              </Row>
            </TabPane>
            <TabPane label="QQ" name="qq">
              <Row @keydown.enter.native="handleSearch">
                <Form ref="qqForm" :model="qq" inline :label-width="85" class="search-form">
                  <Form-item label="社交账号昵称" prop="username">
                    <Input
                      type="text"
                      v-model="qq.username"
                      placeholder="请输入第三方社交账号昵称"
                      clearable
                      style="width: 200px"
                    />
                  </Form-item>
                  <Form-item label="绑定用户名" prop="relateUsername">
                    <Input
                      type="text"
                      v-model="qq.relateUsername"
                      placeholder="请输入绑定系统用户名"
                      clearable
                      style="width: 200px"
                    />
                  </Form-item>
                  <Form-item label="创建时间">
                    <DatePicker
                      v-model="qq.selectDate"
                      type="daterange"
                      format="yyyy-MM-dd"
                      clearable
                      @on-change="selectDateRange"
                      placeholder="选择起始时间"
                      style="width: 200px"
                    ></DatePicker>
                  </Form-item>
                  <Form-item style="margin-left:-35px;" class="br">
                    <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
                    <Button @click="handleReset">重置</Button>
                  </Form-item>
                </Form>
              </Row>
              <Row class="operation">
                <Button @click="delAll" icon="md-trash">批量删除解绑</Button>
                <Button @click="getDataList" icon="md-refresh">刷新</Button>
                <circleLoading v-if="operationLoading"/>
              </Row>
              <Row>
                <Alert show-icon>
                  已选择
                  <span class="select-count">{{qq.selectCount}}</span> 项
                  <a class="select-clear" @click="clearSelectAll">清空</a>
                </Alert>
              </Row>
              <Row>
                <Table
                  :loading="qq.loading"
                  border
                  :columns="columns"
                  :data="qq.data"
                  sortable="custom"
                  @on-sort-change="changeSort"
                  @on-selection-change="showSelect"
                  ref="qqTable"
                ></Table>
              </Row>
              <Row type="flex" justify="end" class="page">
                <Page
                  :current="qq.pageNumber"
                  :total="qq.total"
                  :page-size="qq.pageSize"
                  @on-change="changePage"
                  @on-page-size-change="changePageSize"
                  :page-size-opts="[10,20,50]"
                  size="small"
                  show-total
                  show-elevator
                  show-sizer
                ></Page>
              </Row>
            </TabPane>
            <TabPane label="微博" name="weibo">
              <Row @keydown.enter.native="handleSearch">
                <Form ref="weiboForm" :model="weibo" inline :label-width="85" class="search-form">
                  <Form-item label="社交账号昵称" prop="username">
                    <Input
                      type="text"
                      v-model="weibo.username"
                      placeholder="请输入第三方社交账号昵称"
                      clearable
                      style="width: 200px"
                    />
                  </Form-item>
                  <Form-item label="绑定用户名" prop="relateUsername">
                    <Input
                      type="text"
                      v-model="weibo.relateUsername"
                      placeholder="请输入绑定系统用户名"
                      clearable
                      style="width: 200px"
                    />
                  </Form-item>
                  <Form-item label="创建时间">
                    <DatePicker
                      v-model="weibo.selectDate"
                      type="daterange"
                      format="yyyy-MM-dd"
                      clearable
                      @on-change="selectDateRange"
                      placeholder="选择起始时间"
                      style="width: 200px"
                    ></DatePicker>
                  </Form-item>
                  <Form-item style="margin-left:-35px;" class="br">
                    <Button @click="handleSearch" type="primary" icon="ios-search">搜索</Button>
                    <Button @click="handleReset">重置</Button>
                  </Form-item>
                </Form>
              </Row>
              <Row class="operation">
                <Button @click="delAll" icon="md-trash">批量删除解绑</Button>
                <Button @click="getDataList" icon="md-refresh">刷新</Button>
                <circleLoading v-if="operationLoading"/>
              </Row>
              <Row>
                <Alert show-icon>
                  已选择
                  <span class="select-count">{{weibo.selectCount}}</span> 项
                  <a class="select-clear" @click="clearSelectAll">清空</a>
                </Alert>
              </Row>
              <Row>
                <Table
                  :loading="weibo.loading"
                  border
                  :columns="columns"
                  :data="weibo.data"
                  sortable="custom"
                  @on-sort-change="changeSort"
                  @on-selection-change="showSelect"
                  ref="weiboTable"
                ></Table>
              </Row>
              <Row type="flex" justify="end" class="page">
                <Page
                  :current="weibo.pageNumber"
                  :total="weibo.total"
                  :page-size="weibo.pageSize"
                  @on-change="changePage"
                  @on-page-size-change="changePageSize"
                  :page-size-opts="[10,20,50]"
                  size="small"
                  show-total
                  show-elevator
                  show-sizer
                ></Page>
              </Row>
            </TabPane>
          </Tabs>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import { getRelatedListData, unRelate } from "@/api/index";
import circleLoading from "@/views/my-components/circle-loading.vue";
export default {
  name: "social-manage",
  components: {
    circleLoading
  },
  data() {
    return {
      tabName: "github",
      operationLoading: false,
      github: {
        // 搜索框对应data对象
        username: "",
        relateUsername: "",
        pageNumber: 1, // 当前页数
        pageSize: 10, // 页面大小
        sort: "createTime", // 默认排序字段
        order: "desc", // 默认排序方式
        startDate: "", // 起始时间
        endDate: "", // 终止时间
        data: [],
        total: 0,
        selectDate: null,
        loading: true, // 表单加载状态
        selectCount: 0, // 多选计数
        selectList: [] // 多选数据
      },
      qq: {
        // 搜索框对应data对象
        username: "",
        relateUsername: "",
        pageNumber: 1, // 当前页数
        pageSize: 10, // 页面大小
        sort: "createTime", // 默认排序字段
        order: "desc", // 默认排序方式
        startDate: "", // 起始时间
        endDate: "", // 终止时间
        data: [],
        total: 0,
        selectDate: null,
        loading: true, // 表单加载状态
        selectCount: 0, // 多选计数
        selectList: [] // 多选数据
      },
      weibo: {
        // 搜索框对应data对象
        username: "",
        relateUsername: "",
        pageNumber: 1, // 当前页数
        pageSize: 10, // 页面大小
        sort: "createTime", // 默认排序字段
        order: "desc", // 默认排序方式
        startDate: "", // 起始时间
        endDate: "", // 终止时间
        data: [],
        total: 0,
        selectDate: null,
        loading: true, // 表单加载状态
        selectCount: 0, // 多选计数
        selectList: [] // 多选数据
      },
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
          title: "社交账号昵称",
          key: "username",
          minWidth: 180,
          sortable: true,
          fixed: "left",
          render: (h, params) => {
            if (this.$route.meta.permTypes.includes("view")) {
              return h("span", params.row.username);
            } else {
              return h("span", "你无权查看该数据");
            }
          }
        },
        {
          title: "头像",
          key: "avatar",
          width: 80,
          align: "center",
          render: (h, params) => {
            return h("Avatar", {
              props: {
                src: params.row.avatar
              }
            });
          }
        },
        {
          title: "openID",
          key: "openId",
          width: 200,
          sortable: true,
          render: (h, params) => {
            if (this.$route.meta.permTypes.includes("view")) {
              return h("span", params.row.openId);
            } else {
              return h("span", "你无权查看该数据");
            }
          }
        },
        {
          title: "是否绑定用户",
          key: "isRelated",
          width: 150,
          align: "center",
          sortable: true,
          render: (h, params) => {
            if (params.row.isRelated) {
              return h("div", [
                h(
                  "Tag",
                  {
                    props: {
                      color: "blue"
                    }
                  },
                  "已绑定"
                )
              ]);
            } else {
              return h("div", [
                h(
                  "Tag",
                  {
                    props: {
                      color: "default"
                    }
                  },
                  "未绑定"
                )
              ]);
            }
          }
        },
        {
          title: "绑定用户",
          key: "relateUsername",
          width: 200,
          sortable: true
        },
        {
          title: "创建时间",
          key: "createTime",
          width: 170,
          sortable: true,
          sortType: "desc"
        },
        {
          title: "操作",
          key: "action",
          width: 180,
          align: "center",
          fixed: "right",
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    icon: "md-link",
                    type: "error",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.remove(params.row);
                    }
                  }
                },
                "删除解绑"
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
      let str = JSON.stringify(this.qq);
      let params = JSON.parse(str);
      this.qq.loading = true;
      params.socialType = 1;
      delete params.selectDate;
      delete params.data;
      delete params.selectList;
      getRelatedListData(params).then(res => {
        this.qq.loading = false;
        if (res.success == true) {
          this.qq.data = res.result.content;
          this.qq.total = res.result.totalElements;
        }
      });
      str = JSON.stringify(this.weibo);
      params = JSON.parse(str);
      this.weibo.loading = true;
      params.socialType = 2;
      delete params.selectDate;
      delete params.data;
      delete params.selectList;
      getRelatedListData(params).then(res => {
        this.weibo.loading = false;
        if (res.success == true) {
          this.weibo.data = res.result.content;
          this.weibo.total = res.result.totalElements;
        }
      });
    },
    changePage(v) {
      if (this.tabName == "github") {
        this.github.pageNumber = v;
      } else if (this.tabName == "qq") {
        this.qq.pageNumber = v;
      } else if (this.tabName == "weibo") {
        this.weibo.pageNumber = v;
      }
      this.getDataList();
      this.clearSelectAll();
    },
    changePageSize(v) {
      if (this.tabName == "github") {
        this.github.pageSize = v;
      } else if (this.tabName == "qq") {
        this.qq.pageSize = v;
      } else if (this.tabName == "weibo") {
        this.weibo.pageSize = v;
      }
      this.getDataList();
    },
    selectDateRange(v) {
      if (this.tabName == "github") {
        this.github.startDate = v[0];
        this.github.endDate = v[1];
      } else if (this.tabName == "qq") {
        this.qq.startDate = v[0];
        this.qq.endDate = v[1];
      } else if (this.tabName == "weibo") {
        this.weibo.startDate = v[0];
        this.weibo.endDate = v[1];
      }
    },
    getDataList() {
      let params = {};
      if (this.tabName == "github") {
        let str = JSON.stringify(this.github);
        params = JSON.parse(str);
        this.github.loading = true;
        params.socialType = 0;
      } else if (this.tabName == "qq") {
        let str = JSON.stringify(this.qq);
        params = JSON.parse(str);
        this.qq.loading = true;
        params.socialType = 1;
      } else if (this.tabName == "weibo") {
        let str = JSON.stringify(this.weibo);
        params = JSON.parse(str);
        this.weibo.loading = true;
        params.socialType = 2;
      }
      delete params.selectDate;
      delete params.data;
      delete params.selectList;
      getRelatedListData(params).then(res => {
        if (res.success == true) {
          if (this.tabName == "github") {
            this.github.loading = false;
            this.github.data = res.result.content;
            this.github.total = res.result.totalElements;
          } else if (this.tabName == "qq") {
            this.qq.loading = false;
            this.qq.data = res.result.content;
            this.qq.total = res.result.totalElements;
          } else if (this.tabName == "weibo") {
            this.weibo.loading = false;
            this.weibo.data = res.result.content;
            this.weibo.total = res.result.totalElements;
          }
        }
      });
    },
    handleSearch() {
      if (this.tabName == "github") {
        this.github.pageNumber = 1;
        this.github.pageSize = 10;
      } else if (this.tabName == "qq") {
        this.qq.pageNumber = 1;
        this.qq.pageSize = 10;
      } else if (this.tabName == "weibo") {
        this.weibo.pageNumber = 1;
        this.weibo.pageSize = 10;
      }
      this.getDataList();
    },
    handleReset() {
      if (this.tabName == "github") {
        this.$refs.githubForm.resetFields();
        this.github.pageNumber = 1;
        this.github.pageSize = 10;
        this.github.selectDate = null;
        this.github.startDate = "";
        this.github.endDate = "";
      } else if (this.tabName == "qq") {
        this.$refs.qqForm.resetFields();
        this.qq.pageNumber = 1;
        this.qq.pageSize = 10;
        this.qq.selectDate = null;
        this.qq.startDate = "";
        this.qq.endDate = "";
      } else if (this.tabName == "weibo") {
        this.$refs.weiboForm.resetFields();
        this.weibo.pageNumber = 1;
        this.weibo.pageSize = 10;
        this.weibo.selectDate = null;
        this.weibo.startDate = "";
        this.weibo.endDate = "";
      }
      // 重新加载数据
      this.getDataList();
    },
    changeSort(e) {
      if (this.tabName == "github") {
        this.github.sort = e.key;
        this.github.order = e.order;
        if (e.order == "normal") {
          this.github.order = "";
        }
      } else if (this.tabName == "qq") {
        this.qq.sort = e.key;
        this.qq.order = e.order;
        if (e.order == "normal") {
          this.qq.order = "";
        }
      } else if (this.tabName == "weibo") {
        this.weibo.sort = e.key;
        this.weibo.order = e.order;
        if (e.order == "normal") {
          this.weibo.order = "";
        }
      }
      this.getDataList();
    },
    remove(v) {
      this.$Modal.confirm({
        title: "确认删除解绑",
        // 记得确认修改此处
        content: "您确认要删除解绑该条数据 ?",
        onOk: () => {
          // 删除
          let socialType;
          if (this.tabName == "github") {
            socialType = 0;
          } else if (this.tabName == "qq") {
            socialType = 1;
          } else if (this.tabName == "weibo") {
            socialType = 2;
          }
          let params = {
            ids: v.id,
            usernames: v.relateUsername,
            socialType: socialType
          };
          this.operationLoading = true;
          unRelate(params).then(res => {
            this.operationLoading = false;
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.getDataList();
            }
          });
        }
      });
    },
    showSelect(e) {
      if (this.tabName == "github") {
        this.github.selectList = e;
        this.github.selectCount = e.length;
      } else if (this.tabName == "qq") {
        this.qq.selectList = e;
        this.qq.selectCount = e.length;
      } else if (this.tabName == "weibo") {
        this.weibo.selectList = e;
        this.weibo.selectCount = e.length;
      }
    },
    clearSelectAll() {
      if (this.tabName == "github") {
        this.$refs.githubTable.selectAll(false);
      } else if (this.tabName == "qq") {
        this.$refs.qqTable.selectAll(false);
      } else if (this.tabName == "weibo") {
        this.$refs.weiboTable.selectAll(false);
      }
    },
    delAll() {
      let socialType;
      if (this.tabName == "github") {
        socialType = 0;
        if (this.github.selectCount <= 0) {
          this.$Message.warning("您还未选择要删除的数据");
          return;
        }
        this.$Modal.confirm({
          title: "确认删除解绑",
          content:
            "您确认要删除解绑所选的 " + this.github.selectCount + " 条数据?",
          onOk: () => {
            let ids = "";
            this.github.selectList.forEach(function(e) {
              ids += e.id + ",";
            });
            ids = ids.substring(0, ids.length - 1);
            let usernames = "";
            this.github.selectList.forEach(function(e) {
              usernames += e.relateUsername + ",";
            });
            usernames = usernames.substring(0, ids.length - 1);
            let params = {
              ids: ids,
              usernames: usernames,
              socialType: socialType
            };
            this.operationLoading = true;
            unRelate(params).then(res => {
              this.operationLoading = false;
              if (res.success == true) {
                this.$Message.success("操作成功");
                this.clearSelectAll();
                this.getDataList();
              }
            });
          }
        });
      } else if (this.tabName == "qq") {
        socialType = 1;
        if (this.qq.selectCount <= 0) {
          this.$Message.warning("您还未勾选要删除的数据");
          return;
        }
        this.$Modal.confirm({
          title: "确认删除解绑",
          content: "您确认要删除解绑所选的 " + this.qq.selectCount + " 条数据?",
          onOk: () => {
            let ids = "";
            this.qq.selectList.forEach(function(e) {
              ids += e.id + ",";
            });
            ids = ids.substring(0, ids.length - 1);
            let usernames = "";
            this.qq.selectList.forEach(function(e) {
              usernames += e.relateUsername + ",";
            });
            usernames = usernames.substring(0, ids.length - 1);
            let params = {
              ids: ids,
              usernames: usernames,
              socialType: socialType
            };
            this.operationLoading = true;
            unRelate(params).then(res => {
              this.operationLoading = false;
              if (res.success == true) {
                this.$Message.success("操作成功");
                this.clearSelectAll();
                this.getDataList();
              }
            });
          }
        });
      } else if (this.tabName == "weibo") {
        socialType = 2;
        if (this.weibo.selectCount <= 0) {
          this.$Message.warning("您还未勾选要删除的数据");
          return;
        }
        this.$Modal.confirm({
          title: "确认删除解绑",
          content:
            "您确认要删除解绑所选的 " + this.weibo.selectCount + " 条数据?",
          onOk: () => {
            let ids = "";
            this.weibo.selectList.forEach(function(e) {
              ids += e.id + ",";
            });
            ids = ids.substring(0, ids.length - 1);
            let usernames = "";
            this.weibo.selectList.forEach(function(e) {
              usernames += e.relateUsername + ",";
            });
            usernames = usernames.substring(0, ids.length - 1);
            let params = {
              ids: ids,
              usernames: usernames,
              socialType: socialType
            };
            this.operationLoading = true;
            unRelate(params).then(res => {
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
    }
  },
  mounted() {
    this.init();
  }
};
</script>