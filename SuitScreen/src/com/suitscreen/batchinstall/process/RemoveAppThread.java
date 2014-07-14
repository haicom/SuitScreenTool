package com.suitscreen.batchinstall.process;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.suitscreen.batchinstall.ApkInstallBuffer;
import com.suitscreen.batchinstall.DeviceInfo;
import com.suitscreen.batchinstall.Function;
import com.suitscreen.batchinstall.Tool;
import com.suitscreen.batchinstall.ui.MainBash;

public class RemoveAppThread implements Runnable  {
    private Socket                    mSocket; // Socket客户端
    private DeviceInfo                mDevInfo; // 设备信息
    private static FoucsHandle mUpdateDevice;  
    private static ApkInstallBuffer mInstallBuffer;
    

    
    public RemoveAppThread( DeviceInfo devInfo) {
        mDevInfo = devInfo;           
        mInstallBuffer = ApkInstallBuffer.getInstance();
        mUpdateDevice = FoucsHandle.getInstance();
    }
    
    @Override
    public void run() {
        
        String cmd = null;
        
        mDevInfo.setStatusString(DeviceInfo.INSTALL_PROGRAM_STATUS);    
   
        try {
            
          while ( mDevInfo.getQueue().size() > 0) {
                String removeApp = mDevInfo.getQueue().take();
                System.out.println( " RemoveAppThread removeApp = " + removeApp );
                
                cmd =  MainBash.ADB_PATH  + "adb  -s  " + mDevInfo.getDevSerial()
                      + "   uninstall  " + removeApp ;                
                System.out.println( " RemoveAppThread cmd = " + cmd );
                String result = Function.exec(cmd);
                
                  System.out.println("RemoveAppThread result = " + result 
                  + " cmd = " + cmd  );
            }
          
        } catch (InterruptedException e) {
            
            e.printStackTrace();
        }
        
        
      
//        cmd = MainBash.ADB_PATH + "adb -s " + mDevInfo.getDevSerial()
//                + " shell am startservice --user 0 -a "
//                + " com.suitscreen.app.startservice ";
//
//        // 判断使用--user 0是否出错
//        String result = Function.exec(cmd);
//        int res = result.indexOf("Error: Unknown option: --user");
//        System.out.println("InstallAppThread result = " + result 
//                + " res = " + res + " devInfo.devSerial = " + mDevInfo.getDevSerial()
//                +  "    " + mDevInfo.getStatusString()  );
//        
//        if (res != -1) {
//            cmd =  MainBash.ADB_PATH + "adb -s " + mDevInfo.getDevSerial()
//                    + " shell am startservice -a "
//                    + " com.suitscreen.app.startservice ";
//            
//            result = Function.exec(cmd);
//            System.out.println("InstallAppThread Error: Unknown option: --user result = " + result 
//                    + " res = " + res + " devInfo.devSerial = " + mDevInfo.getDevSerial()
//                    +  "    " + mDevInfo.getStatusString() );
//        }
        
//        cmd =  MainBash.ADB_PATH + "adb -s " + mDevInfo.getDevSerial()
//                + " shell am startservice -a "
//                + " com.suitscreen.app.startservice ";
//        
//        String   result = Function.exec(cmd);
//        System.out.println(" RemoveAppThread  result = " + result 
//                +  " devInfo.devSerial = " + mDevInfo.getDevSerial()
//                +  "    " + mDevInfo.getStatusString() );
//        mDevInfo.setStatusString(DeviceInfo.FORWARD_PORT_MAP);    
//    
//        cmd =  MainBash.ADB_PATH + "adb -s " 
//              + mDevInfo.getDevSerial() + " forward tcp:"
//              + mDevInfo.getMapPort() + " tcp:10086";
//        
//           result = Function.exec(cmd);
//         System.out.println( " RemoveAppThread mDevInfo.getDevSerial() = " + mDevInfo.getDevSerial() 
//                 +  "  mDevInfo.getMapPort()  = " +  mDevInfo.getMapPort()  );
//         
//        try {
//            mSocket = new Socket(InetAddress.getByName("127.0.0.1"),
//            mDevInfo.getMapPort() );       
//            BufferedOutputStream out = new BufferedOutputStream( mSocket.getOutputStream()  );        
//            BufferedInputStream     in = new BufferedInputStream( mSocket.getInputStream() );     
//        
//            
//            // 发送指令
//            /* 将整数转成4字节byte数组 */  
//            byte[] cmdLength = new byte[4];             
//            cmdLength = Tool.intToByte( mDevInfo.CMD_GET_PACKAGENAME );        
//            out.write( cmdLength );  
//            out.flush();  
//            
//            byte[] filelength = new byte[4];
//            String strFormsocket = readFromSocket(in, filelength); 
//            System.out.println( " RemoveAppThread strFormsocket = " + strFormsocket );
//            
//            if  ( strFormsocket != null ) {
//                String[] removeApps = strFormsocket.split(":");
//                
//                for  ( String removeApp:removeApps ) {
//                    System.out.println( " RemoveAppThread removeApp = " + removeApp );
//                    cmd =  MainBash.ADB_PATH  + "adb  -s  " + mDevInfo.getDevSerial()
//                            + "   uninstall  " + removeApp ;                
//                    System.out.println( " RemoveAppThread cmd = " + cmd );
//                    result = Function.exec(cmd);
//                }
//            }
//           
//            mDevInfo.setStatus( DeviceInfo.STATUS_REMOVE_FINISH );
//            mUpdateDevice.handleInfo();
//        } catch (UnknownHostException e) {
//            
//            e.printStackTrace();
//        } catch (IOException e) {
//            
//            e.printStackTrace();
//        }
      //  String packageName = synch2device(CMD_GET_PACKAGENAME, null);
        
       //System.out.println(" RemoveAppThread packageName = " + packageName);
        
        
    }    
   
    /* 从InputStream流中读数据 */  
    public static String readFromSocket( InputStream in, byte[] filelength ) {  
        byte[] filebytes = null;// 文件数据  
        String msg = null;  
        try {
          // 读文件长度  
            in.read(filelength);
            int filelen = Tool.byteToInt(filelength);// 文件长度从4字节byte[]转成Int  
            filebytes = new byte[filelen];  
            int pos = 0;  
            int rcvLen = 0;  
            while ( ( rcvLen = in.read( filebytes, pos, filelen - pos ) ) > 0 ) {  
                pos += rcvLen;  
            }  
            
            if ( filelen > 0 ) {
                msg = new String(filebytes, 0, filelen, "utf-8");  
            }
           
        } catch (IOException e1) {
            
            e1.printStackTrace();
        }
     
        return msg;  
    }  
}
