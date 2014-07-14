package com.suitscreen.batchinstall;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 定义设备信息类 定义设备的一些基本属性
 * 
 * @author gaoxusong
 * @serial
 */
public class DeviceInfo {

    public static final int STATUS_OFFLINE       = -1; // 设备为离线状态
    public static final int STATUS_PLUGIN        = 0; // 设备插入
    public static final int STATUS_START_INSTALL = 1; // 开始安装
    public static final int STATUS_END_INSTALL   = 2; // 安装结束
    public static final int STATUS_START_REMOVE = 4;//设备卸载完成
    public static final int STATUS_REMOVE_FINISH = 5;//设备卸载完成
    public static final int STATUS_PULLED        = 6; // 设备拔出

    private static final String INIT_PORT_STATUS  = "即将安装...";
    private static final String INIT_DEVICE_MODEL = "正在获取...";
    
    
    public static final String INSTALL_PROGRAM_STATUS = "正在安装守护程序";
    public static final String LUNCHER_PROGRAM_STATUS  = "正在启动守护程序";
    public static final String START_INSTALL_PROGRAM_STATUS = "开始安装应用";
    public static final String INSTALL_PROGRAM_ERRO = "安装应用失败，请重新连接手机";
    public static final String  INSTALL_PROGRAM_BREAK_DOWN = "安装过程中出现故障，请检查并重新插拔手机。";           
    public static final String  FORWARD_PORT_MAP  = "正在映射转发端口";
    
    
    public  static final int CMD_GET_MODEL            = 0x01;
    public static final int CMD_CHECK_ACTIVITY_START = 0x04;
    public static final int CMD_GET_PACKAGENAME      = 0x05;
    public static final int CMD_GET_ALL_PACKAGENAME = 0x06;
    public static final int CMD_EXIT                 = 0x09; 
    
    public   int MAX_INSTALL_NUMBER = 10;
    
    public DeviceInfo(String serial, int stat, int port) {
        this.mDevModel = INIT_DEVICE_MODEL;
        this.mDevSerial = serial;       
        this.mMapPort = port;
        this.mStatus = stat;        
        this.mStatusString = INIT_PORT_STATUS;
        this.mQueue = new  ArrayBlockingQueue<String>(MAX_INSTALL_NUMBER); 
    }

   

    public DeviceInfo(String serial, int port) {
        this.mDevModel = INIT_DEVICE_MODEL;
        this.mDevSerial = serial;       
        this.mMapPort = port;
        this.mStatus = 0;      
        this.mStatusString = INIT_PORT_STATUS;
    }
    
    
    public String getApkPath() {
        return mApkPath;
    }

    public void setApkPath(String apkPath) {
        this.mApkPath = apkPath;
    }

    public synchronized int getStatus() {
        return mStatus;
    }

    public synchronized void setStatus(int status) {
        this.mStatus = status;
    }

    public String getDevModel() {
        return mDevModel;
    }



    public void setDevModel(String devModel) {
        this.mDevModel = devModel;
    }
    
    public String getDevSerial() {
        return mDevSerial;
    }

    public void setDevSerial(String devSerial) {
        this.mDevSerial = devSerial;
    }
    
    public int getMapPort() {
        return mMapPort;
    }

    public void setmMapPort(int mapPort) {
        this.mMapPort = mapPort;
    }

    public String getStatusString() {
        return mStatusString;
    }

    public void setStatusString(String statusString) {
        this.mStatusString = statusString;
    }

    public synchronized ArrayBlockingQueue<String> getQueue() {
        return mQueue;
    }
    
    public synchronized void setQueue(ArrayBlockingQueue<String> queue) {
        this.mQueue = queue;
    }

    private  int          mMapPort;         // adb映射到设备的socket端口  
    private String     mStatusString;   // 状态描述字符串
    private String     mDevSerial;        // 设备序列号
    private String     mDevModel;      // 设备型号
    private String     mApkPath;        //apk安装路径
    private volatile int          mStatus;            // 设备在操作过程中的状态：-1-掉线；0-插入；1-开始安装；2-安装完毕；3-设备被拔出
    private ArrayBlockingQueue<String>  mQueue;
}
