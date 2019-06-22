<style scoped>
.expand-row {
  margin-bottom: 16px;
}
</style>
<template>
    <div>
        <Row class="expand-row">
            <Col span="6">
                <span>注册邮箱: </span>
                <span>{{ row.email }}</span>
            </Col>
            <Col span="6">
                <span>生日: </span>
                <span>{{ row.birth }}</span>
            </Col>
            <Col span="6">
                <span>手机号: </span>
                <span>{{ row.mobile }}</span>
            </Col>
            <Col span="6">
                <span>个人网页: </span>
                <a :href="row.website" target="_blank">{{ row.website }}</a>
            </Col>
        </Row>
        <Row>
            <Col span="6">
                <span>GitHub: </span>
                 <a :href="row.github" target="_blank">{{ row.github }}</a>
            </Col>
        </Row>
    </div>
</template>
<script>
export default {
  name: "expandRow",
  props: {
    row: Object
  }
};
</script>