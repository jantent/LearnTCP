package com.buf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileCopy {
    public static void main(String args[]) throws Exception {
        long begeintime = System.currentTimeMillis();
        String srcFile = "F:\\filecopy\\filecopy.rar";
        String desFile = "F:\\filecopy\\test13.rar";
        copyFile(srcFile,desFile);
        long endtime = System.currentTimeMillis();
        System.out.println("”√ ±£∫"+(endtime-begeintime)/1000+" √Î");
    }

    public static void copyFile(String srcFile,String desFile) throws Exception{
        byte[] temp = new byte[1024];
        FileInputStream inputStream = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(desFile);
        int length = 0;
        while ((length = inputStream.read(temp))!=-1){
            fos.write(temp,0,length);
        }
        inputStream.close();
        fos.close();
    }

    public static void NioCopyFile(String srcFile,String desFile) throws Exception{
        RandomAccessFile srcRandomAccess = new RandomAccessFile(srcFile,"r");
        FileChannel srcfileChannel = srcRandomAccess.getChannel();

        RandomAccessFile desRadndomAccess = new RandomAccessFile(desFile,"rw");
        FileChannel desfileChannel1 = desRadndomAccess.getChannel();

        long position = 0;
        long count =srcfileChannel.size();

        srcfileChannel.transferTo(0,count,desfileChannel1);
    }
}
