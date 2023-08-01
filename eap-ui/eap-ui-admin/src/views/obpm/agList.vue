<template>
  <div>
    <List-ag-grid :code="tableKey" :params="tableParams" v-if="showList"/>
  </div>
</template>

<script>
import RightPanel from '@/components/RightPanel'
import { mapState } from 'vuex'
import variables from '@/assets/styles/variables.scss'
// const ListAgGrid = ()=> import("@/components/agGrid/index.vue")

export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
    RightPanel,
    Settings,
    Sidebar,
    TagsView,
    // ListAgGrid
  },
  mixins: [ResizeMixin],
  data(){
    return {
      tableKey: '',
      showList: true,
      tableParams: {
        initParams: {},
        initFilters: {}
      }
    }
  },
  watch:{
    '$route'(v){
      if(v.fullPath.startsWith('/listGrid/')){
        this.tableKey = v.path.split('listGrid/')[1]

      }
    }
  },
  created(){
    this.tableKey = this.$route.path.split('/listGrid/')[1]
    console.log(this.$route)
    this.reloadGrid()
  },
  computed: {
    ...mapState({
      theme: state => state.settings.theme,
      sideTheme: state => state.settings.sideTheme,
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader
    }),
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    variables() {
      return variables;
    }
  },
  methods: {
    reloadGrid(){
      this.showList = false
      setTimeout(() => {
        this.showList = true
      }, 10);
    },
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "~@/assets/styles/mixin.scss";
@import "~@/assets/styles/variables.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$base-sidebar-width});
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.sidebarHide .fixed-header {
  width: calc(100%);
}

.mobile .fixed-header {
  width: 100%;
}
</style>
