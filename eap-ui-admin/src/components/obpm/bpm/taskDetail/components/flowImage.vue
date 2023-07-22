<template>
   <div class="image-container">
    <el-popover
      v-for="item in imageInfo"
      :key="item.id"
      placement="top-start"
      width="300"
      trigger="hover">
      <div style="border: 1px #ccc solid; padding: 10px; margin: 5px 0;">
        <div class="operation-box" v-for="o in item.operations" :key="o.id">
          <div class="node-name" style="font-size: 16px; font-weight: bold">{{o.approverName}}<span :style="{color: item.color}"><i :class="item.icon" style="margin:0 5px"></i>{{ item.name }}</span></div>
          <div class="node-content" v-if="o.status=='start'">
            <p><span>创建时间：</span> {{ o.createTime }}</p>
          </div>
          <div class="node-content" v-else-if="!!o.approverName">
            <p><span>审批意见：</span> {{ o.opinion }}</p>
            <p><span>操作时间：</span> {{ o.approveTime }}</p>
            <p><span>任务耗时：</span> {{ o.durMs }}</p>
            <p><span>预期完成时间：</span> {{ o.planCompleteDay}}</p>
          </div>
          <div class="node-content" v-else-if="!o.approverName">
            <p><span>任务候选人：</span> {{ o.assignInfo.split('-')[1] }}</p>
            <p><span>创建时间：</span> {{ o.createTime }}</p>
            <p><span>预期完成时间：</span> {{ o.planCompleteDay }}</p>
          </div>
        </div>

      </div>
      <div class="node-hover" :style="{'left': item.x + 'px', 'top': item.y + 'px', 'width': item.width  + 'px', 'height': item.height  + 'px', 'color': item.color}" slot="reference">
        <div class="node-name"><i :class="item.icon"></i>{{ item.name }}</div>
      </div>
    </el-popover>
    <img class="image-bg" :src="imageSrc"/>
   </div>
</template>
<script>

export default {
  name: "flow-image",
  props: ["imageSrc", "imageInfo"],
  data(){
    return {
      list: []
    }
  },
  methods:{

  },
  created(){
    console.log(this.imageInfo)
  }
}
</script>
<style lang="scss" scoped>
  .image-container {
    position: relative;
    .image-bg {

    }
    .node-hover {
      position: absolute;
      z-index: 1;
      cursor: pointer;

    }
    .node-name {
        line-height: 28px;
        padding-left: 24px;
    }

  }
</style>
<style lang="scss">
  .node-content {
      font-size: 12px;
      line-height: 20px;
      margin-top: 5px;
      p {
        margin: 0;
        padding: 0;
        span {
          color: #191919;
        }
      }
    }
</style>


