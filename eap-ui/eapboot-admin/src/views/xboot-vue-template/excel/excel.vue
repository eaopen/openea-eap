<style lang="less">
@import "./excel.less";
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card>
          <Row class="operation">
            <Button @click="exportSelectData" icon="md-cloud-upload">导出所选数据</Button>
            <Button @click="exportAll" icon="ios-cloud-upload">导出全部数据</Button>
            <Button @click="importModalVisible=true" icon="ios-download">导入数据</Button>
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
    <!-- 自定义导出数据 -->
    <Modal
      v-model="exportModalVisible"
      :title="exportTitle"
      :loading="loadingExport"
      @on-ok="exportCustomData">
      <Form ref="exportForm" :label-width="85">
        <FormItem label="导出文件名">
          <Input v-model="filename"/>
        </FormItem>
        <FormItem label="自定义导出列">
          <CheckboxGroup v-model="chooseColumns">
            <Checkbox v-for="(item, i) in exportColumns" :label="item.title" :key="i" :value="item.checked" :disabled="item.disabled"></Checkbox>
          </CheckboxGroup>
        </FormItem>
      </Form>
    </Modal>
    <Drawer title="导入数据" closable v-model="importModalVisible" width="1000">
      <Upload action="" :before-upload="beforeUploadImport" accept=".xls, .xlsx">
        <Button icon="ios-cloud-upload-outline" style="margin-right:10px">上传Excel文件</Button>
        <span v-if="uploadfile.name!=''">当前选择文件：{{ uploadfile.name }}</span>
      </Upload>
      <Alert type="warning" show-icon>
        导入前请下载查看导入模版数据文件，确保数据格式正确，不得修改列英文名称。
      </Alert>
      <Table :columns="importColumns" border :height="height" :data="importTableData" ref="importTable"></Table>
      <div class="drawer-footer">
        <Button @click="downloadTemple" type="info" style="position:absolute;left:15px;">下载导入模板</Button>
        <Button @click="importModalVisible=false">关闭</Button>
        <Button :loading="importLoading" :disabled="importTableData.length<=0" @click="importData" style="margin-left:5px" type="primary">
          确认导入<span v-if="importTableData.length>0"> {{importTableData.length}} 条数据</span>
        </Button>
      </div>
    </Drawer>
  </div>
</template>

<script>
// 模版导入文件表数据
import { userColumns, userData } from "@/libs/importTemplate";
// excel转换工具类
import excel from "@/libs/excel";
export default {
  name: "excel",
  data() {
    return {
      height: 510,
      loading: true, // 表单加载状态
      importLoading: false, // 导入加载状态
      loadingExport: true, // 导出加载状态
      exportModalVisible: false, // 自定义导出显示
      importModalVisible: false, // 导入显示
      selectCount: 0, // 多选计数
      selectList: [], // 多选数据
      searchForm: {
        pageNumber: 1,
        pageSize: 10,
        sort: "createTime",
        order: "desc"
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
          title: "名称",
          key: "name",
          sortable: true
        },
        {
          title: "手机",
          key: "mobile",
          sortable: true
        },
        {
          title: "创建时间",
          key: "createTime",
          sortable: true,
          sortType: "desc"
        },
        {
          title: "更新时间",
          key: "updateTime",
          sortable: true
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small",
                    icon: "ios-create-outline"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.$Message.info("点击了编辑");
                    }
                  }
                },
                "编辑"
              ),
              h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small",
                    icon: "md-trash"
                  },
                  on: {
                    click: () => {
                      this.$Message.info("点击了删除");
                    }
                  }
                },
                "删除"
              )
            ]);
          }
        }
      ],
      exportColumns: [
        // 导出列
        {
          title: "名称",
          key: "name",
          sortable: true
        },
        {
          title: "手机",
          key: "mobile",
          sortable: true
        },
        {
          title: "创建时间",
          key: "createTime",
          sortable: true,
          sortType: "desc"
        },
        {
          title: "更新时间",
          key: "updateTime",
          sortable: true
        }
      ],
      chooseColumns: [],
      filename: "数据",
      exportTitle: "确认导出",
      exportType: "",
      importTableData: [],
      importColumns: [],
      uploadfile: {
        name: ""
      },
      tempColumns: userColumns,
      tempData: userData,
      data: [],
      exportData: [],
      total: 0
    };
  },
  methods: {
    init() {
      this.getDataList();
      // 初始化导出列数据
      let array = [];
      this.exportColumns.forEach(e => {
        // 指定列限制权限
        if (
          !this.getStore("roles").includes("ROLE_ADMIN") &&
          e.key == "mobile"
        ) {
          e.title = "[无权导出] " + e.title;
          e.disabled = true;
        } else {
          e.disabled = false;
        }
        array.push(e.title);
      });
      this.chooseColumns = array;
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
      let params = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortColumn,
        order: this.sortType
      };
      // 请求后端获取表单数据 请自行修改接口
      // this.getRequest("请求路径", params).then(res => {
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
          createTime: "2018-08-08 00:08:00",
          updateTime: "2018-08-08 00:08:00"
        },
        {
          id: "2",
          name: "Exrick",
          mobile: "12345678901",
          createTime: "2018-08-08 00:08:00",
          updateTime: "2018-08-08 00:08:00"
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
    exportSelectData() {
      if (this.selectCount <= 0) {
        this.$Message.warning("您还未选择要导出的数据");
        return;
      }
      this.exportType = "part";
      this.exportModalVisible = true;
      this.exportTitle = "确认导出 " + this.selectCount + " 条数据";
    },
    exportAll() {
      this.exportType = "all";
      this.exportModalVisible = true;
      this.exportTitle = "确认导出全部 " + this.total + " 条数据";
      // 请求后端获取全部数据 请自行修改接口
      // this.getRequest("请求路径", params).then(res => {
      //   if (res.success) {
      //     this.exportData = res.result;
      //   }
      // });
      // 以下为模拟数据
      this.exportData = [
        {
          id: "1",
          name: "XBoot",
          mobile: "12345678901",
          createTime: "2018-08-08 00:08:00",
          updateTime: "2018-08-08 00:08:00"
        },
        {
          id: "2",
          name: "Exrick",
          mobile: "12345678901",
          createTime: "2018-08-08 00:08:00",
          updateTime: "2018-08-08 00:08:00"
        }
      ];
    },
    exportCustomData() {
      if (this.filename == "") {
        this.filename = "用户数据";
      }
      // 判断勾选导出列
      let array = [];
      this.exportColumns.forEach(e => {
        this.chooseColumns.forEach(c => {
          if (e.title == c && !e.disabled) {
            array.push(e);
          }
        });
      });
      this.exportColumns = array;
      this.exportModalVisible = false;
      let title = [];
      let key = [];
      this.exportColumns.forEach(e => {
        title.push(e.title);
        key.push(e.key);
      });
      const params = {
        title: title,
        key: key,
        data: this.exportData,
        autoWidth: true,
        filename: this.filename
      };
      excel.export_array_to_excel(params);
    },
    beforeUploadImport(file) {
      this.uploadfile = file;
      const fileExt = file.name
        .split(".")
        .pop()
        .toLocaleLowerCase();
      if (fileExt == "xlsx" || fileExt == "xls") {
        this.readFile(file);
        this.file = file;
      } else {
        this.$Notice.warning({
          title: "文件类型错误",
          desc:
            "所选文件‘ " +
            file.name +
            " ’不是EXCEL文件，请选择后缀为.xlsx或者.xls的EXCEL文件。"
        });
      }
      return false;
    },
    // 读取文件
    readFile(file) {
      const reader = new FileReader();
      reader.readAsArrayBuffer(file);
      reader.onerror = e => {
        this.$Message.error("文件读取出错");
      };
      reader.onload = e => {
        this.$Message.success("读取数据成功");
        const data = e.target.result;
        const { header, results } = excel.read(data, "array");
        const tableTitle = header.map(item => {
          return { title: item, key: item };
        });
        this.importTableData = results;
        this.importColumns = tableTitle;
      };
    },
    downloadTemple() {
      let title = [];
      let key = [];
      userColumns.forEach(e => {
        title.push(e.title);
        key.push(e.key);
      });
      const params = {
        title: title,
        key: key,
        data: userData,
        autoWidth: true,
        filename: "导入数据模版"
      };
      excel.export_array_to_excel(params);
    },
    importData() {
      this.importLoading = true;
      // 传入导入数据 后端接收body数组列表批量导入
      // this.importRequest("请求路径", this.importTableData).then(res => {
      //   this.importLoading = false;
      //   if (res.success) {
      //     this.importModalVisible = false;
      //     this.getDataList();
      //     this.$Modal.info({
      //       title: "导入结果",
      //       content: res.message
      //     });
      //   }
      // });
      // 模拟请求成功
      this.importLoading = false;
      this.importModalVisible = false;
      this.$Modal.info({
        title: "导入结果",
        content: "导入成功，这是假通知"
      });
      this.getDataList();
    },
    showSelect(e) {
      this.exportData = e;
      this.selectList = e;
      this.selectCount = e.length;
    },
    clearSelectAll() {
      this.$refs.table.selectAll(false);
    }
  },
  mounted() {
    // 计算高度
    this.height = Number(document.documentElement.clientHeight - 230);
    this.init();
  }
};
</script>