// 获取操作类型
import request from "@/utils/request";

export function getOperationTypeEnum(){
    return request({
        url: '/sys/tools/getEnum',
        method: 'post',
        params:{
            path: 'com.sec.etech.bpm.constant.EtechOpinionStatus',
            listMode: false
        }
    })
}



// 数据字典
export function getDictData(key){
    return request({
        url: '/sys/dataDict/getDictData?dictKey='+key,
        method: 'get',
    })
}
