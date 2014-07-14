package com.suitscreen.batchinstall;

public class ApkInstallInfo {
    public static final int INSTALL_PREPARE      =  -1; //设备为安装准备状态
    public static final int INSTALL_PROGRESS   =  0;  //设备为安装进行中
    public static final int INSTALL_FINISH          = 1;  //设备为安装完成时
    public static final int INSTALL_ERROR           = 2;  //设备为安装结束
    public static final int REMOVE_FINISH         = 3;  //设备删除完成
  
    public ApkInstallInfo() {
        mInstallStatus = INSTALL_PREPARE;
    }
    
    public String getApkName() {
        return mApkName;
    }
    public void setApkName(String apkName) {
        this.mApkName = apkName;
    }
    public String getApkPath() {
        return mApkPath;
    }
    public void setApkPath(String apkPath) {
        this.mApkPath = apkPath;
    }
    public synchronized  int getInstallStatus() {
        return mInstallStatus;
    }
    public synchronized void setInstallStatus(int installStatus) {
        this.mInstallStatus = installStatus;
    }
    private String   mApkName;     //apk姓名
    private String   mApkPath;     //apk安装路径
    private volatile int      mInstallStatus; //apk安装结果
 
}
