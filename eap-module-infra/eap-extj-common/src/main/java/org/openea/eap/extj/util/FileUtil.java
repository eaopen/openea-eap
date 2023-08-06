package org.openea.eap.extj.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    private static final int BUFFER_SIZE = 2048;

    public FileUtil() {
    }

    public static boolean fileIsExists(String filePath) {
        File f = new File(XSSEscape.escapePath(filePath));
        return f.exists();
    }

    public static boolean fileIsFile(String filePath) {
        File f = new File(XSSEscape.escapePath(filePath));
        return f.isFile();
    }

    public static boolean deleteTmp(MultipartFile multipartFile) {
        try {
            CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile)multipartFile;
            DiskFileItem diskFileItem = (DiskFileItem)commonsMultipartFile.getFileItem();
            File storeLocation = diskFileItem.getStoreLocation();
            deleteEmptyDirectory(storeLocation);
            return true;
        } catch (Exception var4) {
            log.error("删除tmp文件失败,错误：" + var4.getMessage());
            return false;
        }
    }

    public static boolean createFile(String filePath, String fileName) {
        String strFilePath = XSSEscape.escapePath(filePath + fileName);
        File file = new File(XSSEscape.escapePath(filePath));
        if (!file.exists()) {
            file.mkdirs();
        }

        File subfile = new File(XSSEscape.escapePath(strFilePath));
        if (!subfile.exists()) {
            try {
                boolean b = subfile.createNewFile();
                return b;
            } catch (IOException var6) {
                var6.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    public static void createDirs(String filePath) {
        File file = new File(XSSEscape.escapePath(filePath));
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public static List<File> getFile(File file) {
        List<File> list = new ArrayList();
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return list;
        } else {
            File[] var3 = fileArray;
            int var4 = fileArray.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File f = var3[var5];
                if (f.isFile()) {
                    list.add(0, f);
                }
            }

            return list;
        }
    }

    public static List<File> getFile(File file, List<File> list) {
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return list;
        } else {
            File[] var3 = fileArray;
            int var4 = fileArray.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File f = var3[var5];
                if (f.isFile()) {
                    list.add(0, f);
                } else {
                    getFile(f, list);
                }
            }

            return list;
        }
    }

    public static void deleteFileAll(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                log.info(file.getAbsolutePath() + " 删除中...");
                file.delete();
                log.info("删除成功！");
                return;
            }

            File[] files = file.listFiles();

            for(int i = 0; i < files.length; ++i) {
                deleteFileAll(files[i]);
            }

            file.delete();
        } else {
            log.info(file.getAbsolutePath() + " 文件不存在！");
        }

    }

    public static void deleteFile(String filePath) {
        File file = new File(XSSEscape.escapePath(filePath));
        if (file.exists() && file.isFile()) {
            file.delete();
        }

    }

    public static void deleteEmptyDirectory(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for(int i = 0; i < files.length; ++i) {
                    deleteEmptyDirectory(files[i]);
                }

                files = file.listFiles();
            }

            if (files == null || files.length == 0) {
                String absolutePath = file.getAbsolutePath();
                file.delete();
                log.info("删除空文件夹！路径：" + absolutePath);
            }
        }

    }

    public static void open(String path) {
        try {
            String osName = System.getProperty("os.name");
            if (osName != null) {
                if (osName.contains("Mac")) {
                    Runtime.getRuntime().exec("open " + path);
                } else if (osName.contains("Windows")) {
                    Runtime.getRuntime().exec("cmd /c start " + path);
                } else {
                    log.debug("文件输出目录:" + path);
                }
            }
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public static void writeToFile(String strcontent, String filePath, String fileName) {
        String strFilePath = filePath + fileName;
        File subfile = new File(strFilePath);

        try {
            RandomAccessFile raf = new RandomAccessFile(subfile, "rw");

            try {
                raf.seek(subfile.length());
                raf.write(strcontent.getBytes());
            } finally {
                if (Collections.singletonList(raf).get(0) != null) {
                    raf.close();
                }

            }
        } catch (IOException var10) {
            var10.printStackTrace();
        }

    }

    public static void writeToFile(InputStream is, String filePath, String fileName) {
        File subfile = new File(XSSEscape.escapePath(filePath));

        try {
            FileOutputStream downloadFile = new FileOutputStream(subfile);

            try {
                byte[] bytes = new byte[1024];

                int index;
                while((index = is.read(bytes)) != -1) {
                    downloadFile.write(bytes, 0, index);
                    downloadFile.flush();
                }
            } finally {
                if (Collections.singletonList(downloadFile).get(0) != null) {
                    downloadFile.close();
                }

            }
        } catch (IOException var12) {
            var12.printStackTrace();
        }

    }

    public static void modifyFile(String path, String content, boolean append) {
        try {
            FileWriter fileWriter = new FileWriter(path, append);

            try {
                BufferedWriter writer = new BufferedWriter(fileWriter);

                try {
                    writer.append(content);
                    writer.flush();
                    writer.close();
                } finally {
                    if (Collections.singletonList(writer).get(0) != null) {
                        writer.close();
                    }

                }
            } finally {
                if (Collections.singletonList(fileWriter).get(0) != null) {
                    fileWriter.close();
                }

            }
        } catch (IOException var15) {
            var15.printStackTrace();
        }

    }

    public static String getString(String filePath, String filename) {
        try {
            FileInputStream inputStream = null;

            try {
                inputStream = new FileInputStream(new File(XSSEscape.escapePath(filePath + filename)));
                InputStreamReader inputStreamReader = null;

                try {
                    inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    try {
                        StringBuffer sb = new StringBuffer("");

                        String line;
                        while((line = reader.readLine()) != null) {
                            sb.append(line);
                            sb.append("\n");
                        }

                        String var7 = sb.toString();
                        return var7;
                    } finally {
                        if (Collections.singletonList(reader).get(0) != null) {
                            reader.close();
                        }

                    }
                } finally {
                    if (Collections.singletonList(inputStreamReader).get(0) != null) {
                        inputStreamReader.close();
                    }

                }
            } finally {
                if (Collections.singletonList(inputStream).get(0) != null) {
                    inputStream.close();
                }

            }
        } catch (Exception var26) {
            var26.printStackTrace();
            return "";
        }
    }

    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(XSSEscape.escapePath(oldPath));
        File newFile = new File(XSSEscape.escapePath(newPath));
        oleFile.renameTo(newFile);
    }

    public static boolean copy(String fromFile, String toFile) {
        File root = new File(XSSEscape.escapePath(fromFile));
        if (!root.exists()) {
            return false;
        } else {
            File[] currentFiles = root.listFiles();
            File targetDir = new File(XSSEscape.escapePath(toFile));
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            for(int i = 0; i < currentFiles.length; ++i) {
                if (currentFiles[i].isDirectory()) {
                    copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");
                } else {
                    copyFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
                }
            }

            return true;
        }
    }

    public static boolean copyFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(XSSEscape.escapePath(fromFile));

            try {
                OutputStream fosto = new FileOutputStream(XSSEscape.escapePath(toFile));

                try {
                    byte[] bt = new byte[1024];

                    int c;
                    while((c = fosfrom.read(bt)) > 0) {
                        fosto.write(bt, 0, c);
                    }

                    boolean var6 = true;
                    return var6;
                } finally {
                    if (Collections.singletonList(fosto).get(0) != null) {
                        fosto.close();
                    }

                }
            } finally {
                if (Collections.singletonList(fosfrom).get(0) != null) {
                    fosfrom.close();
                }

            }
        } catch (Exception var17) {
            return false;
        }
    }

    public static boolean copyFile(String fromFile, String toFile, String fileName) {
        try {
            File targetDir = new File(XSSEscape.escapePath(toFile));
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            InputStream fosfrom = new FileInputStream(fromFile);

            try {
                OutputStream fosto = new FileOutputStream(toFile + fileName);

                try {
                    byte[] bt = new byte[1024];

                    int c;
                    while((c = fosfrom.read(bt)) > 0) {
                        fosto.write(bt, 0, c);
                    }

                    boolean var8 = true;
                    return var8;
                } finally {
                    if (Collections.singletonList(fosto).get(0) != null) {
                        fosto.close();
                    }

                }
            } finally {
                if (Collections.singletonList(fosfrom).get(0) != null) {
                    fosfrom.close();
                }

            }
        } catch (Exception var19) {
            return false;
        }
    }

    public static InputStream readFileToInputStream(String path) {
        InputStream inputStream = null;

        try {
            File file = new File(XSSEscape.escapePath(path));
            inputStream = new FileInputStream(file);
        } catch (IOException var3) {
            var3.getMessage();
        }

        return inputStream;
    }

    public static void write(InputStream inputStream, String path, String fileName) {
        OutputStream os = null;
        long dateStr = System.currentTimeMillis();

        try {
            byte[] bs = new byte[1024];
            File tempFile = new File(XSSEscape.escapePath(path));
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            String newFileName = tempFile.getPath() + File.separator + fileName;
            log.info("保存文件：" + newFileName);
            os = new FileOutputStream(XSSEscape.escapePath(newFileName));

            int len;
            while((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (IOException var20) {
            log.error("生成excel失败");
        } catch (Exception var21) {
            log.error("生成excel失败");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                inputStream.close();
            } catch (IOException var19) {
                log.error("关闭链接失败" + var19.getMessage());
            }

        }

    }

    public static void writeFile(InputStream inputStream, String path, String fileName) {
        OutputStream os = null;

        try {
            byte[] bs = new byte[1024];
            File tempFile = new File(XSSEscape.escapePath(path));
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            String newFileName = tempFile.getPath() + File.separator + fileName;
            log.info("保存文件：" + newFileName);
            os = new FileOutputStream(XSSEscape.escapePath(newFileName));

            int len;
            while((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (IOException var18) {
            log.error("生成excel失败");
        } catch (Exception var19) {
            log.error("生成excel失败");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                inputStream.close();
            } catch (IOException var17) {
                log.error("关闭链接失败" + var17.getMessage());
            }

        }

    }

    public static void upFile(MultipartFile file, String filePath, String fileName) {
        try {
            File tempFile = new File(XSSEscape.escapePath(filePath));
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            File f = new File(filePath, fileName);
            file.transferTo(f);
        } catch (Exception var5) {
            log.error(var5.getMessage());
        }

    }

    public static String getCreateTime(String filePath) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File(XSSEscape.escapePath(filePath));
        long modifiedTime = file.lastModified();
        Date date = new Date(modifiedTime);
        String dateString = format.format(date);
        return dateString;
    }

    public static String getFileType(File file) {
        if (file.isFile()) {
            String fileName = file.getName();
            String fileTyle = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            return fileTyle;
        } else {
            return null;
        }
    }

    public static String getFileType(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".") + 1;
        String suffix = fileName.substring(lastIndexOf);
        return suffix;
    }

    public static String getSize(String data) {
        String size = "";
        if (data != null && !StringUtil.isEmpty(data)) {
            long fileS = Long.parseLong(data);
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024L) {
                size = df.format((double)fileS) + "BT";
            } else if (fileS < 1048576L) {
                size = df.format((double)fileS / 1024.0) + "KB";
            } else if (fileS < 1073741824L) {
                size = df.format((double)fileS / 1048576.0) + "MB";
            } else {
                size = df.format((double)fileS / 1.073741824E9) + "GB";
            }
        } else {
            size = "0BT";
        }

        return size;
    }

    public static void toZip(String outDir, boolean keepDirStructure, String... srcDir) {
        try {
            OutputStream out = new FileOutputStream(new File(XSSEscape.escapePath(outDir)));

            try {
                ZipOutputStream zos = null;

                try {
                    zos = new ZipOutputStream(out);
                    List<File> sourceFileList = new ArrayList();
                    String[] var6 = srcDir;
                    int var7 = srcDir.length;

                    for(int var8 = 0; var8 < var7; ++var8) {
                        String dir = var6[var8];
                        File sourceFile = new File(XSSEscape.escapePath(dir));
                        sourceFileList.add(sourceFile);
                    }

                    compress(sourceFileList, zos, keepDirStructure);
                } catch (Exception var21) {
                    throw new RuntimeException("zip error from ZipUtils", var21);
                } finally {
                    if (Collections.singletonList(zos).get(0) != null) {
                        zos.close();
                    }

                }
            } finally {
                if (Collections.singletonList(out).get(0) != null) {
                    out.close();
                }

            }
        } catch (Exception var24) {
            log.error("压缩失败:{}", var24.getMessage());
        }

    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[2048];
        if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            FileInputStream in = new FileInputStream(sourceFile);

            try {
                int len;
                while((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }

                zos.closeEntry();
                in.close();
            } finally {
                if (Collections.singletonList(in).get(0) != null) {
                    in.close();
                }

            }
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles != null && listFiles.length != 0) {
                File[] var13 = listFiles;
                int var7 = listFiles.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    File file = var13[var8];
                    if (keepDirStructure) {
                        compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }
                }
            } else if (keepDirStructure) {
                zos.putNextEntry(new ZipEntry(name + "/"));
                zos.closeEntry();
            }
        }

    }

    private static void compress(List<File> sourceFileList, ZipOutputStream zos, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[2048];
        Iterator var4 = sourceFileList.iterator();

        while(true) {
            while(var4.hasNext()) {
                File sourceFile = (File)var4.next();
                String name = sourceFile.getName();
                if (sourceFile.isFile()) {
                    zos.putNextEntry(new ZipEntry(name));
                    FileInputStream in = new FileInputStream(sourceFile);

                    try {
                        int len;
                        while((len = in.read(buf)) != -1) {
                            zos.write(buf, 0, len);
                        }

                        zos.closeEntry();
                        in.close();
                    } finally {
                        if (Collections.singletonList(in).get(0) != null) {
                            in.close();
                        }

                    }
                } else {
                    File[] listFiles = sourceFile.listFiles();
                    if (listFiles != null && listFiles.length != 0) {
                        File[] var8 = listFiles;
                        int var9 = listFiles.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                            File file = var8[var10];
                            if (keepDirStructure) {
                                compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                            } else {
                                compress(file, zos, file.getName(), keepDirStructure);
                            }
                        }
                    } else if (keepDirStructure) {
                        zos.putNextEntry(new ZipEntry(name + "/"));
                        zos.closeEntry();
                    }
                }
            }

            return;
        }
    }

    public static boolean fileType(String fileType, String fileExtension) {
        String[] allowExtension = fileType.split(",");
        return Arrays.asList(allowExtension).contains(fileExtension.toLowerCase());
    }

    public static boolean imageType(String imageType, String fileExtension) {
        String[] allowExtension = imageType.split(",");
        return Arrays.asList(allowExtension).contains(fileExtension.toLowerCase());
    }

    public static boolean fileSize(Long fileSize, int maxSize) {
        return fileSize > (long)maxSize;
    }

    public static String getFileContent(MultipartFile multipartFile, String filePath) {
        String fileName = multipartFile.getName();
        upFile(multipartFile, filePath, fileName);
        String fileContent = getString(filePath, fileName);
        return fileContent;
    }

    public static String getFileContent(MultipartFile multipartFile) {
        StringBuffer content = new StringBuffer();

        try {
            InputStream is = multipartFile.getInputStream();

            try {
                InputStreamReader isReader = new InputStreamReader(is, StandardCharsets.UTF_8);

                try {
                    BufferedReader br = new BufferedReader(isReader);

                    try {
                        while(br.ready()) {
                            content.append(br.readLine());
                        }
                    } finally {
                        if (Collections.singletonList(br).get(0) != null) {
                            br.close();
                        }

                    }
                } finally {
                    if (Collections.singletonList(isReader).get(0) != null) {
                        isReader.close();
                    }

                }
            } finally {
                if (Collections.singletonList(is).get(0) != null) {
                    is.close();
                }

            }
        } catch (IOException var23) {
            log.error(var23.getMessage());
        }

        return content.toString();
    }

    public static boolean existsSuffix(MultipartFile multipartFile, String type) {
        return !multipartFile.getOriginalFilename().endsWith("." + type) || multipartFile.getSize() < 1L;
    }

    public static MultipartFile createFileItem(File file) {
        FileItemFactory factory = new DiskFileItemFactory(16, (File)null);
        FileItem item = factory.createItem("textField", "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];

        try {
            FileInputStream fis = new FileInputStream(file);

            try {
                OutputStream os = item.getOutputStream();
                while((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                os.close();
                fis.close();
            } finally {
                if (Collections.singletonList(fis).get(0) != null) {
                    fis.close();
                }

            }
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        MultipartFile multipartFile = new CommonsMultipartFile(item);
        return multipartFile;
    }

    public static File multipartFileToFile(MultipartFile file) {
        File toFile = null;
        if (file.getSize() > 0L) {
            InputStream ins = null;

            try {
                ins = file.getInputStream();
                toFile = new File(file.getOriginalFilename());

                try {
                    OutputStream os = new FileOutputStream(toFile);

                    try {
                        int bytesRead = 0;
                        byte[] buffer = new byte[8192];
                        while((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }

                        os.close();
                        ins.close();
                    } finally {
                        if (Collections.singletonList(os).get(0) != null) {
                            os.close();
                        }

                    }
                } catch (Exception var11) {
                    var11.printStackTrace();
                }

                ins.close();
            } catch (Exception var12) {
                log.error(var12.getMessage());
            }
        }

        return toFile;
    }
}

