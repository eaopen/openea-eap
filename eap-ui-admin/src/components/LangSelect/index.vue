<template>
  <el-dropdown trigger="click" class="international" @command="handleSetLanguage">
    <div>
      <svg-icon class-name="international-icon" icon-class="language" />
    </div>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item :disabled="language==='en'" command="en">
        English
      </el-dropdown-item>
      <el-dropdown-item :disabled="language==='zh'" command="zh">
        中文
      </el-dropdown-item>
<!--      <el-dropdown-item :disabled="language==='zhtw'" command="zhtw">-->
<!--        繁体中文-->
<!--      </el-dropdown-item>-->
<!--      <el-dropdown-item :disabled="language==='es'" command="es">-->
<!--        Español-->
<!--      </el-dropdown-item>-->
      <el-dropdown-item :disabled="language==='ja'" command="ja">
        日本語
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
import { UpdateLanguage } from '@/api/system/user'
import getPageTitle from '@/utils/get-page-title'
export default {
  computed: {
    language() {
      return this.$store.getters.language
    }
  },
  methods: {
    handleSetLanguage(lang) {
      //UpdateLanguage({ language: lang }).then(res => { })
      this.$i18n.locale = lang
      this.$store.dispatch('app/setLanguage', lang)
      let text = 'Switch Language Success'
      if (lang === 'zh') text = '切换成功'
      if (lang === 'zhtw') text = '切換成功'
      if (lang === 'ja') text = '言語切替成功'
      document.title = getPageTitle(this.$route.meta.title, this.$route.meta.zhTitle)
      this.$message({
        message: text,
        type: 'success'
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.international {
  .icon-ym-header-language {
    line-height: 63px;
  }
}
</style>
