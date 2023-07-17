# 概述
公用AI服务，对接大模型、资料库以及外部AI系统

# EAP的AI服务
## 数据模型服务
- 根据对话生成主要数据模型及关系
- 细化数据模型，自动生成对象/属性(表/字段)
- 工具：根据自然语言模型生成英文对象名/属性名

## 国际化翻译服务
- UI词条翻译，指定长度限制
- 属性名/字段名转label标签(指定长度)

# 应用场景
## 翻译
主要用于软件UI国际化翻译，前端国际化从后端获取     
后端判断需要国际化资源后自动填充词条并通过AI服务自动翻译

## 数据模型
低代码建模，集成OBPM