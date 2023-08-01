
    <template>
        <div>
            <i :class="'ri-' + icon" style="position: absolute; top: 1px; margin-right: 10px;" v-if="icon" ></i>
            <span @click="handleClick" :class="{'ag-link': link}">{{name}}</span>
        </div>
    </template>
    <script>
    export default {
        name: 'BasicRender',
        data() {
            return {
                icon: "",
                link: "",
                name: ""
            };
        },
        methods: {
            handleClick() {
                if(!this.link) return
                console.log(this.link)
            },
            initCell(params) {
                this.name = params.valueFormatted || params.value
                if(params.conditionList && params.conditionList.length){
                    // 条件
                    params.conditionList.forEach(item=>{
                        if(eval(item.condition)){
                            this.icon = item.icon
                            this.link = item.link
                        }
                    })
                }else {
                    this.icon = params.colDef.cellRendererParams.icon
                    this.link = params.colDef.cellRendererParams.link
                }

            },
        },
        created() {
            this.initCell(this.params);
        }
    }
</script>
