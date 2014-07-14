package com.suitscreen.batchinstall;





public class Tool {
    //int、char、double与byte相互转换的程序
    //整数到字节数组的转换
  
    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        
        for ( int i=b.length-1; i>-1; i-- ) {
            b[i] = new Integer(temp&0xff).byteValue();//将最高位保存在最低位
            temp = temp >> 8;//向右移8位
          }
        
          return b;
         }

    
          //字节数组到整数的转换
    public static int byteToInt(byte[] b) {
       int s = 0;
        for (int i = 0; i < 3; i++)  {
             if  ( b[i] >= 0 )
                  s = s + b[i];
             else
                 s = s + 256 + b[i];
             
             s = s * 256;
        }
        
        if (b[3] >= 0)   //最后一个之所以不乘，是因为可能会溢出
            s = s + b[3];
        else
            s = s + 256 + b[3];
        
    return s;
    }
 
     //字符到字节转换

    public static byte[] charToByte(char ch) {
        int temp=(int)ch;
        byte[] b=new byte[2];
        for (int i=b.length-1;i>-1;i--) {
            b[i] = new Integer(temp&0xff).byteValue();//将最高位保存在最低位
            temp = temp >> 8;//向右移8位
          }
        return b;
        
    }
    
    //字节到字符转换
    public static char byteToChar(byte[] b){
        int s=0;
        if ( b[0] > 0 )
           s+=b[0];
        else
            s+=256+b[0];
        
        s*=256;
        
        if(b[1]>0)
            s+=b[1];
        else
            s+=256+b[1];
        
        char ch=(char)s;
        
        return ch;
      }
      
    //浮点到字节转换

    public static byte[] doubleToByte(double d) {
        byte[] b=new byte[8];
        long l=Double.doubleToLongBits(d);
        
        for ( int i=0; i<b.length; i++) {
          b[i] = new Long(l).byteValue();
          l=l>>8;
        }
        return b;
      } 
    
    //字节到浮点转换
    
    public static double byteToDouble(byte[] b) {    
      long l;    
      l=b[0];    
      l&=0xff;    
      l|=((long)b[1]<<8);    
      l&=0xffff;    
      l|=((long)b[2]<<16);    
      l&=0xffffff;    
      l|=((long)b[3]<<24);    
      l&=0xffffffffl;    
      l|=((long)b[4]<<32);    
      l&=0xffffffffffl;    
      l|=((long)b[5]<<40);
      l&=0xffffffffffffl;    
      l|=((long)b[6]<<48);    
      l&=0xffffffffffffffl;    
      l|=((long)b[7]<<56);    
      return Double.longBitsToDouble(l);    
    }
}
