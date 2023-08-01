<template>
    <div class="select-async mult" :id="uuid + '_select'" @mouseleave="listShow= false">
        <div class="select-input2" @click="showList()" style="cursor: pointer;min-height: 24px; border-bottom: 1px solid #999999">
            <div class="selected">
                <span v-if="currentList.length==0" style="color:rgb(158 158 158)">{{placeholder}}</span>
				<span
                        v-for="(item, index) in currentList"
                        :key="index"
                        class="selected-span"
                        style="color:#999999; background-color: #f4f4f5;border-radius: 10px"
                >
					<span class="selected-name">
						{{item.name}}
					</span>
                    <i class="el-icon-circle-close" v-if="abPermission=='b'||abPermission=='w'"  @click="deleteItem(item)"></i>
				</span>
            </div>
            <input
                type="text"
                class="n-b"
                v-model="currentValue"
                style="display: none"
            >
            <!-- readonly="readonly" -->
            <!-- :disabled="abPermission!='b'&& abPermission!='w'" -->
        </div>
        <div
                class="select-list2"
                v-show="listShow"
                style="{'top': inputHeight + px}"
        >
            <ul>
                <li
                        v-for="(item,index) in dataList"
                        v-if="filterDict(item)"
                        :key="index"
                        style="cursor: pointer"
                        @click="appClick(item)"
                        :disabled="disabledDict(item)"
                        :class="{'current': currentList.find(i=>i.key === item.key)}"
                >
                    <span v-html="item.name"></span>
                </li>
                <!-- , 'selected': index == selectIndex -->
            </ul>
        </div>
    </div>
</template>

<script>
    import Vue from 'vue'
    import { generateUUID } from '@/utils/easyForm'
    export default {
        props: {
            dictKey: {
                type: String,
                default: '',
            },
            value: {
                type: [String, Number],
                default: '',
            },
            disabledValue: {
                type: Array, //限制哪些不可选中
                default: () => {
                    return []
                },
            },
            enabledValue: {
                type: Array, //限制只有哪些可选中
                default: () => {
                    return null //如果传空数组所有都不能选中
                },
            },
            disabledCancelValue: {
                type: Array, //限制哪些不可取消选中
                default: () => {
                    return []
                },
            },
            disabledCancelTip: {
                type: String, //限制哪些不可取消选中
                default: "对不起,当前选项不能取消！",
            },
            returnList: {
                //把下拉的所有数据返回，特殊情况需要,获取所有列来计算的
                type: Array,
                default: () => {
                    return []
                },
            },
            createChange: { //组件第一次初始化是否调用Change方法，防止联动后修改，编辑或详情展示的时候又被改回去的问题
                type: Boolean,
                defalut: false //默认启用
            },
            abPermission: {},
            filterValue: {
                type: [String, Number],
                default: '',
            },
            filterName: {
                type: String,
                default: 'parentid',
            },
            placeholder: {
                type: String,
                default: '请选择',
            },
        },
        data: function () {
            return {
                dataList: [],
                currentValue: '',
                currentList: [],
                listShow: false,
                inputHeight: 36,
                uuid: generateUUID(),
                loaded: false, //数据是否加载完成
            }
        },
        created: function () {
            this.currentValue = this.value
            var vm = this
            if (!vm.dictKey) return
            var defer = Vue.baseService.get(
                Vue.__ctx + '/sys/dataDict/getDictData?dictKey=' + this.dictKey
            )
            Vue.tools.getResultData(
                defer,
                function (data) {
                    vm.dataList = data
                    vm.$emit('update:returnList', data)
                    if (vm.currentValue) {
                        let currentCache = vm.currentValue.split(',')
                        vm.currentList = vm.dataList.filter(
                            (item) => currentCache.indexOf(item.key + '') !== -1
                        )
                        console.log(vm.currentList,3333);
                        vm.currentList.sort(function (a, b) {
                            let indexa = currentCache.indexOf(a.key + '')
                            let indexb = currentCache.indexOf(b.key + '')
                            return indexa - indexb;
                        });
                        const name = vm.currentList.map(item => {
                            return item.name;
                        }).toString();
                        vm.$emit('update:name', name)
                        if (vm.createChange) {
                            vm.$emit('change', vm.currentValue, name, vm.currentList ? vm.currentList : [])
                        }
                        vm.$emit('update:list', vm.currentList ? vm.currentList : [])
                    }
                    vm.loaded = true;
                },
                'alert'
            )
        },
        mounted: function () {
            this.currentValue = this.value
        },
        listenChange() {
            const {filterValue, filterName, disabledValue, enabledValue} = this
            return {filterValue, filterName, disabledValue, enabledValue}
        },
        watch: {
            //数据更新时
            value: function (newVal, oldVal) {
                this.currentValue = this.value
                let currentCache = this.currentValue.split(',')
                const option = this.dataList.filter(item => currentCache.indexOf(item.key + '') > -1)
                option.sort(function (a, b) {
                    let indexa = currentCache.indexOf(a.key + '')
                    let indexb = currentCache.indexOf(b.key + '')
                    return indexa - indexb;
                });
                this.currentList = option;
                const name = option.map((item) => {
                    return item.name
                }).toString()
                this.$emit('input', this.currentValue);
                this.$emit('update:name', name)
                if (this.loaded || this.createChange) {
                    this.$emit('change', this.currentValue, name, option)
                }
                if (this.currentList.length > 0 && (!this.currentValue || this.currentValue == '' || this.currentValue == 0)) {
                    this.currentList = [];
                }
                this.$emit('update:list', this.currentList)
            },
            listenChange: function (val) {
                this.updateSelect();
            },
        },
        methods: {
            filterDict(dict) {
                if ((this.filterValue && (this.filterValue != 0 && this.filterValue != '')) && this.filterName) {
                    /*if (this.filterName === 'key' || this.filterName === 'name') {
                        return this.filterEq ? dict[this.filterName] == this.filterValue : dict[this.filterName] != this.filterValue;
                    } else if (dict.extData) {
                        return this.filterEq ? dict.extData[this.filterName] == this.filterValue : dict.extData[this.filterName] != this.filterValue;
                    } else {
                        return this.filterEq ? false : true;
                    }*/
                    if (this.filterName === 'key' || this.filterName === 'name') {
                        return dict[this.filterName] == this.filterValue;
                    } else if (dict.extData) {
                        return dict.extData[this.filterName] == this.filterValue;
                    } else {
                        return false;
                    }
                }
                return true;
            },
            disabledDict(dict) {
                return this.disabledValue.findIndex(i => i == dict.key) !== -1 ||
                    (this.enabledValue && this.enabledValue.findIndex(i => i == dict.key) === -1);
            },
            updateSelect() {
                if (this.loaded && this.currentValue) {
                    let currentCache = this.currentValue.split(',')
                    this.currentList = this.dataList.filter(item => currentCache.indexOf(item.key + '') > -1) || [];
                    if (!this.currentList || this.currentList.length === 0) {
                        this.$emit('input', '');
                    } else {
                        let values = this.currentList.map(item => {
                            return item.key
                        }).toString();
                        if (values != this.currentValue) {
                            this.$emit('input', values);
                        }
                    }
                }
            },
            appClick: function (item) {
                let isAdd = true
                if (!this.currentList.length) {
                    if (this.disabledDict(item)) {
                        return;
                    }
                    this.currentList.push(item)
                } else {
                    if (this.currentList.find((i) => i.key == item.key)) {
                        if (this.disabledCancelValue.length > 0 && this.disabledCancelValue.find((i) => i == item.key)) {
                            if (this.disabledCancelTip && this.disabledCancelTip.length > 0) {
                                $.Dialog.warning(this.disabledCancelTip);
                            }
                            return;
                        }
                        this.currentList = this.currentList.filter((i) => i.key !== item.key)
                        isAdd = false
                    } else {
                        if (this.disabledDict(item)) {
                            return;
                        }
                        this.currentList.push(item)
                    }
                }
                console.log(this.currentList)
                this.currentValue = this.currentList.map((i) => i.key).join(',')
                // let currentName = this.handleValue(item)
                // if (this.isSaveName) {
                // 	this.currentName = currentName
                // } else {
                // 	this.currentName = ''
                // }
                if (isAdd) {
                    this.$emit('handle-add', item.key, item.name, item)
                } else {
                    this.$emit('handle-remove', item.key, item.name, item)
                }
                this.$emit('input', this.currentValue)
                //   this.listShow = false
            },
            deleteItem(option) {
                if (this.disabledCancelValue.length > 0 && this.disabledCancelValue.find((i) => i == option.key)) {
                    $.Dialog.warning(this.disabledCancelTip);
                    return;
                }
                let index = this.currentList.findIndex((item) => item.key === option.key)
                this.currentList.splice(index, 1)
                this.$emit('handle-remove', option.key, option.name, option)
                this.currentValue = this.currentList.map((i) => i.key).join(',')
                this.$emit('input', this.currentValue)
            },
            showList: function () {
                if (this.abPermission != 'b' && this.abPermission != 'w') {
                    return false
                }
                this.listShow = true
                //   this.setIndex()
            },
            setIndex() {
                if (this.dataList && this.dataList.length) {
                    let index = this.dataList.findIndex((item) => item.name == this.value)
                    index === -1 ? (this.selectIndex = 0) : (this.selectIndex = index)
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
            handleValue(item) {
                let arr = []
                if (this.showParams) {
                    arr = this.showParams.split(',')
                } else {
                    arr = this.changeParams.split(',')
                }
                let name = []
                arr.forEach((a) => {
                    name.push(item[a])
                })
                return name.join(' ')
            },
        },
    }
</script>
