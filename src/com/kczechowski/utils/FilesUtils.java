package com.kczechowski.utils;

import java.io.File;
import java.io.IOException;
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

    public static List getMusicFilesInFolderIncludingSubfolders(File folder) throws IllegalArgumentException {
        List list = new ArrayList();
        if(folder.isDirectory()){
            for(File file : folder.listFiles()){
                if(!file.isDirectory()){
                    if(FilesUtils.isMusicFile(file))
                        list.add(file);
                }
                else
                    list.addAll(getMusicFilesInFolderIncludingSubfolders(file));
            }
        }else throw new IllegalArgumentException("Argument is not a directory");

        return list;
    }

    public static String getFileExtension(File file){
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isMusicFile(File file){
        if(getFileExtension(file).equals("mp3"))
            return true;
        else
            return false;
    }

    public static void deleteDirectory(File dir) throws IOException {
/*        File[] files = dir.listFiles();
        if(files!=null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();*/

/*        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });*/
    }

    public static String getCleanFilePath(String path){
        return path.replaceAll("[^\\w\\s]","");
    }

}
