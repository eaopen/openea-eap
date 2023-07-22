import request from "@/utils/request";

// 获取form配置
export function getFormConfig(params) {
    return request({
        url: '/form/formDefData/getData',
        method: 'post',
        params
    })
}

// 自定义对话框
export function getCustomObject(key){
    return request({
        url: '/form/formCustDialog/getObject?key='+key,
        method: 'post',
    })
}