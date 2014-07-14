package com.suitscreen.batchinstall;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.suitscreen.batchinstall.ui.MainBash;


public class Function {

    /**
     * open Screenshot Dir
     * 
     * @param folder
     */
    public static void open_directory(String folder) {

        File file = new File(folder);
        if (!file.exists()) {
            return;
        }

        Runtime runtime = null;

        try {
            String SysType = System.getProperties().getProperty("os.name");
            if (SysType.startsWith("win") || SysType.startsWith("Win")) {
                String[] cmd = new String[5];
                cmd[0] = "cmd";
                cmd[1] = "/c";
                cmd[2] = "start";
                cmd[3] = " ";
                cmd[4] = folder;
                Runtime.getRuntime().exec(cmd);
            } else {
                runtime = Runtime.getRuntime();
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

    /**
     * 执行dos命令，返回命令输出的字符串
     * 
     * @param cmd
     *            , 要执行的命令
     * @return 命令执行的返回字符串
     */
    public static String exec(String cmd) {
        StringBuffer sb = new StringBuffer();
        Process process = null;
        InputStreamReader in = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec(cmd); // 启动另一个进程来执行命令
            in = new InputStreamReader(process.getInputStream());
            reader = new BufferedReader(in);

            String line = null;
            while ( ( line = reader.readLine() ) != null) {
                sb.append(line).append("\r\n");               
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                try {
                    reader.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                process.destroy();
            }
        }
        return sb.toString();
    }

    /**
     * 屏幕截图
     * 
     * @param devInfo
     */
    public static String screenCap(DeviceInfo devInfo) {

        String fileDir = Config.getPicturePath()  + File.separator + devInfo.getDevModel();
        long time = System.currentTimeMillis();
        // 开始截图
        String cmd = MainBash.ADB_PATH  + "adb -s " + devInfo.getDevSerial()
                + " shell /system/bin/screencap -p " + "/sdcard/" + time
                + ".png";
        String result = exec(cmd);
        
        if ( result.length() == 0 ) {
            cmd = MainBash.ADB_PATH  + "adb -s " + devInfo.getDevSerial()
                    + " shell /system/bin/screencapture -p " + "/sdcard/" + time
                    + ".png";
            exec(cmd);
        }
        
       
        
        System.out.println("screenCap result = " + result);
        // 传送到pc端
        cmd = MainBash.ADB_PATH  + "adb -s " + devInfo.getDevSerial() + " pull /sdcard/" + time + ".png "
                + fileDir;
        exec(cmd);

        // 删除手机上的截图文件
        cmd = MainBash.ADB_PATH  + "adb -s " + devInfo.getDevSerial() + " shell rm -r /sdcard/" + time
                + ".png";
        exec(cmd);

        String imageName = fileDir + File.separator + time + ".png ";
        return imageName;
    }
}
