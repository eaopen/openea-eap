<template>
    <el-cascader
            ref="cascader"
            clearable
            filterable
            v-model="data"
            :options="options"
            :props="props"
            :disabled="disabled || (abPermission!='b' && abPermission!='w')"
            @change="handleChange()"
            placeholder="请选择"
            class="et-cascader">
    </el-cascader>
</template>

<script>
    import axios from 'axios'

    export default {
        name: "EtCascader",
        props: {
            value: '',
            config: {},
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
            let that = this;
            return {
                data: [],
                options:[],
                props: {
                    value: that.config.valueField,
                    label: that.config.labelField,
                    leaf: that.config.leafField,
                    checkStrictly: true,
                    lazy: that.config.isLazy ? that.config.isLazy : false,
                    lazyLoad(node, resolve) {
                        axios.defaults.withCredentials = true;
                        let req = `${__ctx}${that.config.url}`;

                        let data = {};
                        if (node.root) {
                            data = node;
                        } else {
                            data.label = node.label;
                            data.value = node.value;
                            data.level = node.level;
                            data.data = node.data;
                        }

                        axios.post(req, data).then(res => {
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
                if (this.config.isJson) {
                    this.$emit('input', this.data ? JSON.stringify(this.data) : "[]");

                } else {
                    this.$emit('input', this.data.join(","));

                }

            }

        },
        mounted: function () {
            if (this.config.isJson) {
                this.data = this.value ? JSON.parse(this.value) : [];

            } else {
                this.data = this.value ? this.value.split(",") : [];

            }

            let that = this;
            axios.defaults.withCredentials = true;
            let req = `${__ctx}${that.config.url}`;
            axios.get(req).then(res => {
                if (!res.data.isOk) {
                    top.$.Dialog.error(`错误：${res.data.msg}`);
                    return;
                }

               that.options = res.data.data;
            });

        },
    }
</script>

