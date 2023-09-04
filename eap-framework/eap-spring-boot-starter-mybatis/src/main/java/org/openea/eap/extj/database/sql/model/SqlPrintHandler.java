package org.openea.eap.extj.database.sql.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@Data
public class SqlPrintHandler {

    private StringBuffer sql = new StringBuffer();
    private StringBuffer sql2 = new StringBuffer();
    private String toDbType;
    private Boolean creFlag = false;
    private Boolean commentFlag = false;
    private Boolean insertFlag = false;
    private Boolean printFlag = false;
    private Boolean executeFlag = true;
    private File sqlFile;
    private File sqlFile2;

    public void start(String outPath, Boolean creFlag, Boolean commentFlag, Boolean insertFlag, String toDbType) throws Exception {
        this.printFlag = true;
        this.executeFlag = false;
        this.sqlFile = formatOutPath(outPath);
        this.creFlag = creFlag;
        this.commentFlag = commentFlag;
        this.insertFlag = insertFlag;
        this.toDbType = toDbType;
    }

    public static File formatOutPath(String outPath) throws Exception {
        File file = new File(outPath);
        if (file.exists()) {
            if (file.isDirectory()) {
                return file;
            } else if (file.isFile()) {
                return new File(file.getParent());
            } else {
                throw new Exception("路径异常");
            }
        } else {
            boolean flag = file.mkdir();
            if (!flag) {
                throw new Exception("路径异常");
            } else {
                return file;
            }
        }
    }

    public void close() {
        this.printFlag = false;
        this.creFlag = false;
        this.commentFlag = false;
        this.insertFlag = false;
        this.executeFlag = true;
        this.sqlFile = null;
        this.sqlFile2 = null;
        this.clear();
    }

    private void clear() {
        this.sql = new StringBuffer();
        this.sql2 = new StringBuffer();
    }

    public void setToDbType(String toDbType) {
        this.toDbType = toDbType;
    }

    public void setFileName(String fileName) {
        if (this.sqlFile.isFile()) {
            this.sqlFile = new File(this.sqlFile.getPath().replace(this.sqlFile.getName(), fileName));
        } else if (this.sqlFile.isDirectory()) {
            this.sqlFile = new File(this.sqlFile.getPath() + "/" + fileName);
        }

    }

    public Boolean creTable(String sql) {
        if (this.printFlag && this.creFlag) {
            this.sql.append(sql).append(";\n");
        }

        return this.printFlag;
    }

    public Boolean dropTable(String sql) {
        if (this.printFlag && this.creFlag) {
            this.sql.append(sql).append(";\n");
        }

        return this.printFlag;
    }

    public Boolean oracleAutoIncrement(String sql) {
        if (this.printFlag) {
            this.sql.append(sql).append(";\n");
        }

        return this.printFlag;
    }

    public Boolean comment(String sql) {
        if (this.printFlag && this.commentFlag) {
            this.sql.append(sql).append(";\n");
        }

        return this.printFlag;
    }

    public void deleteAllInfo(String sql) {
        if (this.insertFlag) {
            this.sql.append(sql).append(";\n");
        }

    }

    public Boolean insert(String sql) {
        if (this.printFlag && this.insertFlag) {
            this.sql.append(sql);
        }

        return this.printFlag;
    }

    public void updateClob(String sql) {
        if (this.insertFlag) {
            this.sql2.append(";\n/\n").append(sql);
        }

    }

    public void tableInfo(String table) {
        table = "-- ----------------------------\n-- 表名：" + table + "\n-- ----------------------------\n";
        if (this.insertFlag) {
            this.sql.append(table);
        }

    }

    public SqlPrintHandler append(String sql) {
        if (this.insertFlag) {
            this.sql.append(sql);
        }

        return this;
    }

    public void print() throws Exception {
        if (this.printFlag) {
            this.createSqlFile(this.sql.toString(), this.sqlFile);
        }

        this.clear();
    }

    private void createSqlFile(String outSql, File file) throws Exception {
        OutputStream outputStream = new FileOutputStream(file);
        String CHARSET_UTF8 = "UTF-8";
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, CHARSET_UTF8);
        writer.append(outSql);
        writer.close();
    }

    public static void openDirectory(String folder) {
        File file = new File(folder);
        if (file.exists()) {
            Runtime runtime = null;

            try {
                runtime = Runtime.getRuntime();
                runtime.exec("cmd /c start explorer " + folder);
            } catch (IOException var7) {
                var7.printStackTrace();
            } finally {
                if (null != runtime) {
                    runtime.runFinalization();
                }

            }

        }
    }
}
