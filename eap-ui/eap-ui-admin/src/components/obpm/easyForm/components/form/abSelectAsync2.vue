<template>
    <div class="select-async">
        <div class="select-input">
            <input
                    type="text"
                    class="n-b"
                    v-model="currentName"
                    placeholder="请输入搜索"
                    style="width:95%"
                    @blur="hideList"
                    @focus="showList"
                    @input="getList"
                    @keydown.down="selectItem(1)"
                    @keydown.up="selectItem(0)"
                    @keydown.enter="confirmIndex"
                    :disabled="(abPermission!='b'&&abPermission!='w')||disabled"
            ><i class="el-select__caret el-input__icon el-icon-circle-close" style="position: absolute;right: 0;display: table-cell;
            background-color: rgb(255, 255, 255);color: #666
            white-space: nowrap;cursor: pointer;line-height: 25px"
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

    export default {
        name: 'selectAsync2',
        props: {
            dialogKey: {
                type: String,
                default: '', //自定义对话框的key
            },
            queryParam: {},
            param: {},
            dialogSetting: {},
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
            model: {},
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

        },
        data: function () {
            return {
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

                initData: [],
                valueMap: {},
            }
        },
        mounted() {
            this.currentValue = this.value
        },
        watch: {
            currentName: function () {
                this.$emit('update:search', this.currentName);
            }
        },
        methods: {
            appClick: function (item) {
                this.currentValue = item[this.newParams]
                let currentName = this.handleValue(item)
                if (this.isSaveName) {
                    this.currentName = currentName
                } else {
                    this.currentName = ''
                }
                this.$emit('input', this.currentValue)
                this.$emit('update:name', item.name);
                this.$emit('change', item.key, item.name, item)

                var dataIgnoreCase = [];
                data.forEach(function (item) {
                    var json = {};
                    for (var key in item) {
                        json[key.toLowerCase()] = item[key];
                    }
                    dataIgnoreCase.push(json);
                });
                that.handleData(dataIgnoreCase);

                this.listShow = false
            },
            handleData: function (data) {
                var valueMap = this.valueMap;
                if (Array.isArray(this.model)) {//model是列表
                    var list = this.model;

                    data.forEach(function (item) {
                        if (JSON.stringify(valueMap) === "{}") {//无映射关系则把数据全返回
                            list.push(item);
                            return;
                        }

                        var json = {};
                        //处理映射关系
                        for (var key in valueMap) {
                            var val = valueMap[key] + "";
                            //如果val是a.b这样的，则需要对json.a初始化，不然直接操作json.a.b会报错
                            var strs = val.split(".");
                            var exp = "json";
                            for (var i = 0; i < strs.length - 1; i++) {
                                exp = exp + "." + strs[i];
                                if (eval("!" + exp)) {//为空则初始化
                                    eval(exp + " = {};");
                                }
                            }
                            eval("json." + val + " = item[key];");
                        }
                        list.push(json);
                    });
                } else {//组件是对象
                    let obj = {};
                    for (var key in valueMap) {
                        var val = valueMap[key];
                        var list = [];
                        data.forEach(function (item) {
                            list.push(item[key]);
                        });
                        this.$set(this.model, val, list.join(','));
                        obj[val] = list.join(',');
                    }
                    this.$emit('change', obj);
                }
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
            getList: function () {
                let vm = this
                vm.listShow = true
                if (vm.currentName) {
                    let params = {
                        order: 'asc',
                        offset: 0,
                        limit: 10,
                        fieldRelation: vm.fieldRelation,
                    }
                    // vm.changeParams.split(",").forEach((a, index, arr) => {
                    //     params[a] = vm.currentName
                    // })
                    let queryParam = this.getQueryParam();
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
            getQueryParam: function () {
                if (this.queryParam) return this.queryParam;
                // 老版本的获取方式
                if (this.param) {
                    try {
                        var paramJson = {};
                        var path = "";
                        for (var key in this.param) {
                            path = "this.$vnode.context." + this.param[key];
                            paramJson[key] = eval(path);
                        }
                        return paramJson;
                    } catch (e) {
                        console.error("获取对话框动态传参失败！", this.param, path);
                    }
                }
                return {};
            },
            getKey() {
                const url = "/etech/formCustDialog/listData_"
                this.api = url + this.dialogKey
                return this.dialogKey
            },
            getDialogConf() {
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
                                if (item.condition !== "EQ" && item.valueSource !== "fixedValue") {
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
            getInitData: function () {
                if (!this.model) return;
                if (Array.isArray(this.model)) {//数组不需要初始化，不会对数组进行删除行为
                    return [];
                }

                let initData = [];
                let json = null;
                for (let key in this.valueMap) {
                    let data = this.model[key];
                    if (!data) {
                        continue;
                    }
                    if (!json) {
                        json = {};
                    }
                    eval("json[key]=data");
                }

                if (!json) {
                    return initData;
                }

                //切割json中的,当作多选
                for (let key in json) {
                    let val = json[key] + "";
                    let list = val.split(",");
                    let index = 0;

                    list.forEach(function (item) {
                        if (!initData[index]) {
                            initData[index] = {};
                        }
                        initData[index][key] = item;
                        index++;
                    });
                }

                return initData;
            },
            getListByIds() {
                var vm = this
                let params = {
                    order: 'asc',
                    offset: 0,
                    limit: 10,
                    fieldRelation: vm.fieldRelation,
                }
                params[this.newParams] = vm.value + '' //id超长转换为String
                this.handleParam(params)
                vm.currentValue = vm.value + ''
                if (!vm.value) {
                    return
                }
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
                        }
                        vm.dataList = rows
                        // 初始化直接赋值
                        if (rows.length > 0) {
                            vm.currentName = rows[0].name
                            vm.$emit('input', vm.currentValue)
                            vm.$emit('update:name', vm.currentName);
                            if (vm.createChange) {
                                vm.$emit('change', vm.currentValue, vm.currentName, rows[0])
                            }
                        } else {
                            if (vm.createChange) {
                                vm.$emit('change', null, null, null)
                            }
                        }
                    },
                    'alert'
                )
            },
        },
        created: function () {
            let that = this;
            //映射关系
            for (let key in this.$attrs) {
                let val = this.$attrs[key];
                // 找到value开头的赋值配置
                if (key.indexOf("value-") !== 0) {
                    continue;
                }
                let name = key.replace("value-", "");
                this.valueMap[name] = val;
            }
            this.initData = this.getInitData();


            this.newParams = this.newParam
            this.changeParams = this.changeParam
            this.showParams = this.showParam
            let vm = this
            this.getDialogConf(function () {
                vm.getListByIds()
            })

        },
    }
</script>

