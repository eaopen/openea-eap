<template>
    <div class="ab-remark">
        <div class="remark-header" v-if="(permission=='b'||permission=='w') && !disabled">
            <input style="width: calc(100% - 70px)" class="el-input__inner" type="text" v-model="inputValue" @keyup.enter="enterSubmit" placeholder="输入完成后按回车新增">
            <span class="btn btn-success ml5" @click="enterSubmit">新增</span>
        </div>
        <ul class="remark-list">
            <li v-for="(r, i) in list" :key="i">
                <span class="remark-text">
                    {{r[itemkey]}}
                </span>
                <span class="remark-del">
                    <el-popover
                    v-if="(permission=='b'||permission=='w') && !disabled"
                    placement="top"
                    width="160"
                    v-model="r.visible">
                    <p>确认删除吗？</p>
                    <div style="text-align: right; margin: 0">
                        <el-button size="mini" type="text" @click="r.visible = false">取消</el-button>
                        <el-button type="primary" size="mini" @click="r.visible = false;list.splice(i, 1);">确定</el-button>
                    </div>
                    <i class="el-icon-close" slot="reference"></i> 
                    </el-popover>
                    
                </span>
            </li>
        </ul>
    </div>
</template>

<script>
    import Vue from 'vue'

    export default {
        name: 'selectAsync',
        props: ['listbase','item', 'itemkey','permission','disabled'],
        data: function () {
            return {
                inputValue: '',
                list: []
            }
        },
        watch:{
            currentName: function () {
                this.$emit('update:search', this.currentName); //搜索框输入的值
            }
        },
        created(){
            let list = this.listbase?this.listbase:[]
            if(list.length){
                list.forEach(i=>{
                    this.$set(i,'visible',false)
                })
            }
            this.list = list
            console.log(this.list)
        },
        methods: {
            del(i){
                this.list.splice(i, 1)
            },
            enterSubmit(){
                if(!this.inputValue) return
                let cache = JSON.parse(JSON.stringify(this.item))
                cache[this.itemkey] = this.inputValue
                cache.visible = false
                this.list.push(cache)
                this.inputValue = ''
                this.$emit('updatelist', this.list)
            }
        },
    }
</script>

