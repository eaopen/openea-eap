<template>
     <el-date-picker
      v-model="currentValue"
      size="mini"
      :type="type"
      :value-format="vFormat"
      :pickerOptions="pickerOptions"
      :placeholder="placeholder">
    </el-date-picker>
</template>

<script>
    export default {
        name: 'datePicker',
        props: {
            value: {
                type: String
            },
            placeholder: {
                default: '请选择日期',
                type: String
            },
            type: {
                default: 'date',
                type: String
            },
            clearable: {
                default: true,
                type: Boolean
            },
            pickoption: {
                type: String
            },
        },
        computed:{
            vFormat(){
                if(this.type == 'datetime'){
                    return 'yyyy-MM-dd HH:mm:ss'
                }else if(this.type == 'month'){
                    return 'yyyy-MM'
                }else if(this.type == 'year'){
                    return 'yyyy'
                }else if(this.type == 'week'){
                    return 'yyyy 年第 WW 周'
                }else {
                    return 'yyyy-MM-dd'
                }
            }
        },
        data: function () {
            return {
                currentValue: '',
                pickerOptions: {
                    disabledDate: (time)=>{
                        console.log(time)
                        console.log(this.pickoption)
                        if(!this.pickoption){
                            return
                        }else if(this.pickoption == 't+'){
                            return time.getTime() < Date.now()
                        }else if(this.pickoption == 't-'){
                            return time.getTime() > Date.now()
                        }else if(this.pickoption.startsWith('t+')){
                            let n = this.pickoption.replace('t+','')-0
                            return (time.getTime() < Date.now()) || (time.getTime() > Date.now() + n *24 * 60 * 60 * 1000)
                        }else if(this.pickoption.startsWith('t-')){
                            let n = this.pickOption.replace('t-','')-0
                            return (time.getTime() < Date.now()) || (time.getTime() > Date.now() + n *24 * 60 * 60 * 1000)
                        }
                    }
                }
            }
        },
        mounted() {
            this.currentValue = this.value
        },
        watch: {
            currentValue(v){
                console.log(v)
                if(v){
                    this.$emit("update:name",v);
                    this.$emit("input", v);
                    this.$emit("change", v);
                }else {
                    this.$emit("update:name",'');
                    this.$emit("input", '');
                    this.$emit("change", '');
                }
            }
        },
    }
</script>

