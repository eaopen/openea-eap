<template>
    <div class="select-async" :id="uuid">
        <div class="select-input">
            <input
                    type="text"
                    class="n-b"
                    v-model="currentName"
                    :placeholder="placeholder"
                    style="width:95%"
                    @blur="hideList"
                    @focus="showList"
                    @input="getListWithTimeOut"
                    @keydown.down="selectItem(1)"
                    @keydown.up="selectItem(0)"
                    @keydown.enter="confirmIndex"
                    :disabled="(abPermission!='b'&&abPermission!='w')||disabled"
            ><i class="el-select__caret el-input__icon el-icon-circle-close" style="position: absolute;right: 0;display: table-cell;
            background-color: rgb(255, 255, 255); color: #666;
            white-space: nowrap;cursor: pointer; line-height: 25px"
                v-if="(abPermission=='b'||abPermission=='w') && currentValue && isSaveName &&!disabled"
                @click="clearValue">
        </i>
            <input
                    type="hidden"
                    v-model="currentValue"
            >
        </div>
        <div
                class="select-list"
                v-show="listShow && dataList.length"
        >
            <div
                    v-if="!dataList.length"
                    style="padding-left: 10px"
            >请重新搜索
            </div>
            <ul v-else>
                <li
                        v-for="(item,index) in dataList"
                        :key="index"
                        style="cursor: pointer"
                        @click="appClick(item)"
                        :class="{'current': item.key == currentValue, 'selected': index == selectIndex}"
                >
                    <span v-html="item.name"></span>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
    import Vue from 'vue'
    import { generateUUID } from '@/utils/easyForm'
    export default {
        name: 'selectAsync',
        props: {
            dialogKey: {
                type: String,
                default: '', //自定义对话框的key
            },
            apiMethod: {
                type: String,
                default: 'postForm',
            },
            newParam: {
                type: String,
                default: '',
            },
            changeParam: {
                type: String,
                default: '',
            },
            placeholder: {
                type: String,
                default: '请输入搜索',
            },
            showParam: {
                type: String,
                default: '', //显示的字段默认为参数字段，多个","分割
                required: false,
            },
            value: {
                type: [String, Number],
                default: '',
            },
            isSaveName: {
                type: Boolean,
                default: true, //是否需要保存name,如果不保存选中值后name不会赋值到输入框，并把输入框的搜索条件清空,特殊情况使用
            },
            abPermission: {},
            createChange: { //组件第一次初始化是否调用Change方法，防止联动后修改，编辑或详情展示的时候又被改回去的问题
                type: Boolean,
                defalut: false //默认不调用
            },
            disabled: {
                type: Boolean,
                defalut: false //默认不禁用
            },
            search: {
                type: String, //默认输入框里面的值（v-model没值时）
                default: '',
                required: false,
            },
            limit: {
                type: Number, //默认取前10条，如果为0取全部（全部数据太多可能会加载延迟）
                default: 100,
            },
            fixedParam: {
                type: Array, //固定参数 [{name:'',value:''}] 需要在条件中参数传入
                default: () => {
                    return []
                },
            },
        },
        data: function () {
            return {
                uuid: generateUUID(),

                api: '',
                newParams: '',
                changeParams: '',
                showParams: '',
                dataList: [],
                currentValue: '',
                currentName: '',
                listShow: false,
                selectIndex: 0,
                timer: null,
                dialogConf: {conditionFields: []},
                fieldRelation: 'OR', //多个条件默认OR
                fixedParameter: [], //固定参数
                timeout: null,//用于屏蔽输入法不必要请求的计时器
            }
        },
        mounted() {
            console.log(this.placeholder)
            this.currentValue = this.value
            if (!this.value && this.search && this.search.length > 0) {
                this.currentName = this.search
            }
        },
        watch: {
            value: function (newValue) {
                //数字类型变为字符串类型的这种忽略不去重新查询防止不该调用change时调用change方法
                if (this.currentValue == newValue) {
                  return;
                }
                if (this.currentValue != newValue && (!newValue || newValue == '')) {
                    this.currentValue = newValue
                    this.currentName = ''
                    this.$emit('update:name', '');
                    this.$emit('change', '', '', null)
                } else {
                    //todo 优化选中完多查一次
                    this.getListByIds(false)
                }
            },
            currentName: function () {
                this.$emit('update:search', this.currentName);
            },
            fixedParam: function () {
                if (this.fixedParam && this.fixedParam.length > 0) {
                    this.dataList = [];
                }
            },
        },
        methods: {
            appClick: function (item) {
                this.currentValue = item[this.newParams]
                let currentName = this.handleValue(item)
                currentName = currentName.replace(/<[^>]+>/g, "");
                if (this.isSaveName) {
                    this.currentName = currentName
                } else {
                    this.currentName = ''
                }
                this.$emit('input', this.currentValue)
                this.$emit('update:name', item.name);
                this.$emit('change', item.key, item.name, item)
                this.listShow = false
            },
            showList: function () {
                this.listShow = true
                this.setIndex()
            },
            setIndex() {
                if (this.dataList && this.dataList.length) {
                    let index = this.dataList.findIndex(item => item.name == this.value)
                    index === -1 ? this.selectIndex = 0 : this.selectIndex = index
                } else {
                    this.selectIndex = -1
                }
            },
            hideList: function () {
                let vm = this
                if (vm.timer) {
                    clearTimeout(vm.timer)
                }
                vm.timer = setTimeout(() => {
                    vm.listShow = false
                }, 300)
            },
            selectItem: function (state) {
                if (state == 1) {
                    if (
                        this.dataList.length &&
                        this.selectIndex < this.dataList.length - 1
                    ) {
                        this.selectIndex++
                    }
                } else {
                    if (this.dataList.length && this.selectIndex > 0) {
                        this.selectIndex--
                    }
                }
            },
            confirmIndex() {
                if (this.listShow) {
                    this.currentValue = this.dataList[this.selectIndex][this.newParams]
                    let currentName = this.handleValue(this.dataList[this.selectIndex])
                    if (this.isSaveName) {
                        this.currentName = currentName
                    } else {
                        this.currentName = ''
                    }
                    this.$emit('input', this.currentValue)
                    this.$emit('update:name', currentName);
                    this.$emit('change', this.currentValue, currentName, this.dataList[this.selectIndex])
                    this.listShow = false
                } else {
                    this.listShow = true
                }
            },
            getListWithTimeOut() {
                clearTimeout(this.timeout) //屏蔽由于输入法本身特性发起的不必要的请求
                this.timeout = setTimeout(() => {
                    this.getList()
                }, 0)
            },
            getList: function () {
                let vm = this
                vm.listShow = true
                if (vm.currentName) {
                    let params = {
                        order: 'asc',
                        offset: 0,
                        limit: this.limit,
                        fieldRelation: vm.fieldRelation,
                    }
                    if (this.limit == 0) {
                        delete params.limit
                    }
                    vm.changeParams.split(",").forEach((a, index, arr) => {
                        params[a] = vm.currentName
                    })
                    if (this.fixedParam && this.fixedParam.length > 0) {
                        this.fixedParam.forEach(item => {
                            params[item.name] = item.value
                        })
                    }
                    this.handleParam(params)
                    var defer = Vue.baseService[vm.apiMethod](Vue.__ctx + vm.api, params)
                    Vue.tools.getResultData(
                        defer,
                        function (rows) {
                            if (rows && rows.length) {
                                rows.forEach((item) => {
                                    vm.$set(item, 'key', item[vm.newParams])
                                    let name = vm.handleValue(item)
                                    vm.$set(item, 'name', name.trim() ? name : '空')
                                })
                                vm.$set(vm, 'dataList', rows)
                            } else {
                                vm.$set(vm, 'dataList', [])
                            }
                            // 初始化直接赋值
                        },
                        'alert'
                    )
                } else {
                    vm.$set(vm, 'dataList', [])
                }
            },
            getKey() {
                const url = "/form/formCustDialog/listData_"
                this.api = url + this.dialogKey
                return this.dialogKey
            },
            getApi(key) {
                const url = "/form/formCustDialog/listData_"
                return url + (key ? key : this.dialogKey)
            },
            getDialogConf(callback) {
                const key = this.getKey();
                if (!key) {
                    return;
                }
                let that = this
                $.ajax({
                    type: "post",
                    url: __ctx + "/form/formCustDialog/getObject?key=" + key,
                    data: {},
                    dataType: 'json',
                    success: function (result) {
                        var dialogConf = result.data;
                        if (!dialogConf) {
                            jQuery.Dialog.error("请确认自定义对话框接口正确-" + key);
                            return;
                        }
                        that.dialogConf = dialogConf
                        let params = [];
                        //删掉不是参数传入的，脚本和固定值的后台会自动处理
                        dialogConf.conditionFields = dialogConf.conditionFields.filter(item => {
                            return item.valueSource !== 'fixedValue'
                        })
                        dialogConf.conditionFields.forEach((item, index, arr) => {
                            if (index > 0) {
                                if (item.condition !== "EQ" && item.fieldRelation !== "and") {
                                    params.push(item.columnName)
                                } else {
                                    that.fixedParameter.push({
                                        name: item.columnName,
                                        value: item.value.text
                                    })
                                }
                            } else if (!that.newParams) {
                                that.newParams = item.columnName
                            }
                        })
                        if (!that.changeParams) { //如果没有传参数默认条件参数除了第一个主键和其他固定参数的其他参数
                            that.changeParams = params.toString()
                        }
                        if (!that.showParams) { //默认显示的是显示字段的值
                            let displayFields = [];
                            dialogConf.displayFields.forEach((item) => {
                                displayFields.push(item.columnName)
                            })
                            that.showParams = displayFields.toString()
                        }
                    },
                    complete() {
                        callback()
                    }
                });
            },
            handleParam: function (param) {
                let dialog = this.dialogConf;
                if (!param) {
                    return;
                }
                jQuery.each(dialog.conditionFields, function (index, field) {
                    jQuery.each(param, function (key, val) {
                        if (key !== field.columnName) {//找到key等于条件参数的
                            return;
                        }
                        //帮忙key拼装配置中的查询条件，当然如果param中的key有写好的^这种写法这里是不干预的
                        var id = field.columnName + "^";
                        if (field.dbType === "varchar") {
                            id += "V";
                        }
                        if (field.dbType === "number") {
                            id += "N";
                        }
                        if (field.dbType === "date") {
                            id += "D";
                        }
                        id += field.condition;
                        param[id] = val;
                    });
                });
            },
            handleValue(item) {
                let arr = []
                if (this.showParams) {
                    arr = this.showParams.split(",")
                } else {
                    arr = this.changeParams.split(",")
                }
                let name = []
                arr.forEach((a) => {
                    name.push(item[a])
                })
                return name.join(" ")
            },
            clearValue() {
                if (this.currentValue) {
                    this.currentValue = ""
                    this.currentName = ""
                    this.$emit('input', this.currentValue)
                    this.$emit('update:name', this.currentName);
                    this.$emit('change', this.currentValue, this.currentName, null)
                }
            },
            getListByIds(isCreate) {
                var vm = this
                let params = {
                    order: 'asc',
                    offset: 0,
                    fieldRelation: vm.fieldRelation,
                }
                params[this.newParams] = vm.value + '' //id超长转换为String
                this.handleParam(params)
                vm.currentValue = vm.value + ''
                if (!vm.value) {
                    return
                }
                let api = vm.api;
                if (isCreate && this.dialogKey === 'ygxz') {
                    api = vm.getApi("ygxz_all");
                }
                var defer = Vue.baseService[vm.apiMethod](Vue.__ctx + api, params)
                Vue.tools.getResultData(
                    defer,
                    function (rows) {
                        if (rows && rows.length) {
                            rows.forEach((item) => {
                                vm.$set(item, 'key', item[vm.newParams])
                                let name = vm.handleValue(item)
                                vm.$set(item, 'name', name.trim() ? name : '空')
                            })
                        }
                        vm.dataList = rows
                        // 初始化直接赋值
                        if (rows.length > 0) {
                            //done 去掉html标签
                            vm.currentName = rows[0].name.replace(/<[^>]+>/g, "");
                            vm.$emit('input', vm.currentValue)
                            vm.$emit('update:name', vm.currentName);
                            if (!isCreate || vm.createChange) {
                                vm.$emit('change', vm.currentValue, vm.currentName, rows[0])
                            }
                        } else {
                            if (!isCreate || vm.createChange) {
                                vm.$emit('change', null, null, null)
                            }
                        }
                    },
                    'alert'
                )
            }
        },
        created: function () {
            this.newParams = this.newParam
            this.changeParams = this.changeParam
            this.showParams = this.showParam
            let that = this
            // this.getDialogConf(function () {
            //     that.getListByIds(true)
            // })

        },
    }
</script>

