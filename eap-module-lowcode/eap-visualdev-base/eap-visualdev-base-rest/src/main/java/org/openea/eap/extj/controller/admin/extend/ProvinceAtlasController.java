package org.openea.eap.extj.controller.admin.extend;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.base.Joiner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.wxutil.HttpUtil;
import org.openea.eap.extj.extend.entity.ProvinceAtlasEntity;
import org.openea.eap.extj.extend.model.province.AtlasJsonModel;
import org.openea.eap.extj.extend.model.province.ProvinceListTreeVO;
import org.openea.eap.extj.extend.service.ProvinceAtlasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 行政区划-地图
 *
 */
@Tag(name = "行政区划-地图", description = "atlas")
@RestController
@RequestMapping("/api/system/atlas")
public class ProvinceAtlasController extends SuperController<ProvinceAtlasService, ProvinceAtlasEntity> {

    @Autowired
    private ProvinceAtlasService provinceAtlasService;
    public static final String ATLAS_URL = "https://geo.datav.aliyun.com/areas_v3/bound/geojson?code=";

    //树形递归
    private static boolean addChild(ProvinceListTreeVO node, List<ProvinceListTreeVO> list) {
        for (int i = 0; i < list.size(); i++) {
            ProvinceListTreeVO n = list.get(i);
            if (n.getId().equals(node.getParentId())) {
                if (n.getChildren() == null) {
                    n.setChildren(new ArrayList<>());
                }
                List<ProvinceListTreeVO> children = n.getChildren();
                children.add(node);
                n.setChildren(children);
                return true;
            }
            if (n.getChildren() != null && n.getChildren().size() > 0) {
                List<ProvinceListTreeVO> children = n.getChildren();
                if (addChild(node, children)) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * 获取所有列表
     *
     * @return ignore
     */
    @Operation(summary = "获取所有列表")
    @GetMapping
    public ActionResult<List<ProvinceListTreeVO>> list() {
        List<ProvinceAtlasEntity> list = provinceAtlasService.getList();
        List<ProvinceListTreeVO> listVOS = JsonUtil.getJsonToList(list, ProvinceListTreeVO.class);
        listVOS.forEach(item -> {
            if( StringUtil.isNotEmpty(item.getAtlasCenter())){
                String[] split = item.getAtlasCenter().split(",");
                item.setCenterLong(new BigDecimal(split[0]));
                item.setCenterLat(new BigDecimal(split[1]));
            }
        });
        for (int i = 0; i < listVOS.size(); i++) {
            ProvinceListTreeVO item = listVOS.get(i);
            if (!StringUtil.isEmpty(item.getParentId())) {
                if (addChild(item, listVOS) && listVOS.size() > 0) {
                    listVOS.remove(item);
                    i--;
                }
            }
        }
        return ActionResult.success(listVOS);
    }

    /**
     * 获取列表
     *
     * @param pid 主键
     * @return
     */
    @Operation(summary = "获取列表")
    @Parameters({
            @Parameter(name = "pid", description = "主键", required = true)
    })
    @GetMapping("/list/{pid}")
    public ActionResult<List<ProvinceListTreeVO>> getListByPid(@PathVariable("pid") String pid) {
        List<ProvinceAtlasEntity> list = provinceAtlasService.getListByPid(pid);
        List<ProvinceListTreeVO> listVOS = JsonUtil.getJsonToList(list, ProvinceListTreeVO.class);
        return ActionResult.success(listVOS);
    }

    /**
     * 获取地图json
     *
     * @param code 编码
     * @return
     */
    @Operation(summary = "获取地图json")
    @Parameters({
            @Parameter(name = "code", description = "编码", required = true)
    })
    @GetMapping("/geojson")
    public ActionResult<JSONObject> geojson(@RequestParam("code") String code) {
        List<ProvinceAtlasEntity> listByPid = provinceAtlasService.getListByPid(code);
        String url = ATLAS_URL + code;
        if (CollectionUtils.isNotEmpty(listByPid)) {
            url += "_full";
        }
        JSONObject rstObj;
        try {
            rstObj = HttpUtil.httpRequest(url, "GET", null);
        } catch (Exception e) {
            return ActionResult.fail("请求发生错误！");
        }
        if (rstObj == null) {
            return ActionResult.fail("获取不到数据！");
        }
        return ActionResult.success(rstObj);
    }

    /**
     * 同步行政区划信息
     *
     * @return
     */
    @Operation(summary = "同步行政区划信息")
    @GetMapping("/crePy")
    public ActionResult crePy() {
        List<ProvinceAtlasEntity> list = provinceAtlasService.list();
        for (ProvinceAtlasEntity p : list) {

            String url = ATLAS_URL + p.getId();
            JSONObject rstObj;
            try {
                rstObj = HttpUtil.httpRequest(url, "GET", null);
                if (rstObj == null) {
                    provinceAtlasService.removeById(p);
                } else {
                    //将获取到的信息写入表
                    AtlasJsonModel jsonToBean = JsonUtil.getJsonToBean(rstObj, AtlasJsonModel.class);
                    List<BigDecimal> center = jsonToBean.getFeatures().get(0).getProperties().getCenter();
                    p.setAtlasCenter(Joiner.on(",").join(center));
                    //生成拼音
//                    String getpy = getpy(p.getFullName());
//                    p.setQuickQuery(getpy);
                    provinceAtlasService.updateById(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ActionResult.fail("请求发生错误！");
            }
        }
        return ActionResult.success();
    }

    private String getpy(String name) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        StringBuilder result = new StringBuilder();
        char[] charArray = name.toCharArray();
        for (char c : charArray) {
            try {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, outputFormat);
                result.append(pinyinArray[0]);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }
}
