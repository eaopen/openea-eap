
<template>
  <div>
    <span @click="handleClick(b)" class="right-link" style="padding-right: 14px" v-for="b in buttons">{{ b.name }}</span>
  </div>
</template>
<script>
import easyForm from "@/components/obpm/form/easyForm/dialogForm.vue"
import {formatString ,getParams} from '@/utils/obpm'
export default {
  name: 'agGridOprations',
  data() {
    return {
      buttons: []
    };
  },
  methods: {
    handleClick(btn) {
      console.log(btn)
      if (btn.warnTxt) {
        this.$confirm(btn.warnTxt, "注意").then(() => {
          this.hasFilters(btn, [this.params.data])
        }).catch(() => {
          console.log('cancel')
        })
      } else {
        this.hasFilters(btn, [this.params.data])
      }
    },
    // 判定是否过滤
    hasFilters(btn, selects) {
      // 过滤条件样子
      let filters = [
        // {
        //   condition: "{statusid}!= 3",
        //   warning: "测试不能操作非执行中的项目"
        // },
        // {
        //   condition: "{country}!='中国 && {dutyid}==5'",
        //   warning: "测试不能操作非中国且dutyid==5的项目"
        // }
      ]
      let flag = true, warning = ""
      let data = selects[0]
      if (filters && filters.length) {
        for (let i = 0; i < filters.length; i++) {
          let str = filters[i].condition.replace(/{/g, 'data.').replace(/}/, '')
          if (eval(str)) {
            flag = false
            warning = filters[i].warning
            break
          }
        }
      }
      if (!flag) {
        this.$alert(warning, "注意")
      } else {
        this.clickAction(btn, selects)
      }
    },
    // 确定生效
    clickAction(btn, selects) {
      // 0 是直接请求
      // 1 是方法按钮
      // 2 是选项卡
      // 3，4 是弹窗
      // if(){

      // }
      if (btn.clickType === '3' || btn.clickType === '4') {
        const { hrefSetting } = JSON.parse(btn.expand)
        if (btn.url.startsWith('/form/formDef/vueFormDefPreview.html')) {
          const component = easyForm
          let newUrl = formatString(btn.url, selects)
          let params = getParams(newUrl)
          params.async = true
          this.$store.dispatch('dialog/showd1', {
            component,
            width: hrefSetting.width + hrefSetting.widthUnit,
            height: hrefSetting.height + hrefSetting.heightUnit,
            title: btn.name,
            params
          })
        }
      }
    }
  },
  created() {
    this.buttons = this.params.colDef.cellRendererParams
  }
}
</script>
<style lang="scss" scoped>
.right-link {
  color: #0958d9;
  cursor: pointer;

  &:hover {
    opacity: 0.6;
  }
}
</style>

