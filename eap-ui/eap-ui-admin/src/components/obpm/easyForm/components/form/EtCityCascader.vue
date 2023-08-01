<template>
    <el-cascader
            ref="cascader"
            clearable
            filterable
            v-model="data"
            :props="props"
            :disabled="disabled || (abPermission!='b' && abPermission!='w')"
            @change="handleChange()"
            placeholder="请选择省市区"
            class="et-cascader">
    </el-cascader>
</template>

<script>
    import Vue from 'vue'
    import axios from 'axios'

    export default {
        name: "EtCityCascader",
        props: {
            value: '',
            disabled: {
                type: [Boolean, String],
                default: false,
            },
            abPermission: {
                type: String,
                default: ''
            }
        },
        data: function () {
            return {
                data:[],
                props: {
                    value: "name",
                    label: "name",
                    leaf: "isLeaf",
                    checkStrictly: true,
                    lazy: true,
                    lazyLoad(node, resolve) {
                        let cityId = node.root ? 0 : node.data.cityId;

                        let that = this;
                        axios.defaults.withCredentials = true;
                        let req = `${__ctx}/common/city/getCity/${cityId}`;
                        axios.get(req).then(res => {
                            if (!res.data.isOk) {
                                top.$.Dialog.error(`错误：${res.data.msg}`);
                                return;
                            }

                            resolve(res.data.data);
                        });


                    },
                },
            }
        },
        methods: {
            handleChange() {
                let checkedNodes = this.$refs.cascader.getCheckedNodes();
                if (!checkedNodes[0]) {
                    this.$emit('choose-city-arr', []);
                    return;
                }

                let chooseCityArr = [];
                checkedNodes[0].pathNodes.forEach(item => {
                    chooseCityArr.push(item.label);
                });

                this.$emit('input', chooseCityArr.join("/"));

            }

        },
        mounted: function () {
            this.data = this.value ? this.value.split("/") : '';

        },
    }
</script>

