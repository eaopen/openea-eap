// 统一请求路径前缀在libs/axios.js中修改
import { getRequest, postRequest, putRequest, deleteRequest, importRequest, uploadFileRequest } from '@/libs/axios';



// 导出模型
export const exportModel = "/xboot/actModel/export/"
// 通过文件部署模型流程
export const deployByFile = "/xboot/actModel/deployByFile"
// 导出流程资源
export const exportResource = "/xboot/actProcess/export"
// 获取高亮实时流程图
export const getHighlightImg = "/xboot/actProcess/getHighlightImg/"



// 获取模型
export const getModelDataList = (params) => {
    return getRequest('/actModel/getByCondition', params)
}
// 添加模型
export const addModel = (params) => {
    return postRequest('/actModel/add', params)
}
// 部署模型
export const deployModel = (id, params) => {
    return getRequest(`/actModel/deploy/${id}`, params)
}
// 删除模型
export const deleteModel = (ids, params) => {
    return deleteRequest(`/actModel/delByIds/${ids}`, params)
}



// 获取流程数据
export const getProcessDataList = (params) => {
    return getRequest('/actProcess/getByCondition', params)
}
// 编辑流程信息
export const updateInfo = (params) => {
    return postRequest('/actProcess/updateInfo', params)
}
// 修改流程状态 激活/挂起
export const updateStatus = (params) => {
    return postRequest('/actProcess/updateStatus', params)
}
// 转化流程为模型
export const convertToModel = (id, params) => {
    return getRequest(`/actProcess/convertToModel/${id}`, params)
}
// 节点设置
export const getProcessNode = (id, params) => {
    return getRequest(`/actProcess/getProcessNode/${id}`, params)
}
// 节点用户设置
export const editNodeUser = (params) => {
    return postRequest('/actProcess/editNodeUser', params)
}
// 删除流程定义
export const deleteProcess = (ids, params) => {
    return deleteRequest(`/actProcess/delByIds/${ids}`, params)
}



// 获取流程数据
export const getRunningProcess = (params) => {
    return getRequest('/actProcess/getRunningProcess', params)
}
// 获取结束数据
export const getFinishedProcess = (params) => {
    return getRequest('/actProcess/getFinishedProcess', params)
}
// 通过key获取最新流程
export const getProcessByKey = (key, params) => {
    return getRequest(`/actProcess/getByKey/${key}`, params)
}
// 通过流程定义id获取第一个任务节点审批人
export const getFirstNode = (id, params) => {
    return getRequest(`/actProcess/getFirstNode/${id}`, params)
}
// 通过流程实例id获取下一个任务节点审批人
export const getNextNode = (id, params) => {
    return getRequest(`/actProcess/getNextNode/${id}`, params)
}
// 通过节点id获取审批人
export const getNode = (id, params) => {
    return getRequest(`/actProcess/getNode/${id}`, params)
}
// 修改流程状态 激活/挂起
export const updateInsStatus = (params) => {
    return postRequest('/actProcess/updateInsStatus', params)
}
// 删除流程运行实例
export const deleteProcessIns = (ids, params) => {
    return deleteRequest(`/actProcess/delInsByIds/${ids}`, params)
}
// 删除结束流程实例
export const delHistoricIns = (ids, params) => {
    return deleteRequest(`/actProcess/delHistoricInsByIds/${ids}`, params)
}



// 获取代办列表
export const todoList = (params) => {
    return getRequest('/actTask/todoList', params)
}
// 获取已办列表
export const doneList = (params) => {
    return getRequest('/actTask/doneList', params)
}
// 获取流程流转历史
export const historicFlow = (id, params) => {
    return getRequest(`/actTask/historicFlow/${id}`, params)
}
// 委托他人代办
export const delegate = (params) => {
    return postRequest('/actTask/delegate', params)
}
// 审批任务通过
export const pass = (params) => {
    return postRequest('/actTask/pass', params)
}
// 批量审批任务通过
export const passAll = (ids, params) => {
    return postRequest(`/actTask/passAll/${ids}`, params)
}
// 获取可返回的节点
export const getBackList = (id, params) => {
    return getRequest(`/actTask/getBackList/${id}`, params)
}
// 任务节点审批驳回至发起人
export const back = (params) => {
    return postRequest('/actTask/back', params)
}
// 批量驳回至发起人
export const backAll = (ids, params) => {
    return postRequest(`/actTask/backAll/${ids}`, params)
}
// 任务节点审批驳回至指定历史节点
export const backToTask = (params) => {
    return postRequest('/actTask/backToTask', params)
}
// 删除任务
export const deleteTask = (ids, params) => {
    return deleteRequest(`/actTask/delete/${ids}`, params)
}
// 删除历史任务
export const deleteHistoricTask = (ids, params) => {
    return deleteRequest(`/actTask/deleteHistoric/${ids}`, params)
}



// 获取一级类别
export const initActCategory = (params) => {
    return getRequest('/actCategory/getByParentId/0', params)
}
// 加载类别子级数据
export const loadActCategory = (id, params) => {
    return getRequest(`/actCategory/getByParentId/${id}`, params)
}
// 添加类别
export const addActCategory = (params) => {
    return postRequest('/actCategory/add', params)
}
// 编辑类别
export const editActCategory = (params) => {
    return postRequest('/actCategory/edit', params)
}
// 删除类别
export const deleteActCategory = (ids, params) => {
    return deleteRequest(`/actCategory/delByIds/${ids}`, params)
}
// 搜索类别
export const searchActCategory = (params) => {
    return getRequest('/actCategory/search', params)
}



// 获取申请数据
export const getBusinessDataList = (params) => {
    return getRequest('/actBusiness/getByCondition', params)
}
// 提交申请
export const applyBusiness = (params) => {
    return postRequest('/actBusiness/apply', params)
}
// 直接提交申请
export const startBusiness = (params) => {
    return postRequest('/actBusiness/start', params)
}
// 撤回申请
export const cancelApply = (params) => {
    return postRequest('/actBusiness/cancel', params)
}
// 删除申请
export const deleteBusiness = (ids, params) => {
    return deleteRequest(`/actBusiness/delByIds/${ids}`, params)
}



// 获取单个请假数据
export const getLeaveData = (id, params) => {
    return getRequest(`/leave/get/${id}`, params)
}
// 新增请假数据
export const addLeave = (params) => {
    return postRequest('/leave/add', params)
}
// 修改请假数据
export const updateLeave = (params) => {
    return putRequest('/leave/update', params)
}




