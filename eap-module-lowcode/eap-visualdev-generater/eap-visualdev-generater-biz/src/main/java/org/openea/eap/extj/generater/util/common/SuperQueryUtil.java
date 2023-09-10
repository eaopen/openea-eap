package org.openea.eap.extj.generater.util.common;

import lombok.Cleanup;
import org.openea.eap.extj.util.XSSEscape;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class SuperQueryUtil {
	/**
	 * 生成前端js文件
	 * @param data
	 * @param path
	 * @param jsFileType
	 * @throws IOException
	 */
	public static void CreateJsFile(String data,String path,String jsFileType) throws IOException {
		String content = "const "+jsFileType+" = " + data;

		File jsFile = new File(path);
		Writer writer = null;
		try {
			writer = new FileWriter(jsFile);
			writer.write(content);
			writer.write(System.getProperty("line.separator"));
			writer.write("export default "+jsFileType);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer!=null){
				writer.close();
			}
		}
	}

	public static void CreateFlowFormJsonFile(String data, String path){
		try {
			File file = new File(XSSEscape.escapePath(path+File.separator+"flowForm."+ ModuleTypeEnum.FLOW_FLOWDFORM.getTableName()));
			boolean b = file.createNewFile();
			if(b) {
				@Cleanup Writer out = new FileWriter(file);
				out.write(data);
				out.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


}
