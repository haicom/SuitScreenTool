package com.suitscreen.batchinstall.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


import com.suitscreen.batchinstall.ApkInstallBuffer;
import com.suitscreen.batchinstall.ApkInstallInfo;
import com.suitscreen.batchinstall.Config;
import com.suitscreen.batchinstall.DeviceBuffer;
import com.suitscreen.batchinstall.DeviceInfo;
import com.suitscreen.batchinstall.process.FoucsHandle;

public class MainBash {
    public  static final String ADB_PATH = "/work/adb/";
    private static volatile int   mPort = 10081;  
    private static DeviceBuffer mDevBuffer;
    private static ApkInstallBuffer mInstallBuffer;
    private static FoucsHandle mUpdateDevice;  
    private static String picturePathName = "suitscreen";
    /**
     * @param args
     */
    public static void main(String[] args) {
        //String pictureDir = null;
        String installPath = null;
        
        if (args.length == 0) {
            System.out.println("请输入安装包路径");
            return;
        }
        
        mDevBuffer = DeviceBuffer.getInstance();
        mInstallBuffer = ApkInstallBuffer.getInstance();
        mUpdateDevice = FoucsHandle.getInstance();
        
        detectDeviceNo();
        
        DeviceInfo deviceInfo;
        
        for (int index = 0; index < mDevBuffer.size(); index++) {
            deviceInfo = mDevBuffer.get(index);
            System.out.println("deviceInfo.devSerial = " + deviceInfo.getDevSerial()
                    + " deviceInfo.status = " + deviceInfo.getStatus());
        }
        
        for (int index = 0; index < args.length; index++) {
            ApkInstallInfo installInfo = new ApkInstallInfo();
            installPath = args[index];
            
            if (!installPath.endsWith(".apk") ) {
                System.out.println("请选择android安装包");
                continue;
            }
            
            File installFile = new File(installPath);
            
            if ( !installFile.exists() ) {
                System.out.println("没有此文件");
                continue;
            }
            
            
            installInfo.setApkName( installFile.getName() );
            installInfo.setApkPath( installFile.getPath() );   
            installInfo.setInstallStatus(ApkInstallInfo.INSTALL_PREPARE);
            mInstallBuffer.add(installInfo);
            System.out.println(" args[index] = " + args[index] + " installFile.getName() = " 
            + installFile.getName() + " installFile.getPath() = " + installFile.getPath()); 
        } 
        
        for (int index = 0; index < mDevBuffer.size(); index++) {
            deviceInfo = mDevBuffer.get(index);
            System.out.println("deviceInfo.devSerial = " + deviceInfo.getDevSerial()
                    + " deviceInfo.status = " + deviceInfo.getStatus() );
        }
        
      //  settingInstallPath();
        
    
        

      
//        mDetectDeviceThread = new DetectDeviceThread();
//        mDetectDeviceThread.start();
//         detectDeviceNo();
          mUpdateDevice.handleInfo();
      
    }
    
    
    /**
     * 执行adb命令，检测已连接的设备
     * 
     * @param cmd
     *            , 要执行的命令
     * @return 命令执行的返回字符串
     */
    private static  void detectDeviceNo() {     
        
        Process process = null;
        InputStreamReader in = null;
        BufferedReader reader = null;
        
        try { 
            
            process = Runtime.getRuntime().exec( new String[] { MainBash.ADB_PATH + "adb", "devices"}); // 启动另一个进程来执行命令
            in = new InputStreamReader( process.getInputStream() );
            reader = new BufferedReader(in);
          
            String line = null;
            
            while (  ( line = reader.readLine()  )  != null  ) {
                
                System.out.println(" MainBash line = " + line);
                
                if ( line.contains("List of devices") ) {
                }
                // 返回“ADB server didn't ACK”，则认为adb服务已经崩溃
                else if (line.contains("ADB server didn't ACK")) {

                } else {
                    
                    int index = line.indexOf("device"); // 判断连线的手机
                    if ( mPort++ > 19999 )  {
                        mPort = 10081;
                    }
                    
                    if (index > 0) {
                        String serial = line.substring(0, index - 1);
                        mDevBuffer.add(new DeviceInfo(serial, 
                                                      DeviceInfo.STATUS_PLUGIN, 
                                                      mPort) );                      
                    } else {
                        index = line.indexOf("offline");
                        if (index > 0) {
                            String serial = line.substring(0, index - 1);
                            mDevBuffer.add(new DeviceInfo(serial, 
                                    DeviceInfo.STATUS_OFFLINE, 
                                    mPort) );    
                        }
                    }
                }
            }
            // 刷新设备列表
         
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
    }
    
    private static void settingInstallPath() {
        String SysType = System.getProperties().getProperty(
                "os.name");
        
        if (SysType.startsWith("win") || SysType.startsWith("Win")) {
            Config.setPicturePath("C:" + File.separator + picturePathName );

            // 安装adb， 将adb相关文件copy到system32文件夹
            String system32 = System.getenv("SystemRoot")
                    + File.separator + "System32" + File.separator;
            try {
                
                File adbFile = new File(system32 + "adb.exe");
                if (!adbFile.exists()) {
                    FileInputStream input = new FileInputStream(
                            "adb/adb.exe");
                    FileOutputStream output = new FileOutputStream(
                            system32 + "adb.exe");
                    int in = input.read();
                    while (in != -1) {
                        output.write(in);
                        in = input.read();
                    }
                    input.close();
                    output.close();
                }

                File AdbWinApi = new File(system32
                        + "AdbWinApi.dll");
                
                if ( !AdbWinApi.exists() ) {
                    FileInputStream input = new FileInputStream(
                            "adb/AdbWinApi.dll");
                    FileOutputStream output = new FileOutputStream(
                            system32 + "AdbWinApi.dll");
                    int in = input.read();
                    while (in != -1) {
                        output.write(in);
                        in = input.read();
                    }
                    input.close();
                    output.close();
                }

                File AdbWinUsbApi = new File(system32
                        + "AdbWinUsbApi.dll");
                if (!AdbWinUsbApi.exists()) {
                    FileInputStream input = new FileInputStream(
                            "adb/AdbWinUsbApi.dll");
                    FileOutputStream output = new FileOutputStream(
                            system32 + "AdbWinUsbApi.dll");
                    int in = input.read();
                    while (in != -1) {
                        output.write(in);
                        in = input.read();
                    }
                    input.close();
                    output.close();
                }

            } catch (IOException e) {
                System.out.println(e.toString());
            }
        } else {
            String userHome = System.getProperties().getProperty(
                    "user.home");
            Config.setPicturePath(userHome + File.separator + picturePathName );
        }

        File picturefile = new File( Config.getPicturePath() );
        
        if ( !picturefile.exists() ) {
            picturefile.mkdirs();
        }
        
        System.out.println("MainBash fileDir = " + Config.getPicturePath() );
    }

}
