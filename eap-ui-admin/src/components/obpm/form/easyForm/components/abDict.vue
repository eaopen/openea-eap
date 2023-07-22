<template>
    <el-select v-model="currentValue" filterable :allow-create="isAllowCreate" :id="uuid" clearable
               :placeholder="placeholder" size="mini"
               :disabled="disabled || (bpmPermission!='b' && bpmPermission!='w')">
        <el-option
                v-for="item in dataList"
                :key="item.key"
                :disabled="disabledDict(item)"
                v-if="filterDict(item)"
                :label="item.name"
                :value="item.key"
                @click.native="optionClick(item)"
        >
        </el-option>
    </el-select>
</template>

<script>
    import Vue from 'vue'
    import { generateUUID } from '@/utils/easyForm'
    import {getDictData} from '@/api/obpm/sys.js'
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
            disabled: {
                type: [Boolean, String],
                default: false,
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
            returnList: { //把下拉的所有数据返回，特殊情况需要,获取所有列来计算的
                type: Array,
                default: () => {
                    return []
                },
            },
            createChange: { //组件第一次初始化是否调用Change方法，防止联动后修改，编辑或详情展示的时候又被改回去的问题
                type: Boolean,
                defalut: false //默认不调用
            },
            filterValue: {
                type: [String, Number],
                default: '',
            },
            filterName: {
                type: String,
                default: 'parentid',
            },
            //支持多条件的级联选框
            filterValueList: {
                type: Array,
                default:()=> [],
            },
            filterNameList: {
                type: Array,
                default:()=> [],
            },
            // filterEq: {
            //     type: Boolean,
            //     default: true,
            // },
            placeholder: {
                type: String,
                default: '请选择',
            },
            abPermission: {
                type: String,
                default: ''
            },
            isAllowCreate: {
                type: Boolean,
                defalut: false
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
                }else if (this.filterValueList.length>0 && this.filterNameList.length>0){ //过滤options
                    if (dict.extData) {
                        let flag = true
                        for(let i=0;i<this.filterNameList.length;i++){
                            let arr = this.filterValueList[i].split(';')  //同一个name对应多个value  用;分割
                            for(let j=0;j<arr.length;j++){
                                flag = dict.extData[this.filterNameList[i]] == arr[j];
                                if(flag){
                                    break;
                                }
                            }
                            if(!flag){
                                break;
                            }
                        };
                        return flag;
                    } else {
                        return false;
                    }
                }
                return true;
            },
            disabledDict(dict) {
                return this.disabledValue.findIndex(i => i == dict.key) !== -1 ||
                    (this.enabledValue && this.enabledValue.findIndex(i => i == dict.key) === -1)
                    || (dict.extData && dict.extData.active_ === 0)
            },
            updateSelect() {
                if (this.loaded && this.currentValue) {
                    let that = this;
                    let dict = this.dataList.filter(item => item.key == that.currentValue);
                    if (!dict || this.disabledDict(dict) || !this.filterDict(dict)) {
                        this.$emit('input', '');
                    }
                }
            },
            optionClick(item){
                if (this.newItemMap[item.key]!=undefined){
                    $(`#${this.uuid}`).val(item.name);
                }

            }
        },
        data: function () {
            return {
                dataList: [],
                currentValue: "",
                bpmPermission: '',
                uuid: generateUUID(),
                loaded: false, //数据是否加载完成
                newId:'',
                newName:'',
                newItemMap:{},
            }
        },
        created: function () {
            var vm = this;
            if (!vm.dictKey) return;
            getDictData(this.dictKey).then(res=>{
              if (vm.currentValue) {
                    const data = res.data
                    const option = data.filter((item, index, array) => item.key == vm.currentValue);
                    const name = option.map(item => {
                        return item.name;
                    }).toString();
                    vm.$emit('update:name', name);
                    let dataList = data.filter(item=>{
                        let flag = true
                        if(item.extData && item.extData.active_ === 0 && item.key != vm.currentValue){
                            flag = false
                        }
                        return flag
                    })
                    vm.dataList = dataList
                    vm.$emit('update:returnList', dataList);
                    if (vm.createChange) {
                        vm.$emit('change', vm.currentValue, name, option && option.length == 1 ? option[0] : option);
                    }
                }else {
                    let dataList = data.filter(item=>{
                        let flag = true
                        if(item.extData && item.extData.active_ === 0){
                            flag = false
                        }
                        return flag
                    })
                    vm.dataList = dataList
                    vm.$emit('update:returnList', dataList);
                }

                vm.loaded = true
            })
        },
        mounted: function () {
            if(this.value){
                this.currentValue = '' + this.value;
            }
            if (this.abPermission && this.abPermission.length > 0) {
                this.bpmPermission = this.abPermission
            }
        },
        computed: {
            listenChange() {
                const {filterValue, filterName, disabledValue, enabledValue} = this
                return {filterValue, filterName, disabledValue, enabledValue}
            }
        },
        watch: {
            //数据更新时
            value: function (newVal, oldVal) {
                this.currentValue = '' + this.value;
                let that = this;
                const option = this.dataList.filter(item => item.key == that.currentValue);
                const name = option.map(item => {
                    return item.name;
                }).toString();
                this.$emit('input', this.currentValue);
                this.$emit('update:name', name);
                if (this.loaded || this.createChange) {
                    this.$emit('change', this.currentValue, name, option && option.length == 1 ? option[0] : option);
                }
            },

            // 点击新创建条目时， val 为新条目文本。
            // that.$emit('input', that.newId) 返回新 id 后，会再次触发本函数 currentValue(val) 此时 val 为 newId 。
            currentValue(val) {
                // console.log("currentValue",val);
                let that = this;
                if (this.loaded) {
                    if (this.isAllowCreate) {
                        // 清空选项时触发 this.$emit('new',undefined,undefined) 用于重置数据
                        if (!val){
                            that.newId = '';
                            that.newName = '';

                            this.$emit('input', val);
                            this.$emit('new',undefined,undefined);
                            this.$parent.$forceUpdate();

                            return;
                        }

                        // 判断是否为新主键，阻断循环触发。
                        if (this.newId && this.newId == val){
                            return;
                        }

                        const option = this.dataList.filter(item => item.key == val);
                        if (option.length) {
                            // 新条目会 push 到 dataList 中，如果是新条目，触发 that.$emit('new', that.newId, that.newName);
                            let newName = this.newItemMap[val];
                            if (newName){
                                that.newId = val;
                                that.newName = newName;

                                that.$emit('input', that.newId);
                                that.$emit('new', that.newId, that.newName);
                                that.$parent.$forceUpdate();

                            }else {
                                that.newId = '';
                                that.newName = '';

                                this.$emit('input', val);
                                this.$emit('new',undefined,undefined);
                                this.$parent.$forceUpdate();

                            }

                        } else {

                            axios.defaults.withCredentials = true;
                            let req = `${__ctx}/util/getSnowFlakeId`;
                            axios.get(req).then(res => {
                                that.newId = res.data;
                                that.newName = val;
                                that.$emit('input', that.newId);
                                that.$emit('new', that.newId, that.newName);
                                setTimeout(()=>{
                                    $(`#${that.uuid}`).val(that.newName);
                                },100);
                                that.$parent.$forceUpdate();

                                that.newItemMap[that.newId] = val;

                                that.dataList.unshift({
                                    key:that.newId,
                                    name:val
                                })

                                setTimeout(()=>{
                                    $(`.el-select-dropdown__item:contains(${that.newId})`).css({display:"none"});
                                    // console.log("change dom",$(`#${that.uuid}`).val(val))
                                },100)

                            });

                        }

                    } else {
                        this.$emit('input', val);
                        this.$parent.$forceUpdate();

                    }

                }
            },
            listenChange: function (val) {
                this.updateSelect();
            },
            abPermission: function (val) {
                this.bpmPermission = val
            }
        }
    }
</script>
