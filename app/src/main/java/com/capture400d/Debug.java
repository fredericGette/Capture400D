package com.capture400d;

import android.os.Environment;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Debug {

    private static BufferedWriter logWriter;
    private static File logFile;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void open() {
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            logFile = new File(sdcard, "debug.log");
            logWriter = new BufferedWriter(new FileWriter(logFile));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void println(String log) {
        try {
            logWriter.write(sdf.format(new Date())+" "+log);
            logWriter.newLine();
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(String log) {
        try {
            logWriter.write(log);
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        println(sw.toString()); // stack trace as a string
        rotateFile();
    }

    private static void rotateFile() {
        close();
        File sdcard = Environment.getExternalStorageDirectory();
        File errorFile = new File(sdcard, "error"+sdf.format(new Date())+".log");
        logFile.renameTo(errorFile);
        open();
    }
}
