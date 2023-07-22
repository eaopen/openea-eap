<template>
    <el-input v-model="val" :class="{'is-error': err}" :placeholder="ph" size="mini" :disabled="!permission || permission !=='w'"></el-input>
</template>
<script>
export default {
    name: 'multInput',
    props: ['value', 'placeholder',"permission","required"],
    data(){
        return {
            val: '',
            err: false
        }
    },
    mounted(){
        if(this.value) this.val = '' + this.value
    },
    watch:{
        val(v,ov){
            if(this.required){
                if(v){
                    this.err = false
                }else {
                    this.err = true
                }
            }
            this.$emit('update:name', v);
            this.$emit('input', v) 
            this.$emit('change',v)
        }
    },
    computed:{
        ph(){
            if(!this.permission || this.permission !=='w'){
                return ''
            }else if(this.placeholder){
                return this.placeholder
            }else {
                return '请输入'
            }
        }
    }
}
</script>