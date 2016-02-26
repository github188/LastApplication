package com.iermu.client.util;

import android.text.TextUtils;
import android.util.Log;

import com.iermu.client.config.PathConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * 日志管理类
 *
 * Created by wcy on 15/6/22.
 */
public class Logger {

    private static String GLOBAL_TAG = "logger";
	/**
	 * 日志开关
	 */
	private static final boolean LOG_OPEN_DEBUG = false;
	private static final boolean LOG_OPEN_POINT = true;

	/**
	 * 日志类型开关，必须 LOG_OPEN_DEBUG = true的时候才能启作用
	 */
	private static boolean logOpeni = true;
	private static boolean logOpend = true;
	private static boolean logOpenw = true;
	private static boolean logOpene = true;
    private static boolean logOpenv = true;

	/**
	 * 日志目录
	 */	
	private static final String PATH_LOG_INFO = PathConfig.APP_LOG_PATH + "/info/";
	private static final String PATH_LOG_WARNING = PathConfig.APP_LOG_PATH + "/warning/";
	public static final String PATH_LOG_ERROR = PathConfig.APP_LOG_PATH + "/error/";
	private static final String AUTHOR = "HARLAN ";
	public static final boolean ENABLE_DEBUG = false;

    private static boolean O_INFO		= false;
    private static boolean O_ERROR		= true;
    private static final int DEBUG		= 1;
    private static final int ERROR		= 2;
    private static final int INFO		= 3;
    private static final int VERBOSE	= 4;
    private static final int WARN		= 5;

    /**
     * 初始化
     * @param tag
     */
    public static void init(String tag) {
        GLOBAL_TAG = tag;
    }

    // 清空已有的文件内容，以便下次重新写入新的内容
    public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取文件内容，文件中的内容为String
    public static String readFile(String fileName) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("",
                Locale.SIMPLIFIED_CHINESE);
        dateFormat.applyPattern("yyyy");
        String path = PATH_LOG_INFO + dateFormat.format(date) + "/";
        dateFormat.applyPattern("MM");
        path += dateFormat.format(date) + "/";
        dateFormat.applyPattern("dd");
        String formatDate = dateFormat.format(date);
        path += (TextUtils.isEmpty(fileName)?formatDate : formatDate+"_"+fileName) + ".txt";

        String str = "";
        File file =new File(path);
        if(!file.exists()) {
            return str;
        }
        try {
            BufferedReader bufferedReader =new BufferedReader(new FileReader(file));
            while(null !=(str=bufferedReader.readLine())) {}
        }catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 强制输出到指定文件中
     * @param msg
     * @param forceOutFileName
     */
    public static void oFile(String msg, String forceOutFileName) {
        show(false, GLOBAL_TAG, O_INFO, forceOutFileName, INFO, msg, null);
    }

    /**
     * 将日志消息写入文件
     * @param msg
     */
    public static void oInfo(String msg){
        show(GLOBAL_TAG, O_INFO, "", INFO, msg, null);
    }

    /**
     * 将日志消息写入文件
     * @param msg
     * @param thr
     */
    public static void oInfo(String msg, Throwable thr){
        show(GLOBAL_TAG, O_INFO, "", INFO, msg, thr);
    }

    /**
     * 输出错误日志消息
     *
     * @param msg
     */
    public static void oError(String msg) {
        show(GLOBAL_TAG, O_ERROR, "", ERROR, msg, null);
    }

    /**
     * 输出错误日志消息
     *
     * @param thr
     */
    public static void oError(Throwable thr) {
        show(GLOBAL_TAG, O_ERROR, "", ERROR, "", thr);
    }

    /**
     * 将日志消息写入文件
     * @param msg
     * @param thr
     */
    public static void oError(String msg, Throwable thr){
        show(GLOBAL_TAG, O_ERROR, "", ERROR, msg, thr);
    }

    /**
     * Send a VERBOSE log message.
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        show(VERBOSE, msg, null);
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void v(String msg, Throwable thr) {
        show(VERBOSE, msg, thr);
    }

    /**
     * Send a DEBUG log message.
     * @param msg
     */
    public static void d(String msg) {
        show(DEBUG, msg, null);
    }

    /**
     * Send a DEBUG log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void d(String msg, Throwable thr) {
        show(DEBUG, msg, thr);
    }

    /**
     * Send an INFO log message.
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
        show(INFO, msg, null);
    }

    /**
     * Send a INFO log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void i(String msg, Throwable thr) {
        show(INFO, msg, thr);
    }

    /**
     * Send an ERROR log message.
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        show(ERROR, msg, null);
    }

    /**
     * Send a WARN log message
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
        show(WARN, msg, null);
    }

    /**
     * Send a WARN log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void w(String msg, Throwable thr) {
        show(WARN, msg, thr);
    }

    /**
     * Send an empty WARN log message and log the exception.
     * @param thr An exception to log
     */
    public static void w(Throwable thr) {
        show(WARN, "", thr);
    }

    /**
     * Send an ERROR log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void e(String msg, Throwable thr) {
        show(ERROR, msg, thr);
    }

    public static void d(String tag, String message) {
        show(tag, false, "",  DEBUG, message, null);
    }

    public static void i(String tag, String message) {
        show(tag, false, "",  INFO, message, null);
    }

    public static void w(String tag, String message) {
        show(tag, false, "",  WARN, message, null);
    }

    public static void e(String tag, String message) {
        show(tag, false, "", ERROR, message, null);
    }

    /**
     * 显示，打印出日志
     * @param Style			输出日志信息的类型
     * @param Message		日志信息
     * @param thr 			异常记录
     */
    private static void show(int Style, String Message, Throwable thr){
        show(true,GLOBAL_TAG, false, "", Style, Message, thr);
    }

    private static void show(String tag, boolean forceOutFile, String forceOutFileName, int Style, String message, Throwable thr) {
        show(true, tag, forceOutFile, forceOutFileName, Style, message, thr);
    }

    private static void show(boolean buildMessage, String tag, boolean forceOutFile, String forceOutFileName, int Style, String message, Throwable thr) {
        logDebug(tag, Style, message, thr);
        message = buildMessage ? buildMessage(message, thr) : message;
        logPoint(forceOutFile, forceOutFileName, Style, message);
    }

    //Log日志输出到日志文件
    private static void logPoint(boolean forceOutFile, String forceOutFileName, int style, String message) {
        if(!forceOutFile && !LOG_OPEN_POINT) return;

        switch (style) {
        case DEBUG:
            point(PATH_LOG_INFO, forceOutFileName, message);
            break;
        case ERROR:
            point(PATH_LOG_ERROR, forceOutFileName, message);
            break;
        case INFO:
            point(PATH_LOG_INFO, forceOutFileName, message);
            break;
        case VERBOSE:
            point(PATH_LOG_INFO, forceOutFileName, message);
            break;
        case WARN:
            point(PATH_LOG_WARNING, forceOutFileName, message);
            break;
        }
    }

    //Debug模式输出日志
    private static void logDebug(String tag, int Style, String Message, Throwable thr) {
        if(!LOG_OPEN_DEBUG) return;
        switch (Style) {
        case DEBUG:
            if(!logOpend) break;
            if(null==thr)
                android.util.Log.d(tag, Message);
            else
                android.util.Log.d(tag, Message, thr);
            break;
        case ERROR:
            if(!logOpene) break;
            if(null==thr)
                android.util.Log.e(tag, Message);
            else
                android.util.Log.e(tag, Message, thr);
            break;
        case INFO:
            if(!logOpeni) break;
            if(null==thr)
                android.util.Log.i(tag, Message);
            else
                android.util.Log.i(tag, Message, thr);
            break;
        case VERBOSE:
            if(!logOpenv) break;
            if(null==thr)
                android.util.Log.v(tag, Message);
            else
                android.util.Log.v(tag, Message, thr);
            break;
        case WARN:
            if(!logOpenw) break;
            if(null==thr)
                android.util.Log.w(tag, Message);
            else
                android.util.Log.w(tag, Message, thr);
            break;
        }
    }

    /**
     * Building Message
     * @param msg The message you would like logged.
     * @return Message String
     */
    private static String buildMessage(String msg, Throwable thr) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        String traceString = Log.getStackTraceString(thr);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
        dateFormat.applyPattern("[yyyy-MM-dd HH:mm:ss]");
        String time = dateFormat.format(date);
        return new StringBuilder()
                .append("===> ").append(time).append(" ").append(GLOBAL_TAG).append(" ").append("\r\n")
                .append(msg).append("\r\n")
                .append((thr==null) ? "" : traceString+"\r\n")
                .append("===> AT FILE(")
                .append(caller.getFileName())//.append(caller.getClassName()).append(".").append(caller.getMethodName())
                .append(": ")
                .append(caller.getLineNumber())
                .append(" line)")			//.append(">>time: ").append(System.currentTimeMillis())
                .append("\r\n")
                .toString();
    }

	private static void point(String path, String fileName, String msg) {
		if (PhoneDevUtil.sdcard()) {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("",
					Locale.SIMPLIFIED_CHINESE);
			dateFormat.applyPattern("yyyy");
			path = path + dateFormat.format(date) + "/";
			dateFormat.applyPattern("MM");
			path += dateFormat.format(date) + "/";
			dateFormat.applyPattern("dd");
            String formatDate = dateFormat.format(date);
            path += (TextUtils.isEmpty(fileName)?formatDate : formatDate+"_"+fileName) + ".txt";
			File file = new File(path);
			if (!file.exists())
				createDipPath(path);
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file, true)));
				out.write(msg);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 根据文件路径 递归创建文件
	 * 
	 * @param file
	 */
	public static void createDipPath(String file) {
		String parentFile = file.substring(0, file.lastIndexOf("/"));
		File file1 = new File(file);
		File parent = new File(parentFile);
		if (!file1.exists()) {
			parent.mkdirs();
			try {
				file1.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	   * A little trick to reuse a formatter in the same thread
	   */
	  private static class ReusableFormatter {

	    private Formatter formatter;
	    private StringBuilder builder;

	    public ReusableFormatter() {
	      builder = new StringBuilder();
	      formatter = new Formatter(builder);
	    }

	    public String format(String msg, Object... args) {
	      formatter.format(msg, args);
	      String s = builder.toString();
	      builder.setLength(0);
	      return s;
	    }
	  }

	  private static final
	  ThreadLocal<ReusableFormatter>
	      thread_local_formatter =
	      new ThreadLocal<ReusableFormatter>() {
	        protected ReusableFormatter initialValue() {
	          return new ReusableFormatter();
	        }
	      };

	  public static String format(String msg, Object... args) {
	    ReusableFormatter formatter = thread_local_formatter.get();
	    return formatter.format(msg, args);
	  }
}
