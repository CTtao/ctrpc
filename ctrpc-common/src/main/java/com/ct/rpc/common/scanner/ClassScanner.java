package com.ct.rpc.common.scanner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class ClassScanner {

    /**
     * 文件
     */
    private static final String PROTOCOL_FILE = "file";

    /**
     * jar包
     */
    private static final String PROTOCOL_JAR = "jar";

    /**
     * class文件的后缀
     */
    private static final String CLASS_FILE_SUFFIX = ".class";



    public static List<String> getClassNameList(String packageName) throws Exception{
        List<String> classNameList = new ArrayList<>();

        boolean recursive = true;

        String packageDirName = packageName.replace('.','/');

        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

        while (dirs.hasMoreElements()){
            URL url = dirs.nextElement();

            String protocol = url.getProtocol();

            if (PROTOCOL_FILE.equals(protocol)){
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNameList);
            } else if (PROTOCOL_JAR.equals(protocol)){
                packageName = findAndAddClassInPackageByJar(packageName, classNameList, recursive, packageDirName, url);
            }
        }
        return classNameList;
    }
    /**
     * 扫描当前工程中指定包下的所有类信息
     * @param packageName   扫描包名
     * @param packagePath   包的完整路径
     * @param recursive     是否递归
     * @param classNameList 类名称集合
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<String> classNameList){
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);

        //如果不存在或者也不是目录则返回
        if (!dir.exists() || !dir.isDirectory()){
            return;
        }

        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录)或是以.class结尾的文件
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for (File file : dirfiles){
            //如果是目录则继续扫描
            if (file.isDirectory()){
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classNameList);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                //添加到集合中去
                classNameList.add(packageName + "." + className);
            }
        }
    }

    /**
     *
     * @param packageName       扫描的包名
     * @param classNameList     完成类名存放的List集合
     * @param recursive         是否递归
     * @param packageDirName    当前包名的前面部分的名称
     * @param url               包的url地址
     * @return                  处理后的包名
     * @throws IOException
     */
    private static String findAndAddClassInPackageByJar(String packageName, List<String> classNameList, boolean recursive, String packageDirName, URL url) throws IOException {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()){
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.charAt(0) == '/'){
                name = name.substring(1);
            }
            if (name.startsWith(packageDirName)){
                int idx = name.lastIndexOf('/');

                if (idx != -1){
                    packageName = name.substring(0, idx).replace('/','.');
                }

                if ((idx != -1) || recursive){
                    if (name.endsWith(CLASS_FILE_SUFFIX) && !entry.isDirectory()){
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        classNameList.add(packageName + "." + className);
                    }
                }
            }
        }
        return packageName;
    }
}
