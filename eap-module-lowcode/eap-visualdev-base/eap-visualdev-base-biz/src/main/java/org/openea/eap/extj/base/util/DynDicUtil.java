package org.openea.eap.extj.base.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.util.CacheKeyUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.RedisUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.context.SpringContext;
import org.openea.eap.extj.base.service.DataInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**

 */
@Component
public class DynDicUtil {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DataInterfaceService dataInterfaceService;


    public final String regEx = "[\\[\\]\"]";

    /**
     * 获取数据字典数据
     *
     * @param feild
     * @return
     */
    public String getDicName(String feild,String dictionaryTypeId) {
        if (redisUtil.exists(cacheKeyUtil.getDictionary() + feild)) {
            return redisUtil.getString(cacheKeyUtil.getDictionary() + feild).toString();
        }
        if (StringUtil.isNotEmpty(feild)) {
            //去除中括号以及双引号
            feild = feild.replaceAll(regEx, "");
            //判断多选框
            String[] feilds = feild.split(",");
            if (feilds.length > 1) {
                StringBuilder feildsValue = new StringBuilder();
                DictionaryDataEntity dictionaryDataEntity;
                for (String feil : feilds) {
                    dictionaryDataEntity = dictionaryDataService.getSwapInfo(feil,dictionaryTypeId);
                    if (dictionaryDataEntity != null) {
                        feildsValue.append(dictionaryDataEntity.getFullName() + ",");
                    }
                }
                String finalValue;
                if (StringUtil.isEmpty(feildsValue) || feildsValue.equals("")) {
                    finalValue = feildsValue.toString();
                } else {
                    finalValue = feildsValue.substring(0, feildsValue.length() - 1);
                }
                redisUtil = SpringContext.getBean(RedisUtil.class);
                redisUtil.insert(cacheKeyUtil.getDictionary() + feild, finalValue, 20);
                return finalValue;
            }
            DictionaryDataEntity dictionaryDataentity = dictionaryDataService.getSwapInfo(feild,dictionaryTypeId);
            if (dictionaryDataentity != null) {
                redisUtil = SpringContext.getBean(RedisUtil.class);
                redisUtil.insert(cacheKeyUtil.getDictionary() + feild, dictionaryDataentity.getFullName(), 20);
                return dictionaryDataentity.getFullName();
            }
            return feild;
        }
        return feild;
    }

    /**
     * 获取远端数据
     *
     * @param urlId
     * @param label
     * @param value
     * @param feildValue
     * @return
     * @throws IOException
     */
    public String getDynName(String urlId, String label, String value, String feildValue) {
        String rediskey = cacheKeyUtil.getDynamic() + "_" + urlId + "_" + feildValue;
        if (redisUtil.exists(rediskey)) {
            return redisUtil.getString(rediskey).toString();
        }
        if (StringUtil.isNotEmpty(feildValue)) {
            //去除中括号以及双引号
            feildValue = feildValue.replaceAll(regEx, "");
            //获取远端数据
            ActionResult object = dataInterfaceService.infoToId(urlId);
            if (object.getData() != null && object.getData() instanceof List) {
//                DataInterfaceActionVo vo= (DataInterfaceActionVo) object.getData();
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) object.getData();
                //判断是否多选
                String[] feildValues = feildValue.split(",");
                if (feildValues.length > 0) {
                    //转换的真实值
                    StringBuilder feildVa = new StringBuilder();
                    for (String feild : feildValues) {
                        for (Map<String, Object> data : dataList) {
                            if (String.valueOf(data.get(value)).equals(feild)) {
                                feildVa.append(data.get(label) + ",");
                            }
                        }
                    }
                    String finalValue;
                    if (StringUtil.isEmpty(feildVa) || feildVa.equals("")) {
                        finalValue = feildVa.toString();
                    } else {
                        finalValue = feildVa.substring(0, feildVa.length() - 1);
                    }
                    redisUtil = SpringContext.getBean(RedisUtil.class);
                    redisUtil.insert(rediskey, finalValue, 20);
                    return finalValue;
                }
                for (Map<String, Object> data : dataList) {
                    if (feildValue.equals(String.valueOf(data.get(value)))) {
                        redisUtil = SpringContext.getBean(RedisUtil.class);
                        redisUtil.insert(rediskey, data.get(label).toString(), 20);
                        return data.get(label).toString();
                    }
                    return feildValue;
                }
            }
            return feildValue;
        }
        return feildValue;
    }

    /**
     * 获取远端数据
     *
     * @param urlId
     * @param name
     * @param id
     * @param children
     * @param feildValue
     * @return
     */
    public String getDynName(String urlId, String name, String id, String children, String feildValue) {
        String rediskey = cacheKeyUtil.getDynamic() + "_" + urlId + "_" + feildValue;
        if (redisUtil.exists(rediskey)) {
            return redisUtil.getString(rediskey).toString();
        }
        List<String> result = new ArrayList<>();
        if (StringUtil.isNotEmpty(feildValue)) {
            ActionResult object = dataInterfaceService.infoToId(urlId);
//            DataInterfaceActionVo actionVo = (DataInterfaceActionVo) object.getData();
            List<Map<String, Object>> dataList = new ArrayList<>();
            if (object.getData() instanceof List) {
                dataList = (List<Map<String, Object>>) object.getData();
            }
            JSONArray dataAll = JsonUtil.getListToJsonArray(dataList);
            List<Map<String, String>> list = new ArrayList<>();
            treeToList(id, name, children, dataAll, list);
            String value = feildValue.replaceAll("\\[", "").replaceAll("\\]", "");
            result = list.stream().filter(t -> value.contains(String.valueOf(t.get(id)))).map(t -> String.valueOf(t.get(name))).collect(Collectors.toList());
        }
        return String.join(",", result);
    }


    /**
     * 树转成list
     **/
    private void treeToList(String id, String fullName, String children, JSONArray data, List<Map<String, String>> result) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject ob = data.getJSONObject(i);
            Map<String, String> tree = new HashMap<>(16);
            tree.put(id, String.valueOf(ob.get(id)));
            tree.put(fullName, String.valueOf(ob.get(fullName)));
            result.add(tree);
            if (ob.get(children) != null) {
                JSONArray childArray = ob.getJSONArray(children);
                treeToList(id, fullName, children, childArray, result);
            }
        }
    }

}
