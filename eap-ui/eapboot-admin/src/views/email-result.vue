<template>
    <Row type="flex" justify="center" align="middle" class="regist" @keydown.enter="submitRegist">
        <Col :xs="{span:22}" style="width: 368px;">
        <Row class="header">
            <img src="../assets/xboot.png" width="220px" />
            <div class="description">EapBoot企业应用平台</div>
        </Row>
    
        <Row class="success">
            <div v-if="result=='1'">
                <Icon type="md-checkmark-circle" color="#52c41a" size="64"></Icon>
                <p class="success-words">恭喜您，修改邮箱成功</p>
            </div>
            <div v-else>
                <Icon type="md-close-circle" color="#f55b33" size="64"></Icon>
                <p class="success-words">无效的token，链接已过期</p>
            </div>
            <Row class="buttons">
                <Button @click="goBack" icon="md-arrow-round-back" size="large" style="margin-right:10px;">返回系统</Button>
            </Row>
        </Row>
    
        <Row class="foot">
            <Row type="flex" justify="space-around" class="code-row-bg help">
                <a class="item">帮助</a>
                <a class="item">隐私</a>
                <a class="item">条款</a>
            </Row>
            <Row type="flex" justify="center" class="code-row-bg copyright">
                Copyright © 2018 <a href="http://exrick.cn" target="_blank" style="margin:0 5px;">Exrick</a> Presents
            </Row>
        </Row>
        </Col>
    </Row>
</template>

<script>
import { userInfo } from "@/api/index";
import Cookies from "js-cookie";
export default {
  data() {
    return {
      result: ""
    };
  },
  methods: {
    goBack() {
      if (this.result == "1") {
        // 更新用户信息
        userInfo().then(res => {
          if (res.success) {
            // 避免超过大小限制
            delete res.result.permissions;
            if (this.getStore("saveLogin")) {
              // 保存7天
              Cookies.set("userInfo", JSON.stringify(res.result), {
                expires: 7
              });
            } else {
              Cookies.set("userInfo", JSON.stringify(res.result));
            }
            this.setStore("userInfo", res.result);
            this.$router.push({
              name: "ownspace_index"
            });
          }
        });
      } else {
        this.$router.push({
          name: "ownspace_index"
        });
      }
    }
  },
  mounted() {
    this.result = this.$route.query.result;
  }
};
</script>

<style lang="less">
@import "./email-result.less";
</style>
