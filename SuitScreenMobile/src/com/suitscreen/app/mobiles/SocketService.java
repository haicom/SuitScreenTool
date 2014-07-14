package com.suitscreen.app.mobiles;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.suitscreen.app.R;
import com.suitscreen.app.providers.SuitScreen;
import com.suitscreen.app.providers.SuitScreen.InstalledApp;


import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class SocketService extends Service {
    public  static final String TAG = "SocketService";
    
    private static final int        SERVER_PORT = 10086;
    
    private static final String      SERVICE_START = "started";
    private static final String      SERVICE_NO_START = "not start";
    private static final String      SERVICE_EXIT = "exit";
    
    private ServerSocket            mServerSocket;   
    private SysBroadcastReceiver    mSysBroadcastReceiver;
    private Socket                  mSocket;
    private ContentResolver         mContentResolver;
    private Thread                  mSocketAcceptThread;
    
    private static boolean          isActivityStart = false;
    private static String           mPackageName;
    private static String           mClassName;
    
    private volatile boolean         mInterrupted;
    
    private BufferedOutputStream     mOutput;
    private BufferedInputStream      mInput;
    
    public  static final int CMD_GET_MODEL            = 0x01;
    public static final int CMD_CHECK_ACTIVITY_START = 0x04;
    public static final int CMD_GET_PACKAGENAME      = 0x05;
    public static final int CMD_GET_ALL_PACKAGENAME = 0x06;
    public static final int CMD_EXIT                 = 0x09; 
   

//    public static String               imei            = "";

    private static final String[] PROJECTION_INSTALL_APP = new String[] {
        InstalledApp._ID ,            // 0
        InstalledApp.PACKAGE_NAME   // 1
    };


   
    private static final int PACKAGE_NAME_INDEX = 1;

    @Override
    public void onCreate() {
        super.onCreate();

//        TelephonyManager mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        imei = mTm.getDeviceId();
        Log.d(TAG, " SocketService onCreate");
        mSysBroadcastReceiver = new SysBroadcastReceiver();
        mContentResolver = this.getContentResolver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mSysBroadcastReceiver, filter); 
        start();
    }
    
    private synchronized boolean start( ) {
        
        if (mSocketAcceptThread == null) {           
            mSocketAcceptThread = new Thread(TAG) {
                public void run() {                 
                    mInterrupted = false;  
                    
                    try {
                        Log.d(TAG, "Create TCP ServerSocket before mInterrupted = " + mInterrupted);
                        mServerSocket = new ServerSocket(SERVER_PORT);
                        Log.d(TAG, "Create TCP ServerSocket after mInterrupted = " + mInterrupted);
                    } catch (IOException e) {
                        Log.e(TAG, "Error listing on port" + SERVER_PORT);
                        mInterrupted = true;
                    }                     
                    
                    while (!mInterrupted) {                    
                        try {
                            Log.d( TAG, "  mServerSocket before  mSocket  = " + mSocket  + " mInterrupted = " +mInterrupted  );
                            mSocket = mServerSocket.accept();
                            mSocket.setSoTimeout(50000);
                            
                            Log.d( TAG, "  mServerSocket  mSocket  = " + mSocket  + " mInterrupted = " +mInterrupted  );
                            if  ( !mSocket.isConnected() )  {  
                                break;  
                            }  
                            mOutput = new BufferedOutputStream( mSocket.getOutputStream() );
                            mInput = new BufferedInputStream( mSocket.getInputStream() );

                            byte[] cmdBytes = new byte[4];
                            int read = mInput.read( cmdBytes, 0,  cmdBytes.length );
                            
                            Log.d( TAG, "  byteToInt  read  = " + read   );
                            
                            if (read == 0) {
                                continue;
                            }

                            int cmd = Tool.byteToInt(cmdBytes);
                            
                            Log.d(TAG, "  byteToInt  cmd  = " + cmd   );
                            
                            
                            if ( cmd == CMD_GET_MODEL ) {
                                mOutput.write(Build.MODEL.getBytes());
                                mOutput.flush();
                            } else if (cmd == CMD_CHECK_ACTIVITY_START) {
                                if (isActivityStart) {
                                    mOutput.write( SERVICE_START.getBytes() );
                                    mOutput.flush();
                                } else {
                                    mOutput.write( SERVICE_NO_START.getBytes() );
                                    mOutput.flush();
                                }
                            } else if ( cmd == CMD_GET_PACKAGENAME ) {                              
                                Log.d(TAG, " CMD_GET_PACKAGENAME cmd = " + cmd  + " mPackageName = " + mPackageName );                                     
                                /* 将整数转成4字节byte数组 */  
                                byte[] filelength = new byte[4];                                 
                                if  (         ( mPackageName != null )
                                        && ( mPackageName.length() > 0)  ) {
                                    filelength = Tool.intToByte( mPackageName.length() );                            
                                    mOutput.write( filelength );  
                                    mOutput.flush();                             
                                    mOutput.write( mPackageName.getBytes() );
                                    mOutput.flush();
                                }                                
                            } else if ( cmd == CMD_GET_ALL_PACKAGENAME ) {     
                                String packageName = queryAllPackageName();
                                Log.d(TAG, " CMD_GET_ALL_PACKAGENAME cmd = " + cmd  + " packageName = " + packageName );
                                /* 将整数转成4字节byte数组 */  
                                byte[] filelength = new byte[4];  
                               
                                if  (         ( packageName != null )
                                        && ( packageName.length() > 0)  ) {
                                    filelength = Tool.intToByte( packageName.length() );                            
                                    mOutput.write( filelength );  
                                    mOutput.flush();                             
                                    mOutput.write( packageName.getBytes() );
                                    mOutput.flush();
                                }
                             
                            } else if (cmd == CMD_EXIT) {
                                mOutput.write( SERVICE_EXIT.getBytes() );
                                mOutput.flush();
                                mInterrupted = false;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Error accept connection " + e);
                        }
                    }
                }
            };
        }    
            
      
        mSocketAcceptThread.start();
                        
        return true;
    }

    private  class SysBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "MyReceiver onReceive action = " + action);
            if ( action.equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED) )  {
                
                String str = intent.getDataString();
                String[] array = str.split(":");
                mPackageName = array[1];
                Log.d(TAG, " ACTION_PACKAGE_ADDED InstallAppReceiver  mPackageName = " + mPackageName + " str = " + str + " array = " + array.length );
                mClassName = getClassName(mPackageName);
                
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ComponentName componentName = new ComponentName(mPackageName, mClassName);
                intent.setComponent(componentName);
                startActivity(intent);
                
                try {
                    Thread.sleep(1000);
                
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    isActivityStart = false;
                    int index = 0;
                    while (   ( isActivityStart == false )
                           && ( index  < 5 ) ) {
                        
                        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                        
                        if ( mPackageName.equals( cn.getPackageName() ) ) {
                            isActivityStart = true;                            
                        }
                        
                        Thread.sleep(1000);
                        index++;
                    }
                   

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    handleInstallAppRecord();
                }                
            }  else if ( action.equalsIgnoreCase(Intent.ACTION_PACKAGE_REMOVED) ) {
                String str = intent.getDataString();
                String[] array = str.split(":");
                mPackageName = array[1];
                Log.d(TAG, " ACTION_PACKAGE_REMOVED InstallAppReceiver  mPackageName = " + mPackageName + " str = " + str + " array = " + array.length );
                handleRemoveAppRecord();
            }
        }
    }
    

    /**
     * get apk entry activity
     * 
     * @param packagename
     * @return
     */
    private String getClassName(String packagename) {
        Intent localIntent = new Intent(Intent.ACTION_MAIN, null);
        localIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = getPackageManager().queryIntentActivities(
                localIntent, 0);

        String classname = null;
        for ( int index = 0; index < appList.size(); index++ ) {
            ResolveInfo info = appList.get(index);
            Log.d(TAG, "InstallAppReceiver  info.activityInfo.packageName = " + info.activityInfo.packageName
                    + " info.activityInfo.name = " + info.activityInfo.name  );
            String packagestr = info.activityInfo.packageName;
            if ( packagestr.equals(packagename) ) {
                classname = info.activityInfo.name;
            }
        }
        return classname;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stop();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    public synchronized void stop() {
        if (mSocketAcceptThread != null) {
            Log.i(TAG, "stopping Accept Thread");

            mInterrupted = true;
            
            if (mSocket != null ) {
                try {
                    mSocket.close();
                } catch (IOException e) {                  
                    e.printStackTrace();
                }
            }
            
            if (mOutput != null) {
                try {
                    mOutput.close();
                } catch (IOException e) {                    
                    e.printStackTrace();
                }
            }
            
            if (mInput != null) {
                try {
                    mInput.close();
                } catch (IOException e) {                   
                    e.printStackTrace();
                }
            }
            
            if (mServerSocket != null) {
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error close mTcpServerSocket");
                }
            }
            
           
           
           try {
                mSocketAcceptThread.interrupt();
                Log.d(TAG, "waiting for thread to terminate");
                mSocketAcceptThread.join();
                mSocketAcceptThread = null;             
            } catch (InterruptedException e) {
                Log.d(TAG, "Interrupted waiting for Accept Thread to join");
            }
        }
    }
    

    
    /**
     * append new installed application information
     * 
     * @return
     */
    private String handleInstallAppRecord() {
        String result = null;

        String accountClause = InstalledApp.PACKAGE_NAME + " = ?";

        String[] args = new String[] { mPackageName };

        Cursor cursor = mContentResolver.query(InstalledApp.CONTENT_URI,
                PROJECTION_INSTALL_APP, accountClause, args, null);

        if (cursor == null) {
            Log.d(TAG, " testBaseInfoView cursor = " + cursor);
        }

        if  ( cursor != null )  {
            
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            
            if (cursor.getCount() > 0) {
                updateInstallAppRecord(ops, false);
            } else {
                appendInstallAppRecord(ops);
            }

            try {
                getContentResolver().applyBatch(SuitScreen.AUTHORITY, ops);
            } catch (Exception e) {
                result = getResources().getString(R.string.data_error);
                // Log exception
                Log.e(TAG, "Exceptoin encoutered while inserting contact: " + e);
            }
        }

        return result;
    }
    
  
    
    private String handleRemoveAppRecord() {
        String result = null;

        String accountClause = InstalledApp.PACKAGE_NAME + " = ?";

        String[] args = new String[] { mPackageName };

        Cursor cursor = mContentResolver.query(InstalledApp.CONTENT_URI,
                PROJECTION_INSTALL_APP, accountClause, args, null);

        if (cursor == null) {
            Log.d(TAG, " testBaseInfoView cursor = " + cursor);
        }

        if  ( cursor != null )  {
            
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
           
            Log.d(TAG, " handleRemoveAppRecord mPackageName = " + mPackageName + " cursor.getCount() = " + 
                    cursor.getCount() );
            
            if (cursor.getCount() > 0) {
                updateInstallAppRecord(ops, true);
            } 

            try {
                getContentResolver().applyBatch(SuitScreen.AUTHORITY, ops);
            } catch (Exception e) {
                result = getResources().getString(R.string.data_error);
                // Log exception
                Log.e(TAG, "Exceptoin encoutered while inserting contact: " + e);
            }
        }

        return result;
    }

    private void appendInstallAppRecord(ArrayList<ContentProviderOperation> ops) {
        Log.d(TAG, "appendInstallAppRecord mPackageName = " + mPackageName + " mClassName = " + 
    mClassName + " isActivityStart = " +isActivityStart);
        ops.add( ContentProviderOperation.newInsert(InstalledApp.CONTENT_URI)
                .withValue(InstalledApp.PACKAGE_NAME, mPackageName)
                .withValue(InstalledApp.CLASS_NAME, mClassName)
                .withValue(InstalledApp.ACTIVITY_START, isActivityStart == false ? 0 : 1)
                .build() );
    }
    
    private void updateInstallAppRecord(ArrayList<ContentProviderOperation> ops, boolean deleted) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        stringBuilder.append(InstalledApp.PACKAGE_NAME + " = ? ");
       
        int del =  (deleted == false ? 0 : 1);
        int start =  (isActivityStart == false ? 0 : 1);
        
        ops.add( ContentProviderOperation.newUpdate(InstalledApp.CONTENT_URI)
                .withSelection(stringBuilder.toString(),  
                        new String[] { 
                        mPackageName   } )               
                .withValue(InstalledApp.APP_DELETED,  del)         
                .withValue(InstalledApp.ACTIVITY_START,  start)     
                .build());
        
    
    }
    
    private String queryAllPackageName() {
        String IntegrateString = null;
        StringBuilder IntegratePackage = new StringBuilder();
        IntegratePackage.setLength(0);
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        stringBuilder.append(InstalledApp.APP_DELETED + " = ? " );
   

        String[] args = new String[] { String.valueOf( 0 )  };
        
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(InstalledApp.CONTENT_URI,
                    PROJECTION_INSTALL_APP, 
                    stringBuilder.toString(), 
                    new String[] { String.valueOf(0) }, 
                    null);
            
            if (cursor == null) {
                Log.d(TAG, " fillInTotalEnergyInWeek cursor = " + cursor);
            }

            cursor.moveToPosition(-1);
            Log.d(TAG, "queryAllPackageName cursor.getCount()  ＝ " + cursor.getCount() );
            
            while (cursor.moveToNext() ) {
                String packageName = cursor.getString(PACKAGE_NAME_INDEX);
                IntegratePackage.append(packageName);              
                IntegratePackage.append(":");                               
            }
            
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        
        Log.d(TAG, "queryAllPackageName IntegratePackage.toString() ＝ " + IntegratePackage);
        
        if ( IntegratePackage.length() > 0 ) {
            IntegrateString = IntegratePackage.substring( 0, IntegratePackage.length() - 1 );
        }       
        
        Log.d(TAG, "queryAllPackageName IntegrateString ＝ " + IntegrateString );
        
        return IntegrateString;
       
        
        
    }
    
   
}
