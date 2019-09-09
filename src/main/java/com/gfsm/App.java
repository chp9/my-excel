package com.gfsm;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class App {
    public static void main(String[] args) {
        //String path = "D:\\广发商贸\\8.13\\test2\\test2";
        //String path = "D:\\广发商贸\\8.13\\test3";
        //String path = "D:\\广发商贸\\8.13\\test2";
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入包含两个excel文件的目录：");
        String path = sc.nextLine();
        System.out.println("你输入的是：");
        System.out.println(path);
        compareExcel(path);
    }

    /**
     * 比较两个excel
     * 将两个文件放在同一个文件夹下
     */
    public static void compareExcel(String path){
        //获取文件夹
        HashMap<String, List<File>> fileMapLists = getFileLists(path);
        System.out.println("1、文件夹加载完毕,"+fileMapLists);
        //保存文件夹下的数据
        ExcelListener excelListener = new ExcelListener();
        HashMap<String, List<HashSet<ArrayList>>> excelMapLists = new HashMap<>();
        for (String file : fileMapLists.keySet()) {
            excelMapLists.put(file,getHashSets(excelListener, fileMapLists.get(file)));
        }
        System.out.println("2、excel数据加载完成！");

        //两个excel放一个文件夹下比较
        if (excelMapLists.size() == 1) {
            System.out.println("正进行同一目录下的excel文件比较！");
            Set<String> keySet = excelMapLists.keySet();
            String key = keySet.iterator().next();
            List<HashSet<ArrayList>> list = excelMapLists.get(key);
            if(list.size() == 2){
                HashSet<ArrayList> firstExcelData = list.get(0);
                HashSet<ArrayList> secondExcelData = list.get(1);
                if (firstExcelData.containsAll(secondExcelData) ||secondExcelData.containsAll(firstExcelData) ){
                    System.out.println("比对完成,结果正确!");
                }else {
                    System.out.println("比对完成,有错误。");
                    System.out.println("以下数据出现错误：");
                    System.out.println(secondExcelData );
                    System.out.println(firstExcelData );
                }
            }
        //比较两个文件夹下的所有excel
        } else if (excelMapLists.size() == 2) {
            System.out.println("正两个文件夹下的excel文件比较！");
            Set<String> keySet = excelMapLists.keySet();
            List<String> keys = new ArrayList<>(keySet);
            String firstDir = keys.get(0);
            String secondDir = keys.get(1);
            List<HashSet<ArrayList>> firstDirExcel = excelMapLists.get(firstDir);
            List<HashSet<ArrayList>> secondDirExcel = excelMapLists.get(secondDir);
            if (firstDirExcel.containsAll(secondDirExcel) || secondDirExcel.containsAll(firstDirExcel)) {
                System.out.println("比对完成,结果正确!");
                return;
            }
            for (HashSet<ArrayList> excelData : secondDirExcel) {
                boolean isRun = false;
                int index = 0;
                while (!isRun){
                    if (index < firstDirExcel.size()) {
                        HashSet<ArrayList> tempExcel = firstDirExcel.get(index);
                        if (tempExcel.containsAll(excelData) || excelData.containsAll(tempExcel) ) {
                            isRun = true;
                        }
                    } else {
                        System.out.println("比对完成,有错误。");
                        System.out.println("错误数据：");
                        System.out.println(excelData);
                        return;
                    }
                    ++index;
                }
             }
            System.out.println("比对完成,结果正确!");
        } else {
            System.out.println("excel文件夹数量 ：" + excelMapLists);
            System.out.println("文件路径无法比较，请输入一个文件夹路径（包含两个需要对比的excel文件），" +
                              "或者一个文件夹（包含两个需要比较的目录，目录含多个需要比较的excel文件）。");
        }
    }

    /**
     * 知道文件夹下的所有excel
     * @param excelListener
     * @param index1List
     * @return
     */
    private static List<HashSet<ArrayList>> getHashSets(ExcelListener excelListener, List<File> index1List) {
        //保存文件夹下的所有excel
        List<HashSet<ArrayList>> directExcels = new ArrayList<>();
        for (File tempFile : index1List) {
            try {
                FileInputStream inputStream = new FileInputStream(tempFile);
                ExcelReader excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLSX, null, excelListener);
                excelReader.read();
                //获取一个excel
                HashSet<ArrayList> index1ValueList = ExcelListener.getValueList();
                ExcelListener.setValueList(null);
                //保存一个excel
                directExcels.add(index1ValueList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return directExcels;
    }

    /**
     * 保存文件中的文件 key为上一个目录内容
     * @param path
     * @return
     */
    public static HashMap<String,List<File>> getFileLists(String path){
        HashMap<String,List<File>> fileMap = new HashMap<>();
        File file = new File(path);
        File[] files = file.listFiles();
        LinkedList<File> index0List = new LinkedList<>();
        LinkedList<File> index1List = new LinkedList<>();
        if(null != files && files.length == 2){
            if (files[0].isDirectory()){
                index0List = fileLists(files[0].getAbsolutePath(), index0List);
            } else {
                index0List.add(files[0]);
            }
            if (files[1].isDirectory()){
                index1List = fileLists(files[1].getAbsolutePath(), index1List);
            } else {
                index1List.add(files[1]);
            }
        } else {
            System.out.println("输入文件加目录不正确，或者缺少文件！");
        }

        fileMap = fileMapList(fileMap, index0List);
        fileMap = fileMapList(fileMap, index1List);
        return fileMap;
    }

    private static HashMap<String, List<File>> fileMapList(HashMap<String, List<File>> fileMap, LinkedList<File> indexList) {
        List<File> tempList;
        for (File temp : indexList) {
            String parent = temp.getParent();
            if (null != fileMap.get(parent)) {
                tempList = fileMap.get(parent);
                tempList.add(temp);
                fileMap.put(parent,tempList);
            }else {
                tempList = new ArrayList<>();
                tempList.add(temp);
                fileMap.put(parent,tempList);
            }
        }
        return fileMap;
    }

    private static LinkedList<File> fileLists(String path,LinkedList<File>list){
        File file = new File(path);
        File[] files = file.listFiles();
        for (File f:files){
            if(f.isDirectory()){
                fileLists(f.getAbsolutePath(),list);
            }else {
                list.add(f);
            }
        }
        return list;
    }
}
