package org.openea.eap.extj.base.util;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.util.FileUtil;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.XSSEscape;
import org.openea.eap.extj.base.model.read.ReadEnum;
import org.openea.eap.extj.base.model.read.ReadListVO;
import org.openea.eap.extj.base.model.read.ReadModel;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**

 */
@Slf4j
public class ReadFile {

    /**
     * 预览代码
     *
     * @param codePath
     * @return
     */
    public static List<ReadListVO> priviewCode(String codePath) {
        File fileAll = new File(XSSEscape.escapePath(codePath));
        List<File> fileList = new ArrayList<>();
        if (fileAll.exists()) {
            FileUtil.getFile(fileAll, fileList);
        }
        Map<String, List<ReadModel>> data = new LinkedHashMap<>();
        for (int i = fileList.size() - 1; i >= 0; i--) {
            File file = fileList.get(i);
            String path = file.getAbsolutePath();
            ReadEnum readEnum = ReadEnum.getMessage(path);
            if (readEnum != null) {
                ReadModel readModel = new ReadModel();
                String fileContent = readFile(file);
                readModel.setFileContent(fileContent);
                readModel.setFileName(file.getName());
                readModel.setFileType(readEnum.getMessage());
                readModel.setId(RandomUtil.uuId());
                String folderName = FileUtil.getFileType(file);
                readModel.setFolderName(folderName);
                List<ReadModel> readModelList = data.get(readEnum.getMessage()) != null ? data.get(readEnum.getMessage()) : new ArrayList<>();
                readModelList.add(readModel);
                data.put(readEnum.getMessage(), readModelList);
            }
        }
        List<ReadListVO> list = new ArrayList<>();
        for (String fileName : data.keySet()) {
            ReadListVO listVO = new ReadListVO();
            listVO.setFileName(fileName);
            listVO.setChildren(data.get(fileName));
            listVO.setId(RandomUtil.uuId());
            list.add(listVO);
        }
        return list;
    }


    /**
     * 读取指定目录下的文件
     *
     * @param path 文件的路径
     * @return 文件内容
     */
    private static String readFile(File path) {
        String fileRead = "";
        try {
            //创建一个输入流对象
            @Cleanup InputStream is = new FileInputStream(path);
            @Cleanup ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ((n = is.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            //释放资源
            is.close();
            fileRead = out.toString();
        } catch (IOException e) {
            log.error("代码生成器读取文件报错:" + e.getMessage());
        }
        return fileRead;
    }
}
