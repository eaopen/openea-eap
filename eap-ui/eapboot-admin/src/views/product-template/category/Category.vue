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
              <CategoryMooc/>
            </TabPane>
            <TabPane label="电商类" name="2">
              待开发，如果需要尽早购买，购买后持续免费更新，避免涨价
              <br>
              <br>
              <Button to="http://xpay.exrick.cn/pay?xboot" target="_blank" type="error" icon="md-paper-plane">立即获取</Button>
            </TabPane>
          </Tabs>
              
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import CategoryMooc from "./mooc/CategoryMooc.vue";
export default {
  name: "category",
  components: {
    CategoryMooc
  },
  data() {
    return {
      
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