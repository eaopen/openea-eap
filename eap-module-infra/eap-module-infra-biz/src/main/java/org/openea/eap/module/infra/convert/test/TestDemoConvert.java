package org.openea.eap.module.infra.convert.test;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.infra.controller.admin.test.vo.TestDemoCreateReqVO;
import org.openea.eap.module.infra.controller.admin.test.vo.TestDemoExcelVO;
import org.openea.eap.module.infra.controller.admin.test.vo.TestDemoRespVO;
import org.openea.eap.module.infra.controller.admin.test.vo.TestDemoUpdateReqVO;
import org.openea.eap.module.infra.dal.dataobject.test.TestDemoDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 字典类型 Convert
 *
 */
@Mapper
public interface TestDemoConvert {

    TestDemoConvert INSTANCE = Mappers.getMapper(TestDemoConvert.class);

    TestDemoDO convert(TestDemoCreateReqVO bean);

    TestDemoDO convert(TestDemoUpdateReqVO bean);

    TestDemoRespVO convert(TestDemoDO bean);

    List<TestDemoRespVO> convertList(List<TestDemoDO> list);

    PageResult<TestDemoRespVO> convertPage(PageResult<TestDemoDO> page);

    List<TestDemoExcelVO> convertList02(List<TestDemoDO> list);

}
