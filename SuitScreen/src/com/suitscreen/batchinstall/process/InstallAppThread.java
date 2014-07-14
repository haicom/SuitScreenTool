package com.suitscreen.batchinstall.process;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.suitscreen.batchinstall.ApkInstallBuffer;
import com.suitscreen.batchinstall.ApkInstallInfo;
import com.suitscreen.batchinstall.DeviceInfo;
import com.suitscreen.batchinstall.Function;
import com.suitscreen.batchinstall.Tool;
import com.suitscreen.batchinstall.ui.MainBash;


public class InstallAppThread implements Runnable {
    private Socket     mSocket; // Socket客户端
    private DeviceInfo mDevInfo; // 设备信息
    private static FoucsHandle mUpdateDevice;  
    private static ApkInstallBuffer mInstallBuffer;
    
    
    public InstallAppThread( DeviceInfo devInfo) {
        mDevInfo = devInfo;           
        mInstallBuffer = ApkInstallBuffer.getInstance();
        mUpdateDevice = FoucsHandle.getInstance();
    }

    public  void run() {
      
        // 先执行命令
        BufferedOutputStream out = null;
        BufferedInputStream     in = null;
        try {
            String cmd = null;
            
            mDevInfo.setStatusString(DeviceInfo.INSTALL_PROGRAM_STATUS);    
         
//            cmd = MainBash.ADB_PATH + "adb -s " + devInfo.getDevSerial()
//                    + " install -r SuitScreen_Android.apk";
////            // 判断是否安装成功
////            String result = Function.exec(cmd);
//            
//           
//            int res = result.indexOf("Failure");
//            System.out.println("InstallAppThread result = " + result 
//                    + " res = " + res + " devInfo.devSerial = " + devInfo.getDevSerial() 
//                    +  "   " + devInfo.statusString );
//            
//            if (res != -1) {
//                cmd = MainBash.ADB_PATH + "adb -s " + devInfo.getDevSerial()
//                        + " uninstall com.suitscreen.suitscreen";
//                Function.exec(cmd);
//                cmd = MainBash.ADB_PATH + "adb -s " + devInfo.getDevSerial()
//                        + " install -r SuitScreen_Android.apk";
//                Function.exec(cmd);
//            }          
            mDevInfo.setStatusString(DeviceInfo.LUNCHER_PROGRAM_STATUS);           
            cmd = MainBash.ADB_PATH + "adb -s " + mDevInfo.getDevSerial()
                    + " shell am startservice --user 0 -a "
                    + " com.suitscreen.app.startservice ";

            // 判断使用--user 0是否出错
            String result = Function.exec(cmd);
            int res = result.indexOf("Error: Unknown option: --user");
            System.out.println("InstallAppThread result = " + result 
                    + " res = " + res + " devInfo.devSerial = " + mDevInfo.getDevSerial()
                    +  "    " + mDevInfo.getStatusString() );
            
            if (res != -1) {
                cmd =  MainBash.ADB_PATH + "adb -s " + mDevInfo.getDevSerial()
                        + " shell am startservice -a "
                        + "com.suitscreen.app.startservice ";
                result = Function.exec(cmd);
                System.out.println("InstallAppThread Error: Unknown option: --user result = " + result 
                        + " res = " + res + " devInfo.devSerial = " + mDevInfo.getDevSerial()
                        +  "    " + mDevInfo.getStatusString() );
            }
            
//            cmd =  MainBash.ADB_PATH + "adb -s " + mDevInfo.getDevSerial()
//                    + " shell am startservice -a "
//                    + " com.suitscreen.app.startservice ";
            
           // String   result = Function.exec(cmd);
//            System.out.println(" InstallAppThread  result = " + result 
//                    +  " devInfo.devSerial = " + mDevInfo.getDevSerial()
//                    +  "    " + mDevInfo.getStatusString() );
       
              
          cmd = MainBash.ADB_PATH + "adb -s " + mDevInfo.getDevSerial()
                  + " shell am broadcast -a NotifyServiceStop";
           result = Function.exec(cmd);
          System.out.println( " InstallAppThread NotifyServiceStop " );    
          mDevInfo.setStatusString(DeviceInfo.FORWARD_PORT_MAP);    
          cmd =  MainBash.ADB_PATH + "adb -s " 
                  + mDevInfo.getDevSerial() + " forward tcp:"
                  + mDevInfo.getMapPort() + " tcp:10086";
          
          result = Function.exec(cmd);
          System.out.println( " InstallAppThread mDevInfo.getDevSerial() = " + mDevInfo.getDevSerial() 
                  +  "  mDevInfo.getMapPort()  = " +  mDevInfo.getMapPort()  );    
          
          cmd = MainBash.ADB_PATH + "adb  -s  " + mDevInfo.getDevSerial()
                  + " shell am broadcast -a NotifyServiceStart";
         result =  Function.exec(cmd);
         System.out.println( " InstallAppThread NotifyServiceStart " );    
          
          
//
//         
//
//            socket = new Socket(InetAddress.getByName("127.0.0.1"),
//                    devInfo.mapPort);
//            devInfo.statusString = "正在和手机客户端通讯";

//            // 发送获取手机型号的指令
//            // 手机型号有空格则用“—”代替
//            devInfo.setDevModel( synch2device(0x01, null).trim() );
//            if (   (devInfo.getDevModel() != null ) 
//                && (devInfo.getDevModel().length() != 0 ) ) {
//                devInfo.setDevModel( devInfo.getDevModel().replace(" ", "-") );
//            }
            
//            System.out.println("devInfo.devModel = " + devInfo.devModel );
//            // 发送获取手机IMEI号的指令
//            devInfo.imei = synch2device(0x02, null);
//            System.out.println(" devInfo.imei = " + devInfo.imei);
//            
//            String displayMetrics = synch2device(0x03, null);
//            
//            if (    (displayMetrics != null ) 
//                 && (displayMetrics.length() != 0 ) ) {
//                String[] wh = displayMetrics.split("-");
//              
//                devInfo.screenWidth = Integer.parseInt(wh[0]);            
//                devInfo.screenHeight = Integer.parseInt(wh[1]);
//            }
//           
//            System.out.println("displayMetrics = " + displayMetrics );
            
//            String fileDir;
//            // pc端创建存储文件夹
//            if ( devInfo.getDevModel().length() > 0) {
//                fileDir  = Config.getPicturePath() + File.separator
//                        + devInfo.getDevModel();
//            } else {
//                fileDir = Config.getPicturePath() + File.separator
//                        + "devModel";
//            }
//                        
//          
//            File filePath = new File(fileDir);
//            
//            System.out.println("InstallAppThread fileDir = " + fileDir );
//            
//            devInfo.statusString = "创建文件夹";
//            if (!filePath.exists() ) {
//                filePath.mkdirs();
//            }

           
            // 通知终端开始安装
            mDevInfo.setStatusString(DeviceInfo.START_INSTALL_PROGRAM_STATUS );            
            cmd = MainBash.ADB_PATH + "adb -s " + mDevInfo.getDevSerial() + " install -r "
                    + mDevInfo.getApkPath();
            
            String installResult = Function.exec(cmd);
            int install_res = installResult.indexOf("Failure");
            
            System.out.println("InstallAppThread installResult = " + installResult 
                    + " install_res = " + install_res + " devInfo.devSerial = " + mDevInfo.getDevSerial()
                    +  "    " + mDevInfo.getStatusString() );
            
            if  ( install_res != -1 ) {
                mDevInfo.setStatusString(DeviceInfo.INSTALL_PROGRAM_ERRO);               
                changeApkInstallStatus( ApkInstallInfo.INSTALL_ERROR );
                mDevInfo.setStatus( DeviceInfo.STATUS_END_INSTALL );
                mUpdateDevice.handleInfo();
                return;
            }
            
            
//            int index = 0;
//            while ( index < 5) {
//                devInfo.statusString = "正在截图";
//                Function.screenCap(devInfo);
//                Thread.sleep(1000);
//                index++;
//            }

//             check activity started
//            for (;;) {
//                if ( "started".equals(synch2device(0x04, null).trim() ) ) {              
//                    devInfo.statusString = "开始截图";
//                    int i = 0;
//                    while (i < 5) {
//                        devInfo.statusString = "正在截图";
//                        Function.screenCap(devInfo);
//                        Thread.sleep(1000);
//                        i++;
//                    }
//
//                    devInfo.statusString = "截图完毕";
//                    break;
//                }
//            }

            // // 操作完成后删除守护软件
            // cmd = "adb -s " + devInfo.devSerial +
            // " uninstall com.suitscreen.suitscreen";
            // Function.exec(cmd);

            // 将状态设置为完成安装      
                mSocket = new Socket(InetAddress.getByName("127.0.0.1"),
                mDevInfo.getMapPort() );       
                out = new BufferedOutputStream( mSocket.getOutputStream()  );        
                in = new BufferedInputStream( mSocket.getInputStream() );     
               
                // 发送指令
                /* 将整数转成4字节byte数组 */  
                byte[] cmdLength = new byte[4];             
                cmdLength = Tool.intToByte( mDevInfo.CMD_GET_PACKAGENAME );        
                out.write( cmdLength );  
                out.flush();  
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {                  
                    e1.printStackTrace();
                }
                byte[] filelength = new byte[4];
                String strFormsocket = readFromSocket(in, filelength); 
                System.out.println( " RemoveAppThread strFormsocket = " + strFormsocket );
                
                if  ( strFormsocket != null ) {
                    try {
                        mDevInfo.getQueue().put(strFormsocket);
                    } catch (InterruptedException e) {                    
                        e.printStackTrace();
                        mDevInfo.setStatusString(DeviceInfo.INSTALL_PROGRAM_BREAK_DOWN);
                    }                   
                }
               
                changeApkInstallStatus( ApkInstallInfo.INSTALL_FINISH );
                mDevInfo.setStatus( DeviceInfo.STATUS_END_INSTALL );
                mUpdateDevice.handleInfo();                
            } catch (UnknownHostException e) {                
                e.printStackTrace();
                mDevInfo.setStatusString(DeviceInfo.INSTALL_PROGRAM_BREAK_DOWN);
            } catch (IOException e) {                
                e.printStackTrace();
                mDevInfo.setStatusString(DeviceInfo.INSTALL_PROGRAM_BREAK_DOWN);
            } finally {  
                try {  
                    if ( out != null ) {
                        out.close();
                    }
                    
                    if ( in != null ) {
                        in.close();
                    }
                    if  ( mSocket != null ) {  
                        mSocket.close();  
                        System.out.println("socket.close()");  
                    }  
                } catch (IOException e) {  
                    System.out.println("TCP 5555" + "ERROR:" + e.toString());  
                }  
            }  
          

    }
    
//    // 发送获取手机IMEI号的指令
//    public void closeSocket() {
//        try {
//            if (socket != null) {
//                socket.close();
//                socket = null;
//            }
//        } catch (Exception e) {
//            System.out.println("关闭链路时出错：" + e);
//        }
//    }

//    private String synch2device(int cmd, byte[] data) throws Exception {
//        try {
//            int length = 4;
//
//            if (data != null) {
//                length = length + 4 + data.length;
//            }
//            // 发送指令
//            ByteBuffer bb = ByteBuffer.allocate(length);
//            bb.putInt(cmd);
//
//            if (data != null) {
//                bb.putInt(length);
//                bb.put(data);
//            }
//            socket.getOutputStream().write(bb.array());
//            // 收取服务器返回的应答
//            byte[] resBytes = new byte[128];
//            int numReadedBytes = socket.getInputStream().read(resBytes, 0,
//                    resBytes.length);
//            String rsp = new String(resBytes, 0, numReadedBytes, "utf-8")
//                    .trim();
//            return rsp;
//
//        } catch (Exception e) {
//            return "";
//        }
//    }
    
    private void changeApkInstallStatus(int installStatus) {        
        
        for ( int index = 0; index < mInstallBuffer.size(); index++ )  {
            
            if ( mInstallBuffer.get(index).getApkPath().equals( mDevInfo.getApkPath() ) )  {
                mInstallBuffer.get(index).setInstallStatus(installStatus);
            }
            
        }
    }
    
    /* 从InputStream流中读数据 */  
    public static String readFromSocket( InputStream in, byte[] filelength ) {  
        byte[] filebytes = null;// 文件数据  
        String msg = null;  
        try {
          // 读文件长度  
            
            in.read(filelength, 0, filelength.length);
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