package org.openea.eap.extj.extend.util;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openea.eap.extj.exception.ImportException;
import org.openea.eap.extj.util.ServletUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.XSSEscape;
import org.openea.eap.extj.util.DateUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

public class ExcelUtil {
    public ExcelUtil() {
    }

    public static MultipartFile workbookToCommonsMultipartFile(Workbook workbook, String fileName) {
        FileItemFactory factory = new DiskFileItemFactory(16, (File)null);
        FileItem fileItem = factory.createItem("textField", "text/plain", true, fileName);

        try {
            OutputStream os = fileItem.getOutputStream();
            workbook.write(os);
            os.close();
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
            return multipartFile;
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public static void dowloadExcel(Workbook workbook, String fileName) {
        try {
            HttpServletResponse response = ServletUtil.getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        } else {
            ImportParams params = new ImportParams();
            params.setTitleRows(titleRows);
            params.setHeadRows(headerRows);
            List<T> list = null;

            try {
                list = ExcelImportUtil.importExcel(new File(XSSEscape.escapePath(filePath)), pojoClass, params);
            } catch (Exception var7) {
                var7.printStackTrace();
            }

            return list;
        }
    }

    public static <T> List<T> importExcel(File file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws ImportException {
        if (file == null) {
            return null;
        } else {
            ImportParams params = new ImportParams();
            params.setTitleRows(titleRows);
            params.setHeadRows(headerRows);
            List<T> list = null;

            try {
                list = ExcelImportUtil.importExcel(file, pojoClass, params);
                return list;
            } catch (Exception var7) {
                throw new ImportException(var7.getMessage());
            }
        }
    }

    public static <T> List<T> importExcelByInputStream(InputStream inputStream, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws Exception {
        if (inputStream == null) {
            return null;
        } else {
            ImportParams params = new ImportParams();
            params.setTitleRows(titleRows);
            params.setHeadRows(headerRows);
            List<T> list = null;
            list = ExcelImportUtil.importExcel(inputStream, pojoClass, params);
            return list;
        }
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;

        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return list;
    }

    public static void imoportExcelToMap(File file, Integer titleIndex, List<Map> excelDataList) {
        List<Map<String, Object>> mapList = new ArrayList();
        FileInputStream inputStream = null;

        try {
            String fileName = file.getName();
            Workbook workbook = null;

            try {
                inputStream = new FileInputStream(file);

                try {
                    workbook = new HSSFWorkbook(inputStream);
                } catch (Exception var35) {
                    inputStream = new FileInputStream(file);
                    workbook = new XSSFWorkbook(inputStream);
                }

                Sheet sheet = ((Workbook)workbook).getSheetAt(0);
                Row titleRow = sheet.getRow(titleIndex - 1);

                int n;
                for(n = titleIndex; n < sheet.getPhysicalNumberOfRows(); ++n) {
                    Row row = sheet.getRow(n);
                    Map<String, Object> map = new HashMap();

                    for(int j = 0; j < row.getPhysicalNumberOfCells(); ++j) {
                        Cell cell = row.getCell(j);
                        Cell titleCell = titleRow.getCell(j);
                        if (cell != null && CellType.NUMERIC.equals(cell.getCellType())) {
                            short format = cell.getCellStyle().getDataFormat();
                            if (cell.getDateCellValue() != null && format > 0) {
                                String titleName = titleCell.getStringCellValue();
                                if (StringUtil.isEmpty(titleName)) {
                                    titleName = sheet.getRow(titleIndex - 2).getCell(j).getStringCellValue();
                                }

                                Date dateCellValue = cell.getDateCellValue();
                                String valueName = DateUtil.daFormat(dateCellValue);
                                map.put(titleName, valueName);
                            }
                        }
                    }

                    mapList.add(map);
                }

                if (!CollectionUtils.sizeIsEmpty(mapList)) {
                    for(n = 0; n < mapList.size(); ++n) {
                        Map<String, Object> a = (Map)mapList.get(n);
                        Map b = (Map)excelDataList.get(n);
                        if (a != null) {
                            Iterator var41 = a.keySet().iterator();

                            while(var41.hasNext()) {
                                String key = (String)var41.next();
                                if (b.containsKey(key)) {
                                    b.put(key, a.get(key));
                                }
                            }
                        }
                    }
                }
            } finally {
                if (Collections.singletonList(workbook).get(0) != null) {
                    ((Workbook)workbook).close();
                }

            }
        } catch (Exception var37) {
            var37.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException var34) {
                var34.printStackTrace();
            }

        }

    }
}