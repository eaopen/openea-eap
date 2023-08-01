<template>
  <div style="padding:8px 16px">
    <div :bordered="false" class="customer-list" dis-hover>
      <div class="top-buttons">
        <buttons
          ref="buttons"
          :list="buttons"
          :req="params"
          :selected="selected"
          :listId="listId"
          :code="code"
        />
        <right-button @refreshGrid="refreshGrid"/>
      </div>
      <!-- 暂时删除 :initFilters="req.initFilters"
        :initParams="req.initParams" -->
      <data-list
        :id="listId"
        v-if="showTable"
        ref="dataList"
        class="mt5"
        :code="code"
        :curPage="pageData.curPage"
        :listFields="fields"
        :height="tableHeight"
        :lockColumnCount="expand.lockColumnCount"
        :pageSize="pageData.pageSize"
        :selectEnable="selectEnable"
        :selectType="expand.selectType"
        :selected="selected"
        :showNo="expand.showNo"
        :dicMap="dicMap"
        :innerButtons="innerButtons"
        :expand="expand"
        @changeTotal="changeTotal"
      />
      <el-row>
        <filters :code="code" @clearFilter="clearFilter"></filters>
        <page
        ref="page"
        class="mt5"
        :data="pageData"
        :code="code"
        @handleChange="changePage"
      />
      </el-row>
    </div>
  </div>
</template>
<script>
  /**
   * 自定义列表
   */
  import buttons from './buttons.vue'
  import rightButton from './rightButtons.vue'
  import dataList from './dataList.vue'
  import page from './page.vue'
  import filters from './filter.vue'
  import {getListConfig} from "@/api/obpm/grid.js"

  export default {
    name: 'FormCustSqlView',
    components: { buttons, dataList, page, rightButton, filters },
    props: ['code', 'req', 'changeTitleState', 'params'],
    data() {
      return {
        isTree: false,
        fields: [],
        pageData: {
          curPage: 1,
          pageSize: 100,
          total: 0,
        },
        // 参照文档后的参数重整理
        showTable: false,
        tableHeight: 'auto',
        listId: '',
        name: '', // 标题
        status: 0, // 状态，关闭后不执行
        groupsList: '', //分组
        buttons: [], //按钮
        innerButtons: [],
        expand: {
          selectType: 1, // 单选
          userLocal: 1, //是否缓存
          showNo: 1, // 显示序号
          showSelect: 0, // 显示选择
          lockColumnCount: 0, // 左侧固定列数
          version: '', //版本
        },
        showRowsNum: 0, // 显示统计
        canExport: 1, // 支持导出
        quickSearch: [
          //快速搜索配置
          {
            key: 0,
            name: '全部',
            tips: '搜索全部',
            filter: 'true',
          },
          // {
          //   key: 1,
          //   name: '定购(毛)',
          //   tips: '路线0为“定购”，路线1为“(毛)”，且的关系',
          //   filter: 'item.route0 && item.route0 == 2 && item.route1 && item.route1 == 6'
          // },
        ],
        selected: [],
        selectEnable: ['1128', '1858'],
        dicMap: {}
      }
    },
    computed: {
      visitedViews() {
        return this.$store.state.tagsView.visitedViews
      },
    },
    created() {
      console.log('created')
      //测试URL参数 {"componentid":"433396969320742913","componentnum":"75429-11028G01","title":"75429-11028G01计划任务"}
      this.init(this.code)
    },
    activated(){
      console.log(this.visitedViews)
    },
    mounted(){
    },
    methods: {
      changeTotal(data) {
        this.pageData.total = data
      },
      changePage(data) {
        this.pageData = Object.assign(this.pageData, data)
        this.$refs.dataList.changePage(this.pageData)
      },
      initReq() {
        let req = this.$route.query.req
        if (req) {
          let cache = JSON.parse(decodeURIComponent(req))
          Object.keys(cache).forEach((k) => {
            if (k == 'title') {
              this.req.title = cache.title
            } else if (k == 'filter') {
              this.req.initFilters = cache.filter
            } else if (k == 'extend') {
              this.req.extend = cache.extend
            } else {
              this.req.initParams[k] = cache[k]
            }
          })
        }
      },
      refreshGrid(){
        this.$refs.dataList.refreshData()
      },
      clearFilter(){
        this.$refs.dataList.resetFilter()
      },
      init(v) {
        if(!v) return // to do
        getListConfig(this.code).then((data) => {
          if (!data.isOk) {
            return
          }
          if (data.data.status === 0) {
            this.$baseAlert('列表未开启，如有疑问请联系管理员')
            return
          }
          this.status = data.data.status
          this.fields = data.data.fieldsList
          this.buttons = data.data.buttons.filter(i=>i.type == 2)
          this.innerButtons = data.data.buttons.filter(i=>i.type == 1)
          this.listId = data.data.pkName
          this.dicMap = data.data.dicMap // dicMap 存入window 对象，方便使用
          this.expand = Object.assign(this.expand, data.data.expand)
          this.pageData.pageSize = data.data.pageSize
          if(this.changeTitleState){
            this.$emit('changeTitle', data.data.name)
          }
          this.showTable = true
          setTimeout(() => {
            console.log("ref init")
            this.$refs.dataList.init()
          }, 100)
        })
      },
    },
  }
</script>
<style lang="scss" scoped>
  .top-buttons {
    display: flex;
    justify-content: space-between;
    align-items: flex-start
  }
</style>
