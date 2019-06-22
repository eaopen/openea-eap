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