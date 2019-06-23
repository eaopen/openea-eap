# Eap Admin iview

based on iview admin 2.5

# iView Admin

## Install
```bush
// install dependencies
npm install
```
## Run
### Development
```bush
npm run dev
```
### Production(Build)
```bush
npm run build
```

## 加入QQ群，获取最新更新咨询，和3000+开发者交流学习

![image](https://github.com/iview/iview-admin/raw/2.0/src/assets/images/groups.jpg)

## 简介
&emsp;&emsp;iView admin是基于Vue.js，搭配使用[iView](https://www.iviewui.com) UI组件库形成的一套后台集成解决方案，由TalkingData前端可视化团队部分成员开发维护。iView admin遵守iView设计和开发约定，风格统一，设计考究，并且更多功能在不停开发中。
如果您想查看iview-admin的更新动态，您可以到[更新日志](https://github.com/iview/iview-admin/releases)查看了解最新更新；如果您是新手，想快速入手iview-admin，您可以到[使用教程](https://github.com/iview/iview-admin/wiki)查看讲解；如果您想在线体验iview-admin，您可以到[在线访问](https://admin.iviewui.com/)体验。如果你只是想要一个清醒爽朗的界面，那你可以下载[简化版模板](https://github.com/iview/iview-admin/tree/template)来做开发。

## 功能

- 登录/登出
- 权限管理
    - 列表过滤
    - 权限切换
- 多语言切换
- 组件
    - 富文本编辑器
    - Markdown编辑器
    - 城市级联
    - 图片预览编辑
    - 可拖拽列表
    - 文件上传
    - 数字渐变
    - split-pane
- 表单编辑
    - 文章发布
    - 工作流
- 表格
    - 可拖拽排序
    - 可编辑表格
        - 行内编辑
        - 单元格编辑
    - 可搜索表格
    - 表格导出数据
        - 导出为Csv文件
        - 导出为Xls文件
    - 表格转图片
- 错误页面
    - 403页面
    - 404页面
    - 500页面
- 高级路由
    - 动态路由
    - 带参页面
- 换肤
- 收缩侧边栏
- tag标签导航
- 面包屑导航
- 全屏/退出全屏
- 锁屏
- 消息中心
- 个人中心

## 文件结构
```shell
.
├── build  项目构建配置
├── config  开发相关配置
├── public  打包所需静态资源
└── src
    ├── api  AJAX请求
    └── assets  项目静态资源
        ├── icons  自定义图标资源
        └── images  图片资源
    ├── components  业务组件
    ├── config  项目运行配置
    ├── directive  自定义指令
    ├── libs  封装工具函数
    ├── locale  多语言文件
    ├── mock  mock模拟数据
    ├── router  路由配置
    ├── store  Vuex配置
    ├── view  页面文件
    └── tests  测试相关
```
