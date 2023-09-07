package org.openea.eap.extj.generater.factory;

import org.openea.eap.extj.generater.util.common.CodeGenerateUtil;
import org.openea.eap.extj.generater.model.TemplateMethodEnum;
import org.openea.eap.extj.generater.util.functionForm.FlowFormUtil;
import org.openea.eap.extj.generater.util.functionForm.FormListUtil;
import org.openea.eap.extj.generater.util.functionForm.FormUtil;
import org.openea.eap.extj.generater.util.functionForm.FunctionFlowUtil;
import org.springframework.stereotype.Component;

/**
 * 代码生成工厂类
 *
 * 
 */
@Component
public class CodeGenerateFactory {

	/**
	 * 根据模板路径对应实体
	 * @param templateMethod
	 * @return
	 */
	public CodeGenerateUtil getGenerator(String templateMethod){
		if (templateMethod.equals(TemplateMethodEnum.T2.getMethod())){
			return  FormListUtil.getFormListUtil();
		}else if (templateMethod.equals(TemplateMethodEnum.T4.getMethod())){
			return  FormUtil.getFormUtil();
		}else if (templateMethod.equals(TemplateMethodEnum.T3.getMethod())){
			return  FunctionFlowUtil.getFunctionFlowUtil();
		}else if (templateMethod.equals(TemplateMethodEnum.T5.getMethod())){
			return  FlowFormUtil.getFormUtil();
		}else {
			return null;
		}
	}
}
