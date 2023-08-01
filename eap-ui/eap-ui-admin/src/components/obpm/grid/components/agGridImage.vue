
    <template>
        <div style="text-align: center;">
            <img :src="imgSrc" :width="width" :height="height" v-if="isShow"/>
            <div v-else :style="{width:width+'px', height:height+'px'}"> 图片读取失败 </div>
        </div>
    </template>
    <script>
    export default {
        name: 'BasicRender',
        data() {
            return {
                imgSrc: "",
                width: "",
                height: "",
                isShow: ""
            };
        },
        methods: {
            handleClick() {
                if(!this.link) return
            },
        },
        created() {
            let data = this.params.data
            let imgSrc = this.params.colDef.cellRendererParams.imgSrc.replace(/\${/g, '${data.')
            this.imgSrc = eval('`' + imgSrc + '`')
            this.width = this.params.colDef.cellRendererParams.width || 150
            this.height = this.params.colDef.cellRendererParams.height || 100
            let isShow = true
            if(this.params.colDef.cellRendererParams.isShow) {
                isShow = this.params.colDef.cellRendererParams.isShow.replace(/\${/g, 'data.').replace(/\}/g, '')
            }
            this.isShow = eval(isShow)
        }
    }    
</script>
