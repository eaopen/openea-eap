<style lang="less">
</style>
<template>
  <div>
    <Row>
      <Col>
        <Card>     
          <Tabs value="1">
            <TabPane label="慕课类" name="1">
              <Alert type="warning" show-icon>说明：基于Vue组件化开发，该组件付费获取后无需授权，请自行修改UI避免侵权！</Alert>
              <span style="margin-right:10px;">开启背景随图片模糊切换：</span>
              <i-switch size="large" v-model="isOpen">
                <span slot="open">开启</span>
                <span slot="close">关闭</span>
              </i-switch>
              <BannerMooc :bk="isOpen"/>
            </TabPane>
          </Tabs>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import BannerMooc from "./mooc/BannerMooc.vue";
import BannerShop from "./shop/BannerShop.vue";
export default {
  name: "banner",
  components: {
    BannerMooc
  },
  data() {
    return {
      isOpen: false
    };
  },
  methods: {
    init() {}
  },
  mounted() {
    this.init();
  }
};
</script>