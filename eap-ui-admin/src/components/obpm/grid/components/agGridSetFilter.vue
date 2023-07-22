
    <template>
        <div>
            <Checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</Checkbox>
            <div style="margin: 15px 0;"></div>
            <Checkbox-group v-model="value" @change="handleCheckedCitiesChange">
                <el-checkbox v-for="item in mapList" :label="item.label" :key="item.value">{{item.label}}</el-checkbox>
            </Checkbox-group>
        </div>
    </template>
    <script>
    import Vue from 'vue'
    import {Checkbox, CheckboxGroup} from 'element-ui'
    Vue.use(Checkbox)
    Vue.use(CheckboxGroup)
    export default {
        name: 'setFilter',
        data() {
            return {
                checkAll: false,
                isIndeterminate: false,
                value: []
            };
        },
        computed:{
            mapList(){
                return this.params.colDef.cellRendererParams.list
            }
        },
        methods: {
            handleCheckAllChange(){},
            getModel(){
                console.log(this.params)
            },
            handleCheckedCitiesChange(){}

        },
        mounted() {
            console.log(this.params)
            console.log(this.mapList)
        }
    }    
</script>
