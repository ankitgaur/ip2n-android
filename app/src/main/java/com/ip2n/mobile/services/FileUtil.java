package com.ip2n.mobile.services;

import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ankitgaur on 5/27/17.
 */
public class FileUtil {

    public static byte[] getBytes(File file){

        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        } catch (FileNotFoundException e) {
            //File not found
        } catch (IOException e) {
            //Error reading file
        }

        return bytesArray;

    }


}
