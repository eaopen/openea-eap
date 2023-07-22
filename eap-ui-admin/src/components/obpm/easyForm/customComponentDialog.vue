<template>
    <el-dialog
      :title="title"
      :visible="visible"
      :width="width"
      :height="height || '400px'">
      <div>
        <component v-if="showComponent" :is="componentName" :params="params"></component>
        <div v-else>11</div>
      </div>
    </el-dialog>
</template>
<script>
export default {
    name: 'customComponentDialog',
    props: ['title', 'visible', 'width', 'height', 'componentName', 'innerComponent', 'params'],
    data(){
        return {
            showComponent: false,
        }
    },
    created(){

    },
    methods:{
        
    }
}
</script>

