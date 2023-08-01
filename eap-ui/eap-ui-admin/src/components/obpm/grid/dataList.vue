<template>
    <ag-grid-vue
      style="width: 100%;"
      :style="{height: tableHeight + 'px'}"
      class="ag-theme-alpine"
      :rowModelType="rowModelType"
      :serverSideFilterOnServer="true"
      :rowSelection="rowSelection"
      :isRowSelectable="selectAble"
      :getRowId="getRowId"
      :suppressCellFocus="true"
      :enableCellTextSelection = "true"
      :columnDefs="columnDefs"
      :enableBrowserTooltips="true"
      :getContextMenuItems="false"
      :suppressCellSelection="false"
      :suppressContextMenu="true"
      :defaultColDef="defaultColDef"
      @gridReady="onGridReady"
      @columnResized="onColumnResized"
      @firstDataRendered="onFirstDataRendered"
      @filterChanged="onFilterChanged"
      @rowSelected="rowSelected"
    ></ag-grid-vue>
    <!-- paging -->
</template>
<script>

import basicRender from './components/agGridBasic'
import datePicker from './components/agDatePicker'
import setFilter from './components/agGridSetFilter'
import agImage from './components/agGridImage'
import agOperations from './components/agGridOperations'
import { getListData } from '@/api/obpm/grid.js'
window.agGridMapList = {}
// 数据源
window.createDatasource = (server) => {
  return {
    getRows: (params) => {
      let filterModel = {}
      let filterCache = params.request.filterModel
      Object.keys(filterCache).forEach(k=>{
        if(filterCache[k].filterType == 'set'){
          let itemCache = {
            filterType: 'set',
            filter: ''
          }
          let filterList = agGridMapList[k].filter(i=>filterCache[k].values.indexOf(i.label) !== -1)
          itemCache.filter = filterList.map(i=>i.value).join(',')
          filterModel[k] = itemCache
        }else if(filterCache[k].filterType == 'text' || filterCache[k].filterType == 'number'){
          filterModel[k] = filterCache[k]
        }
      })
      server.getListData(
        server.code,
        {
          curPage: server.page || server.curPage,
          pageSize: server.size || server.pageSize,
          params:{
            filterModel:filterModel,
            sortModel: []
          }
        }
      ).then(res=>{
        if(res.code == 200){
          params.successCallback(res.data.list, res.data.count)
          server.$emit('changeTotal', res.data.count)
        }else {
          params.fail()
            server.$emit('changeTotal', 0)
        }
      })
    },
  }
}
export default {
  name: 'dataList',
  components: { basicRender, datePicker,setFilter, agImage, agOperations },
  props: [
    'listFields',
    'code',
    'id',
    'pageSize',
    'curPage',
    'initParams',
    'showNo',
    'selectType',
    'selectEnable',
    'dicMap',
    'lockColumnCount',
    'innerButtons',
    'expand',
    'height'
  ],
  data() {
    return {
      serverSideInfiniteScroll: true,
      page: 1,
      size: 100,
      defaultColDef: {
        flex: 1,
        filter: false,
        resizable: true,
        sortable: true,
        menuTabs: ['filterMenuTab']
      },
      tableHeight: 500,
      columnDefs: [],
      getListData: getListData,
      gridApi: null,
      columnApi: null,
      rowModelType: 'serverSide',
      getRowId: null,
      userLocalCols: false,
      selectAble: (rowNode) => {
        if (this.selectEnable.indexOf(rowNode.data[this.id]) === -1) {
          return true
        } else {
          return false
        }
      },
      controlType: {
        json: [10, 18, 4, 5, 6, 7], // 单选
        multJson: [8, 9], // 多选
        colorJson: [20], // 颜色
      },
      isEdit: false,
    }
  },
  computed: {
    rowSelection() {
      let state = null
      if (this.selectType == 1) {
        state = 'single'
      } else if (this.selectType == 2) {
        state = 'multiple'
      }
      return state
    },
    localConfigs(){
      if(this.code && this.$store.state.list.configs){
        return this.$store.state.list.configs[this.code]
      }else {
        return null
      }
    }
  },
  created(){
    if(this.expand.userLocal == 1){
      if(this.localConfigs){
        if(this.expand.version != this.localConfigs.version){
          this.$store.dispatch('list/removeListConfigs', this.code)
        }
      }else {
        this.$store.dispatch('list/setListConfigs', {
          code: this.code,
          version: this.expand.version || '0.0.0'
        })
      }
    }else {
      this.$store.dispatch('list/removeListConfigs', this.code)
    }

  },
  methods: {
    refreshData(){
      this.gridApi.refreshServerSide({ purge: true })
    },
    resetFilter(){
      this.gridApi.setFilterModel(this.localConfigs.filter)
    },
    onGridReady(params) {
      this.gridApi = params.api
      this.gridColumnApi = params.columnApi
      if(this.localConfigs.filter && Object.keys(this.localConfigs.filter).length){
        // console.log('设置筛选', this.localConfigs.filter)
        setTimeout(()=>{
          params.api.setFilterModel(this.localConfigs.filter)
        },100)
      }
      var datasource = createDatasource(this)
      this.gridApi.setServerSideDatasource(datasource)
    },
    // 调整宽度后保存至local
    onColumnResized(params){
      if(this.expand.userLocal == 1){
        console.log('调整宽度保存')
          this.saveColWidth()
      }
    },
    // 保存列信息
    saveColWidth(){
      let cols = this.gridApi.getColumnDefs()
      let obj = {
        code: this.code,
        columns: []
      }
      cols.forEach(item=>{
        obj.columns.push({
          colId: item.colId,
          width: item.width,
          hide: item.hide
        })
      })

      this.$store.dispatch('list/setListConfigs', obj)
    },
    onFirstDataRendered(params) {
      if(!this.userLocalCols){
        const allColumnIds = []
        this.gridColumnApi.getColumns().forEach((column) => {
          allColumnIds.push(column.getId())
        })
        this.gridColumnApi.autoSizeColumns(allColumnIds, true)
        if(this.expand.userLocal == 1){
            this.saveColWidth()
        }
      }

    },
    rowSelected() {
      let selects = this.gridApi.getSelectedRows()
      this.$store.dispatch('list/setListConfigs', {
        code: this.code,
        selects
      })
      // console.log()
    },
    onFilterChanged(params) {
      var filter = this.gridApi.getFilterModel()
      Object.keys(filter).forEach(k=>{
        let col = this.columnDefs.find(i=>i.field == k)
        if(col) filter[k]['name'] = col.headerName
      })
      this.$store.dispatch('list/setListConfigs', {
        code: this.code,
        filter
      })
    },
    changePage(data) {
      if(data.curPage){
        this.page = data.curPage
      }
      if(data.pageSize){
        this.size = data.pageSize
      }
      if (this.gridApi) {
        this.gridApi.refreshServerSide({ purge: true })
      }
    },
    // map转list
    map2List(m) {
      if (!m) return []
      let list = []
      Object.keys(m).forEach((k) => {
        list.push({
          key: k,
          name: m[k],
        })
      })
      return list
    },
    // 颜色格式化配置转list
    color2List(m) {
      let list = [],
        arr = m.split(',')
      arr.forEach((item) => {
        let [value, label, className] = item.split('-')
        if(className == 'tdred'){className = 'bg-red'}else if(className == 'tdgreen'){className = 'bg-green'}
        list.push({
          value: value,
          label: label,
          class: className,
        })
      })
      return list
    },
    getColList(m) {
      if (m) {
        if (m.startsWith('{')) {
          let list = []
          m = JSON.parse(m)
          Object.keys(m).forEach((k) => {
            list.push({
              value: k,
              label: m[k],
            })
          })
          return list
        } else if (m.startsWith('[')) {
          return JSON.parse(m)
        } else if (this.dicMap['a_'+m]) {
          return this.dicMap['a_'+m]
        }
      } else {
        return []
      }
    },
    // 将配置的属性，转化为aggrid 支持的属性
    transferItem(item) {
      // autoHeight, 自动高度，搭配wrapText 宽度需定死
      let obj = {
        field: item.fieldName,
        headerName: item.fieldDesc,
        hide: item.hidden == 1,
        minWidth: item.fieldDesc.length * 16 + 40,
        resizable: true,
        cellRendererParams: {},
        filterParams: {
          buttons: ['apply', 'reset'],
          closeOnApply: true
        }
      }
      // 全局的组件样式
      if(item.expand && item.expand.styleFormat){
        let cache = item.expand.styleFormat
        if(cache.startsWith('{')){
          obj.cellClassRules = {}
          cache = JSON.parse(cache)
          Object.keys(cache).forEach(k=>{
            obj.cellClassRules[cache[k]] = (params)=>{
              let rule = 'params.value ==' + k
              return eval(rule)
            }
            console.log(obj.cellClassRules)
          })
        }else{
          obj.cellClass = cache
        }
      }
      // 渲染组件，标头筛选，排序
      switch (item.controlType) {
        case 1:
          // 普通文本
          if(item.dataType == 'number'){
            obj.filter = 'agNumberColumnFilter'
          }else {
            obj.filter = 'agTextColumnFilter'
          }
          obj.cellEditor = 'agTextCellEditor'
          if (item.icon || item) {
            obj.cellRendererParams.icon = item.icon
            obj.cellRenderer = 'basicRender'
          }
          break
        case 2:
          // 大文本
          obj.filter = 'agTextColumnFilter'
          obj.cellEditor = 'agLargeTextCellEditor'
          if (item.icon) {
            obj.cellRendererParams.icon = item.icon
            obj.cellRenderer = 'basicRender'
          }
          break
        case 3:
          // 日期控件
          obj.filter = 'agDateColumnFilter'
          obj.cellEditor = 'datePicker'
          obj.cellRendererParams.dateFormat = item.dateFormat || 'yyyy-MM-DD'
          obj.valueFormatter = (params) => {
            // var day = this.parseTime(params.value)
            return this.parseTime(params.value, params.colDef.cellRendererParams.dateFormat)
          }
          if (item.icon) {
            obj.cellRendererParams.icon = item.icon
            obj.cellRenderer = 'basicRender'
          }
          break
        case 4:
        case 5:
        case 6:
        case 7:
          // 单选控件
          obj.filter = 'agSetColumnFilter'
          obj.cellEditor = 'datePicker'
          if(item.dateFormat.indexOf('-')!= -1){
            // 兼容之前的颜色格式化，待修改
            window.agGridMapList[item.fieldName] = obj.cellRendererParams.list = this.color2List(
            item.dateFormat
          )
          }else {
            window.agGridMapList[item.fieldName] = obj.cellRendererParams.list = this.getColList(
            item.dateFormat
          )
          }

          obj.filterParams.values = obj.cellRendererParams.list.map(i=>i.label)
          obj.valueFormatter = (params) => {
            if (!params.value) {
              return ''
            } else {
              let obj = params.colDef.cellRendererParams.list.find(
                (i) => i.value == params.value
              )
              return obj ? obj.label : ''
            }
          }
          break
        case 18:
          // 后台字典控件
          obj.filter = 'agSetColumnFilter'
          obj.cellEditor = 'datePicker'
          obj.valueFormatter = (params) => {
            if (!params.value) {
              return ''
            } else {
              return params.data[params.colDef.field + '_label']
            }
          }
          if (item.icon) {
            obj.cellRendererParams.icon = item.icon
            obj.cellRenderer = 'basicRender'
          }
          break
        case 8:
        case 9:
          // 多选控件
          obj.filter = 'agSetColumnFilter'
          obj.cellEditor = 'datePicker'
          obj.cellRendererParams.list = this.getColList(item.dateFormat)
          obj.valueFormatter = (params) => {
            if (!params.value) {
              return ''
            } else {
              let obj = params.colDef.cellRendererParams.list.find(
                (i) => i.value == params.value
              )
              return obj ? obj.label : ''
            }
          }
          if (item.icon) {
            obj.cellRendererParams.icon = item.icon
            obj.cellRenderer = 'basicRender'
          }
          break
        case 10:
          // JSON
          break
        case 11:
          // 数字格式化
          obj.filter = 'agNumberColumnFilter'
          obj.cellEditor = 'agTextCellEditor'
          if (item.icon) {
            obj.cellRendererParams.icon = item.icon
            obj.cellRenderer = 'basicRender'
          }
          let dateFormat = item.dateFormat
          let cache
          if (dateFormat == 'delZero') {
            cache = { delZero: true }
          } else {
            cache = JSON.parse(item.dateFormat)
          }

          if (cache.delZero) {
            obj.valueFormatter = (params) => {
              return Number(params.value)
            }
          } else if (cache.percentFormat) {
            obj.valueFormatter = (params) => {
              return Number(params.value) * 100 + '%'
            }
          } else if (cache.afterValue) {
            obj.valueFormatter = (params) => {
              return params.value + cache.afterValue
            }
          } else if (cache.beforeValue) {
            obj.valueFormatter = (params) => {
              return cache.beforeValue + params.value
            }
          } else if (cache.remainDays) {
            obj.cellClassRules = {
              'bg-red': (params) => params.value < 1,
              'bg-orange': (params) => params.value > 0 && params.value < 4,
              'bg-green': (params) => params.value > 3,
            }
          }
          break
        case 12:
          item.cellRenderer = 'htmlRender'
          item.autoHeight = true
          break
        case 20:
          // 之前的颜色格式化兼容
          obj.filter = 'agSetColumnFilter'
          obj.cellEditor = 'datePicker'
          window.agGridMapList[item.fieldName] = obj.cellRendererParams.list = this.color2List(
            item.dateFormat
          )
          obj.filterParams.values = obj.cellRendererParams.list.map(i=>i.label)
          obj.valueFormatter = (params) => {
            if (params.value === undefined) {
              return ''
            } else {
              let obj = params.colDef.cellRendererParams.list.find(
                (i) => i.value == params.value
              )
              return obj ? obj.label : ''
            }
          }
          obj.cellClassRules = {}
          obj.cellRendererParams.list.forEach(i=>{
            if(i.class){
              obj.cellClassRules[i.class] = (params)=>{
                let rule = 'params.value ==' + i.value
                return eval(rule)
              }
            }
          })
          break
        case 27:
          // 头像
          obj.autoHeight = true
          obj.cellRendererParams.width = 100
          obj.cellRendererParams.height = 100
          obj.cellRendererParams.imgSrc = "/api/static/avatar/${id_}.jpg"
          obj.cellRenderer = 'agImage'
          break
        case 28:
          // 手簽
          obj.autoHeight = true
          obj.cellRendererParams.width = 300
          obj.cellRendererParams.height = 100
          obj.cellRendererParams.imgSrc = "/api/static/signature/${id_}.png"
          obj.cellRenderer = 'agImage'
          obj.cellRendererParams.isShow = '${is_signatured} == 1'
          break
      }
      return obj
    },
    getColumnDefs() {
      // 项目机组，测试右边操作， 测试列分组, 列分组不能移动列
      let columnDefs = []
      let groupRules = []
      if(this.code == 'projectOrdersList'){
        groupRules = [
          {
            headerName: '分组测试2-5',
            min: 1,
            max: 6,
            marryChildren: true,
            children: []
          },
          {
            headerName: '分组测试6-9',
            min: 5,
            max: 12,
            marryChildren: true,
            children: []
          },
          {
            headerName: '分组测试13-',
            min: 18,
            marryChildren: true,
            suppressStickyLabel: true,
            openByDefault: false,
            children: []
          },
        ]
      }
      if(groupRules.length){
        this.listFields.forEach((item) => {
          columnDefs.push(this.transferItem(item))
        })
        for(let i = groupRules.length - 1; i > -1; i--){
          if(groupRules[i].min && groupRules[i].max){
            groupRules[i].children = columnDefs.filter((c, index)=> {
              return index > groupRules[i].min && index < groupRules[i].max
            })
            let minArr = columnDefs.filter((c, index)=>{
              return index < groupRules[i].min || index === groupRules[i].min
            })
            let maxArr = columnDefs.filter((c, index)=>{
              return index > groupRules[i].max || index === groupRules[i].max
            })
            columnDefs = [...minArr,groupRules[i],...maxArr]
          }else if(groupRules[i].min){
            groupRules[i].children = columnDefs.filter((c, index)=> {
              return index > groupRules[i].min
            })
            let minArr = columnDefs.filter((c, index)=>{
              return index < groupRules[i].min || index === groupRules[i].min
            })
            columnDefs = [...minArr,groupRules[i]]
          }else if(groupRules[i].max){
            groupRules[i].children = columnDefs.filter((c, index)=> {
              return index < groupRules[i].max
            })
            let maxArr = columnDefs.filter((c, index)=>{
              return index > groupRules[i].max || index === groupRules[i].min
            })
            columnDefs = [groupRules[i], ...maxArr]
          }
        }
      }else {
        this.listFields.forEach((item) => {
          columnDefs.push(this.transferItem(item))
        })
        // 列分组，不锁定列
        if (this.lockColumnCount) {
          columnDefs.forEach((c, i) => {
            if (i < this.lockColumnCount) {
              c.pinned = 'left'
            }
          })
        }
      }
      if (this.showNo == 1) {
          columnDefs.unshift({
            headerName: '序号',
            colId: 'rowNum',
            field: 'rowNum',
            valueGetter: 'Number(node.rowIndex) + 1',
            width: 40,
            minWidth: 40,
            pinned: this.lockColumnCount && !groupRules.length ? 'left' : false,
            suppressMenu: true,
          })
        }
      // 右侧添加固定操作按钮
      if(this.innerButtons && this.innerButtons.length){
        let width = 0
        this.innerButtons.forEach(b=>{
          width += b.name.length * 14
        })
        width += (this.innerButtons.length - 1) * 6
        width += 34
        columnDefs.push({
          headerName: '操作',
          colId: 'rowNum_1',
          field: 'rowNum_1',
          width: width,
          pinned: 'right',
          suppressMenu: true,
          cellRendererParams: this.innerButtons,
          cellRenderer: 'agOperations'
        })
      }
      if(groupRules.length){
        this.columnDefs = columnDefs
        console.log('分组columnDefs',columnDefs)
        return
      }
      setTimeout(()=>{
        if(this.localConfigs && this.localConfigs.columns && this.localConfigs.columns.length && this.expand.userLocal == 1){
          let cols = []
          this.localConfigs.columns.forEach(item=>{
            let temp = columnDefs.find(i=>i.field == item.colId)
            if(!!temp){
              let cache = Object.assign({}, temp, item)
              cols.push(cache)
            }
          })
          this.columnDefs = cols
          this.userLocalCols = true
        }else {
          this.columnDefs = columnDefs
        }
      },50)
    },
    init() {
      this.getColumnDefs()
      if(!this.height || this.height == 'auto'){
        let a = document.body.clientHeight
        let b = document.getElementById('top-buttons').scrollHeight
        this.tableHeight =  a - b - 70 - 36 - 60
      }else {
        this.tableHeight = this.height
      }
      this.getRowId = (params) => {
        return params.data[this.id]
      }
    },
  },
}
</script>
