<template>
    <div class="select-async" style="height: auto">
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
            >
            <input
                    type="hidden"
                    v-model="currentValue"
            >
        </div>
        <div style="width: 100%" class="selected-list">
            <ul>
                <li v-for="(item,index) in selectList" :key="index">
                    <span class="name" v-html="item.name"></span>
                    <i class="el-icon-circle-close" @click="deleteItem(index)"
                       v-if="(abPermission=='b'||abPermission=='w') &&!disabled"></i>
                </li>
            </ul>
        </div>
        <div
                class="select-list"
                v-show="listShow"
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
                        :class="{'current': selectList.find(i=>i.key == item.key)}"
                >
                    <span v-html="item.name"></span>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
    import Vue from 'vue'

    export default {
        name: 'selectAsync',
        props: {
            placeholder: {
                type: String,
                default: '请输入搜索', //自定义对话框的key
            },
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
            showParam: {
                type: String,
                default: '', //显示的字段默认为参数字段，多个","分割
                required: false,
            },
            value: {
                type: [String, Number],
                default: '',
            },
            disabledCancelValue: {
                type: Array, //限制哪些不可取消选中
                default: () => {
                    return []
                },
            },
            disabledCancelTip: {
                type: String, //限制哪些不可取消选中
                default: "对不起,当前选项不能移除！",
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
                type: Number, //默认取前20条，如果为0取全部（全部数据太多可能会加载延迟）
                default: 20,
            },
            fixedParam: {
                type: Array, //固定参数 [{name:'',value:''}] 需要在条件中设置参数传入
                default: () => {
                    return []
                },
            },
        },
        data: function () {
            return {
                api: '',
                newParams: '',
                changeParams: '',
                showParams: '',
                dataList: [],
                selectList: [],
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
                    this.selectList = []
                    this.$emit('update:list', [])
                    this.$emit('change', '', '', [])
                } else {
                    this.getListByIds(false);
                }
            },
            currentName: function () {
                this.$emit('update:search', this.currentName); //搜索框输入的值
            },
            fixedParam: function () {
                if (this.fixedParam && this.fixedParam.length > 0) {
                    this.dataList = [];
                }
            },
        },
        methods: {
            deleteItem(index) {
                let newParams = this.newParams;
                let item = this.selectList[index]
                if (this.disabledCancelValue.length > 0 && this.disabledCancelValue.find((i) => i == item[newParams])) {
                    if (this.disabledCancelTip && this.disabledCancelTip.length > 0) {
                        $.Dialog.warning(this.disabledCancelTip);
                    }
                    return;
                }
                this.selectList.splice(index, 1)
                let vals = this.selectList.map(item => {
                    return item[newParams]
                });
                if (vals.length > 0) {
                    this.currentValue = vals.toString()
                } else {
                    this.currentValue = ''
                }
                this.$emit('input', this.currentValue)
                this.$emit('change', item[this.newParams], item[this.showParams], this.selectList)
                this.$emit('update:list', this.selectList)
            },
            appClick: function (item) {
                let newParams = this.newParams;
                console.log(this.selectList, item)
                let n = this.selectList.findIndex(i => i[newParams] == item[newParams])
                if (n == -1) {
                    this.selectList.push(item)
                    this.currentName = ''
                    this.$emit('handle-add', item[this.newParams], item[this.showParams], item)
                } else {
                    if (this.disabledCancelValue.length > 0 && this.disabledCancelValue.find((i) => i == item[newParams])) {
                        if (this.disabledCancelTip && this.disabledCancelTip.length > 0) {
                            $.Dialog.warning(this.disabledCancelTip);
                        }
                        return;
                    }
                    this.selectList.splice(n, 1)
                    this.$emit('handle-remove', item[this.newParams], item[this.showParams], item)
                }
                let vals = this.selectList.map(item => {
                    return item[newParams]
                });
                if (vals.length > 0) {
                    this.currentValue = vals.toString()
                } else {
                    this.currentValue = ''
                }
                this.$emit('input', this.currentValue)
                this.$emit('change', item[this.newParams], item[this.showParams], this.selectList)
                this.$emit('update:list', this.selectList)
                // this.listShow = false
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
                    let item = this.dataList[this.selectIndex]
                    this.appClick(item)
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
                        limit: vm.limit,
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
                const url = "/etech/formCustDialog/listData_"
                this.api = url + this.dialogKey
                return this.dialogKey
            },
            getApi(key) {
                const url = "/etech/formCustDialog/listData_"
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
                            if (index == 0) {
                                item.condition = 'IN'; //多选的支持主键多选获取值
                                item.dbType = 'varchar'; //数字类型换成字符串类型,否则接收不到搜索值导致in报错
                            }
                            if (index > 0) {
                                if (item.condition !== "EQ" && item.fieldRelation !== "and") {
                                    params.push(item.columnName)
                                } else {
                                    that.fixedParameter.push({//todo 固定值需要改接口或者
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
            getListByIds(isCreate) { //isCreate初始化时调用
                if (!this.newParams) {
                  return
                }
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
                    vm.$emit('update:list', [])
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
                            let ids = vm.value.split(',');
                            rows.sort(function (a, b) {
                                let indexa = ids.indexOf(a[vm.newParams] + '')
                                let indexb = ids.indexOf(b[vm.newParams] + '')
                                return indexa - indexb;
                            });
                        }
                        vm.dataList = rows
                        // 初始化直接赋值
                        if (rows.length > 0) {
                            vm.currentName = ''
                            vm.$set(vm, 'selectList', JSON.parse(JSON.stringify(rows)))
                            vm.$emit('input', vm.currentValue)
                            if (!isCreate || vm.createChange) {
                                vm.$emit('change', vm.currentValue, vm.currentName, rows)
                            }
                            vm.$emit('update:list', vm.selectList)
                        } else {
                            vm.$set(vm, 'selectList', [])
                            if (!isCreate || vm.createChange) {
                                vm.$emit('change', '', '', [])
                            }
                            vm.$emit('update:list', vm.selectList)
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

