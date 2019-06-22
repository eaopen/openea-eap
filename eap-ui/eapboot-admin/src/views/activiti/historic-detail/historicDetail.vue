<style lang="less">
@import "./historicDetail.less";
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card style="margin-bottom:10px;">
          <p slot="title">
            <span>流程审批进度历史</span>
          </p>
          <Row style="position:relative">
            <Table :loading="loading" border :columns="columns" :data="data" ref="table"></Table>
            <Spin size="large" fix v-if="loading"></Spin>
          </Row>
        </Card>
        <Card>
          <p slot="title">
            <span>实时流程图</span>
          </p>
          <Row style="position:relative">
            <img :src="imgUrl">
            <Spin size="large" fix v-if="loadingImg"></Spin>
          </Row>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import { getHighlightImg, historicFlow } from "@/api/activiti";
import util from "@/libs/util";
export default {
  name: "historic_detail",
  data() {
    return {
      type: 0,
      loading: false, // 表单加载状态
      loadingImg: false,
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
          title: "任务名称",
          key: "name",
          sortable: true
        },
        {
          title: "处理人",
          key: "assignees",
          align: "center",
          sortable: true,
          minWidth: 100,
          render: (h, params) => {
            return h(
              "div",
              params.row.assignees.map(function(item, index) {
                if (item.isExecutor) {
                  return h("span", [
                    h("Badge", {
                      style: {
                        "margin-right": "8px"
                      },
                      props: {
                        status: "success",
                        text: item.username
                      }
                    })
                  ]);
                } else {
                  return h("span", [
                    h("Badge", {
                      style: {
                        "margin-right": "8px"
                      },
                      props: {
                        status: "default",
                        text: item.username
                      }
                    })
                  ]);
                }
              })
            );
          }
        },
        {
          title: "审批操作",
          key: "deleteReason",
          align: "center",
          sortable: true
        },
        {
          title: "审批意见",
          key: "comment",
          align: "center",
          sortable: true
        },
        {
          title: "耗时",
          key: "duration",
          align: "center",
          sortable: true,
          render: (h, params) => {
            return h("div", util.millsToTime(params.row.duration));
          }
        },
        {
          title: "创建时间",
          key: "createTime",
          width: 150,
          sortType: "asc",
          sortable: true
        },
        {
          title: "完成时间",
          key: "endTime",
          width: 150,
          sortable: true
        },
        {
          title: "状态",
          align: "center",
          render: (h, params) => {
            let text = "",
              color = "";
            if (params.row.endTime) {
              color = "blue";
              text = "已办理";
            } else {
              color = "default";
              text = "待处理";
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
        }
      ],
      data: [],
      id: "",
      imgUrl: "",
      backRoute: ""
    };
  },
  methods: {
    init() {
      this.type = this.$route.query.type;
      this.backRoute = this.$route.query.backRoute;
      this.id = this.$route.query.id;
      this.imgUrl =
        getHighlightImg +
        this.id +
        "?accessToken=" +
        this.getStore("accessToken") +
        "&time=" +
        new Date();
      this.getDataList();
    },
    getDataList() {
      this.loading = true;
      historicFlow(this.id).then(res => {
        this.loading = false;
        if (res.success) {
          this.data = res.result;
        }
      });
    },
    // 关闭当前页面
    closeCurrentPage() {
      this.$store.commit("removeTag", "historic_detail");
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
      if (to.name == "historic_detail") {
        this.init();
      }
    }
  }
};
</script>