package com.suitscreen.batchinstall;


import java.util.ArrayList;

public class ApkInstallBuffer {
    
    public static ApkInstallBuffer getInstance() {

        if (mInstance == null) {
            mInstance = new ApkInstallBuffer();
        }

        return mInstance;
    }

    private ApkInstallBuffer() {
        mList = new ArrayList<ApkInstallInfo>( );
    }
    
    public synchronized boolean add(ApkInstallInfo installInfo) {

        for (int index = 0; index < mList.size(); index++) {
            ApkInstallInfo info = mList.get(index);
            if ( info.getApkName().equals( installInfo.getApkName() ) ) {
                return false;
            }
        }
        
        System.out.println(" ApkInstallBuffer installInfo = " + installInfo.getApkName() ); 
        mList.add(installInfo);
        return true;
    }
      

    public synchronized ApkInstallInfo get(int index) {
        return mList.get(index);
    }

    public synchronized void remove(ApkInstallInfo installInfo) {
        mList.remove(installInfo);
    }

    public synchronized void remove(int index) {
        mList.remove(index);
    }

    public synchronized void clear() {
        mList.clear();
    }

    public synchronized int size() {
        return mList.size();
    }
    
    private static  ApkInstallBuffer mInstance;
    private ArrayList<ApkInstallInfo>  mList;

}
