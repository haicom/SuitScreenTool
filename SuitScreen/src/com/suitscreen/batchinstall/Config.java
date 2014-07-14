package com.suitscreen.batchinstall;

public class Config {

    public static final String FONT_NAME      = "文泉驿等宽正黑";

    /** <----公共变量----> */
    public static boolean      isStartInstall = false;    // 是否开始安装
    /** </----公共变量----> */
    public static String getPicturePath() {
        return mPicturePath;
    }
    
    public static void setPicturePath(String picturePath) {
        mPicturePath = picturePath;
    }
    
    private static String mPicturePath;
   
}
