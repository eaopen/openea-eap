<template>
    <span>
        <sparePart :num="num" :name="name" @returnnum="receivenum" ref="sparePart"></sparePart>
        <oilPumpPart :num="num" :name="name" @returnnum="receivenum" ref="oilPumpPart"></oilPumpPart>
    </span>
</template>
<script>
import sparePart from "./sparePart.vue"
import oilPumpPart from "./oilPumpPart.vue"
export default {
    name: 'componentCreater', // 部套名称生成器
    components:{ sparePart,oilPumpPart },
    props: ['ctype', 'num','name'],
    data(){
        return {
            list: [
                {
                    name: 'sparePart',
                    type: 10
                },
                {
                    name: 'oilPumpPart',
                    type: 13
                },
            ]
        }
    },
    created(){

    },
    methods:{
        receivenum(data){
            this.$emit('sendnum', data)
        },
        showDialog(){
            setTimeout(()=>{
                let item = this.list.find(i=>i.type == this.ctype)
                console.log(this.ctype)
                console.log(item)
                if(item){
                    this.$refs[item.name].startCreateNum()
                }
            },20)
        }
    }
}
</script>