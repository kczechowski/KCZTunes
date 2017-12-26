package com.kczechowski.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesUtils {
    public static void listFilesInFolder(File folder){
        for(File file : folder.listFiles()){
            if(!file.isDirectory())
                System.out.println(file.getName());
        }
    }

    public static List getFilesInFolder(File folder) throws IllegalArgumentException {
        List list = new ArrayList();
        if(folder.isDirectory()){
            for(File file : folder.listFiles()){
                if(!file.isDirectory())
                    list.add(file.getName());
            }
        }else
            throw new IllegalArgumentException("Argument is not a directory");

        return list;
    }
}
