package com.example.notebook.utils;

import java.io.File;
import java.time.LocalDateTime;

public class FileUtils {
    public static void main(String[] args) {
        // System.out.println(LocalDateTime.now());
        System.out.println(replaceFileNames("E:\\java\\视频\\全站首推【Linux-shell编程】2021完整版_从入门到精通_全套教程+项目实战",
                "全站首推【Linux-shell编程】2021完整版_从入门到精通_全套教程+项目实战 - ", "", false));
    }

    /** 
     * 替换文件名，将oldStr替换为newStr
     * @param dirPath 需要修改的文件夹路径
     * @param oldStr 需要替换的str
     * @param newStr 需要替换后的str
     * @param isRec 是否递归修改文件夹
     * @return 返回修改结果
     */
    public static boolean replaceFileNames(String dirPath, String oldStr, String newStr, boolean isRec) {
        /*
        //打开资源管理器选择对应路径dirpath
        JFileChooser jFileChooser = new JFileChooser();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        jFileChooser.setCurrentDirectory(fileSystemView.getHomeDirectory());
        jFileChooser.setApproveButtonText("确定");
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = jFileChooser.showOpenDialog(new JLabel());
        if(JFileChooser.APPROVE_OPTION == result) {
            dirPath = jFileChooser.getSelectedFile().getPath();
            System.out.println(dirPath);
        }
        */
        File dir = new File(dirPath);
        if(!dir.isDirectory()) {
            System.out.println("inputFile is not a directory");
            return false;
        }

        File[] files = dir.listFiles();
        if(files == null || files.length == 0) {
            System.out.println("inputFile is empty");
            return false;
        }
        for(int i = 0; i < files.length; i++) {
            File file = files[i];
            //是否递归批量修改文件名
            if(file.isDirectory() && isRec) {
                replaceFileNames(file.getAbsolutePath(), oldStr, newStr, isRec);
            }else{

                file.renameTo(new File(file.getParent() + "\\" + file.getName().replace(oldStr, newStr)));
            }
        }
        return true;
    }

    /**
     * beginstr + 序号修改文件名
     * @param dirPath 文件夹路径
     * @param beginStr 修改文件后的前缀
     * @param beginIndex 修改文件后的起始索引
     * @return
     */
    public boolean renameOrder(String dirPath, String beginStr, int beginIndex) {
        File dir = new File(dirPath);
        if(!dir.isDirectory()) {
            System.out.println("inputFile is not a directory");
            return false;
        }

        File[] files = dir.listFiles();
        if(files == null || files.length == 0) {
            System.out.println("inputFile is empty");
            return false;
        }

        for(int i = 0; i < files.length; i++) {
            File file = files[i];
            if(files[i].isFile()) {
                String[] split = file.getName().split("\\.");
                String suffix = "";
                if(split.length > 1) {
                    suffix = split[1];
                }
                file.renameTo(new File(file.getParent() + "\\" + beginStr + beginIndex + "." + suffix));
                beginIndex++;
            }
        }
        return true;
    }

}
