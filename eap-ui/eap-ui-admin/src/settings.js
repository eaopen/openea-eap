module.exports = {

  /**
   * 侧边栏主题 深色主题theme-dark，浅色主题theme-light
   */
  sideTheme: 'theme-dark',

  /**
   * 是否系统布局配置
   */
  showSettings: false,

  /**
   * 是否显示顶部导航
   */
  topNav: true,

  /**
   * 是否显示 tagsView
   */
  tagsView: true,

  /**
   * 是否固定头部
   */
  fixedHeader: false,

  /**
   * 是否显示logo
   */
  sidebarLogo: true,

  /**
   * 是否显示动态标题
   */
  dynamicTitle: false,
  /**
   * 是否缓存选项卡
   */
  isTopNavCache: true,
  /**
   * 选项卡缓存时间
   */
  topNavCacheTime: 3*24*3600*1000,
  /**
   * 列表配置缓存时间
   */
  listCacheTime: 3*24*3600*1000,
  /**
   * 列表行编辑缓存时间
   */
  editCacheTime: 1*24*3600*1000,


  /**
   * 是否显示语言
   */
  showLanguage: true,

  /**
   * 是否显示搜索
   */
  showSearch: true,

  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description Need show err logs component.
   * The default is only used in the production env
   * If you want to also use it in dev, you can pass ['production', 'development']
   */
  errorLog: 'production'
}
