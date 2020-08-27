package funcScripts;

import javax.servlet.Servlet;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Time;

public class TimeTestLogger {
    public static FileWriter fw_ts = null;
    public static FileWriter fw_tj = null;

    static String tsLogFile = "tsTime";
    static String tjLogFile = "tjTime";


    static public void initializeLogFile(String contextPath){
        String logDirPath = contextPath + "timeTestLogs".replace("/", File.separator);
        File logDir = new File(logDirPath);
        // System.out.println("logDir: " + logDirPath);

        if( !logDir.exists() ){
            logDir.mkdir();
        }

        String d = HelperFunc.getCurrentDate(false);
        String tsTimeLogFilePath = logDirPath + "/log_" + d + "_" + tsLogFile + ".log";
        tsTimeLogFilePath = tsTimeLogFilePath.replace("/", File.separator);
        String tjTimeLogFilePath = logDirPath + "/log_" + d + "_" + tjLogFile + ".log";
        tjTimeLogFilePath = tjTimeLogFilePath.replace("/", File.separator);

        File tsLogFile = new File(tsTimeLogFilePath);
        File tjLogFile = new File(tjTimeLogFilePath);
        try{
            fw_ts = new FileWriter(tsLogFile, true);
            fw_tj = new FileWriter(tjLogFile, true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    static public void tsTimeTestWriteLog(Object tar){
        try{
            String d = HelperFunc.getCurrentDate(true);
            fw_ts.write(d + " " + tar.toString() + "\n");
            fw_ts.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    static public void tjTimeTestWriteLog(Object tar){
        try{
            String d = HelperFunc.getCurrentDate(true);
            fw_tj.write(d + " " + tar.toString() + "\n");
            fw_tj.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    static public void closeTimeTestLogFile(){
        try {
            fw_ts.close();
            fw_tj.close();
            fw_ts = null;
            fw_tj = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
