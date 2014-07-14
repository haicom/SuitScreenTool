package com.suitscreen.batchinstall.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.suitscreen.batchinstall.ApkInstallBuffer;
import com.suitscreen.batchinstall.ApkInstallInfo;
import com.suitscreen.batchinstall.DeviceBuffer;
import com.suitscreen.batchinstall.DeviceInfo;



public class FoucsHandle {
//    private  ExecutorService pool;
  
   
    private static DeviceBuffer mDevBuffer;
    private static ApkInstallBuffer mInstallBuffer;
    private static  FoucsHandle mInstance;
   
    private ExecutorService mExec;
    private DeviceInfo mDeviceInfo;
//    private boolean mDone;  
    
    public static FoucsHandle getInstance() {

        if (mInstance == null) {
            mInstance = new FoucsHandle();
        }

        return mInstance;
    }
    
    public FoucsHandle( ) {        
        mDevBuffer = DeviceBuffer.getInstance();
        mInstallBuffer = ApkInstallBuffer.getInstance();
        mExec = Executors.newCachedThreadPool();
//        pool      = Executors.newCachedThreadPool();
//        mDone = false;
    }
    
  
    public void handleInfo() {
        boolean installDevice =  findInstalledDevice();
        
        while (  installDevice  ) {
            System.out.println(" UpdateDeviceThread pool.execute before "  );           
            InstallAppThread installApp = new InstallAppThread(mDeviceInfo);         
            mExec.execute(installApp);          
            System.out.println(" UpdateDeviceThread pool.execute after " );      
            installDevice =  findInstalledDevice();
        }  
        
        if ( installDevice == false ) {
            boolean removeDevice =  findRemoveDevice() ;
            if ( true == removeDevice ) {
                RemoveAppThread removeApp = new RemoveAppThread(mDeviceInfo);
                mExec.execute(removeApp);
            }
        }
    }
    
    
    
    private synchronized boolean findInstalledDevice() {
        
        boolean deviceStatus = false;
       
        ApkInstallInfo apkInstallInfo;
        
        for (int index = 0; index < mDevBuffer.size(); index++) {
            
            mDeviceInfo = mDevBuffer.get(index);
            System.out.println(" findInstalledDevice index = " + index + " mDeviceInfo.getStatus()  = " + mDeviceInfo.getStatus() 
                    + "  mDeviceInfo.getApkPath() = " + mDeviceInfo.getApkPath() );
            
            if (  ( (mDeviceInfo.getStatus() == DeviceInfo.STATUS_PLUGIN)
               || (mDeviceInfo.getStatus() == DeviceInfo.STATUS_END_INSTALL) 
               || (mDeviceInfo.getStatus() == DeviceInfo.STATUS_REMOVE_FINISH) ) 
               && mDeviceInfo.getQueue().size() <= mDeviceInfo.MAX_INSTALL_NUMBER ) {
                
                for ( int installIndex = 0; installIndex < mInstallBuffer.size(); installIndex++ ) {
                    
                    apkInstallInfo = mInstallBuffer.get(installIndex);
                    System.out.println(" findInstalledDevice installIndex = " + installIndex + 
                            " apkInstallInfo.getInstallStatus() = " + apkInstallInfo.getInstallStatus() );
                    
                    if (  ( apkInstallInfo.getInstallStatus() == ApkInstallInfo.INSTALL_PREPARE ) 
                       || ( apkInstallInfo.getInstallStatus() == ApkInstallInfo.INSTALL_ERROR ) ) { 
                        
                        if (apkInstallInfo.getInstallStatus() == ApkInstallInfo.INSTALL_ERROR) {
                            String apkPath = mDeviceInfo.getApkPath();
                            System.out.println(" findInstalledDevice apkPath  = " + apkPath + " apkInstallInfo.getApkPath() = " + apkInstallInfo.getApkPath());
                            if (  (apkPath != null)
                                && (apkPath.length() > 0 ) ) {
                                if ( apkPath.equals( apkInstallInfo.getApkPath() ) ) {
                                    continue;
                                }
                            }
                        }   
                        
                        mDeviceInfo.setApkPath( apkInstallInfo.getApkPath() );
                        mDeviceInfo.setStatus(DeviceInfo.STATUS_START_INSTALL); 
                        apkInstallInfo.setInstallStatus(ApkInstallInfo.INSTALL_PROGRESS);
                        System.out.println(" findInstalledDevice ApkInstallInfo.INSTALL_PREPARE deviceInfo.devSerial = " + mDeviceInfo.getDevSerial()
                                + " deviceInfo.status = " + mDeviceInfo.getStatus() 
                                + " mDeviceInfo.getApkPath() = " + mDeviceInfo.getApkPath()
                                + " installIndex = " + installIndex  + " mInstallBuffer.size() = " + mInstallBuffer.size() );
                        deviceStatus = true;
                        return deviceStatus;
                    }                    
                }
            }            
        }
        
        System.out.println(" UpdateDeviceThread deviceStatus = " + deviceStatus);
        return deviceStatus;
    }
    
  private synchronized boolean findRemoveDevice() {
        boolean deviceStatus = false;      
        for (int index = 0; index < mDevBuffer.size(); index++) {
            
            mDeviceInfo = mDevBuffer.get(index);
            System.out.println(" findRemoveDevice index = " + index + " mDeviceInfo.getStatus()  = " + mDeviceInfo.getStatus() 
                    + "  mDeviceInfo.getApkPath() = " + mDeviceInfo.getApkPath() );
            
            if (  mDeviceInfo.getStatus() == DeviceInfo.STATUS_END_INSTALL ) {
                deviceStatus = true;
                mDeviceInfo.setStatus(DeviceInfo.STATUS_START_REMOVE);
                return deviceStatus; 
            }            
        }
        
        System.out.println(" UpdateDeviceThread deviceStatus = " + deviceStatus);
        return deviceStatus;
    }
    
//    public synchronized void stop() {
//        mDone = true;
//    }
//    
   

}
