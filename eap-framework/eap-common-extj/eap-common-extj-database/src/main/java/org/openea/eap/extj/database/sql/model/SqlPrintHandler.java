package org.openea.eap.extj.database.sql.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * 打印SQL语句功能类
 *
 */
@Component
@ToString
@Data
public class SqlPrintHandler {

    /**
     * 打印使用的SQL
     */
    private StringBuffer sql = new StringBuffer();
    private StringBuffer sql2 = new StringBuffer();

    /**
     * 将要转换成的数据库类型
     */
    private String toDbType;

    /**
     * 创表开关
     */
    private Boolean creFlag = false;

    /**
     * 注释开关
     */
    private Boolean commentFlag = false;

    /**
     * 插入开关
     */
    private Boolean insertFlag = false;

    /**
     * 打印开关
     */
    private Boolean printFlag = false;

    /**
     * 执行开关
     */
    private Boolean executeFlag = true;

    /**
     * 文件
     */
    @Setter(value = AccessLevel.NONE)
    private File sqlFile;

    private File sqlFile2;

    /**
     * 开启打印状态
     */
    public void start(String outPath, Boolean creFlag, Boolean commentFlag, Boolean insertFlag, String toDbType) throws Exception {
        printFlag = true;
        this.executeFlag = false;
        this.sqlFile = formatOutPath(outPath);
        this.creFlag = creFlag;
        this.commentFlag = commentFlag;
        this.insertFlag = insertFlag;
        this.toDbType = toDbType;
    }

    /**
     * 格式化路径
     * @param outPath 输出路径
     */
    public static File formatOutPath(String outPath) throws Exception{
        File file = new File(outPath);
        if(file.exists()){
            if(file.isDirectory()) {
                return file;
            }else if(file.isFile()){
                throw  new Exception("不是文件夹路径");
            }
        }
        throw  new Exception("生成路径不存在");
    }

    /**
     * 关闭打印状态
     */
    public void close(){
        this.printFlag = false;
        this.creFlag = false;
        this.commentFlag = false;
        this.insertFlag = false;
        this.executeFlag = true;
        this.sqlFile = null;
        this.sqlFile2 = null;
        clear();
    }

    private void clear(){
        this.sql = new StringBuffer();
        this.sql2 = new StringBuffer();
    }

    public void setToDbType(String toDbType){
        this.toDbType = toDbType;
    }

    public void setFileName(String fileName){
        if(sqlFile.isFile()){
            sqlFile = new File(sqlFile.getPath().replace(sqlFile.getName(), fileName));
        }else if(this.sqlFile.isDirectory()){
            sqlFile = new File(sqlFile.getPath() + "/" + fileName);
        }
    }

    public Boolean creTable(String sql){
        if(this.printFlag && this.creFlag) this.sql.append(sql).append(";\n");
        return this.printFlag;
    }

    public Boolean oracleAutoIncrement(String sql){
        if(this.printFlag) this.sql.append(sql).append(";\n");
        return this.printFlag;
    }

    public Boolean comment(String sql){
        if(this.printFlag && this.commentFlag) this.sql.append(sql).append(";\n");
        return this.printFlag;
    }

    public void deleteAllInfo(String sql){
        if(this.insertFlag) this.sql.append(sql).append(";\n");
    }

    public Boolean insert(String sql){
        if(this.printFlag && this.insertFlag) this.sql.append(sql);
        return this.printFlag;
    }

    /**
     * Oracle使用
     */
    public void updateClob(String sql){
        if(this.insertFlag) this.sql2.append(";\n/\n").append(sql);
    }

    public void tableInfo(String table){
        table = "-- ----------------------------\n" +
                "-- 表名：" + table + "\n" +
                "-- ----------------------------\n";
        if(insertFlag) this.sql.append(table);
    }

    public SqlPrintHandler append(String sql){
        if(this.insertFlag) this.sql.append(sql);
        return this;
    }

    public void print() throws Exception {
        if(printFlag){
            createSqlFile(sql.toString(), this.sqlFile);
//            if(sqlFile2 != null) createSqlFile(sql2.toString(), this.sqlFile2);
        }
        // 打印完清理SQL语句
        clear();
    }

    private void createSqlFile(String outSql, File file) throws Exception {
        OutputStream outputStream = new FileOutputStream(file);
        String CHARSET_UTF8 = "UTF-8";
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, CHARSET_UTF8);
        writer.append(outSql);
        writer.close();
    }

    /**
     * 打开文件夹
     * @param folder 文件路径
     */
    public static void openDirectory(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            return;
        }
        Runtime runtime = null;
        try {
            runtime = Runtime.getRuntime();
//            if (!SystemUtil.isWindows) {
            if (true) {
                runtime.exec("cmd /c start explorer " + folder);
            } else {
                // System.out.println("is linux");
                runtime.exec("nautilus " + folder);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (null != runtime) {
                runtime.runFinalization();
            }
        }
    }

}
