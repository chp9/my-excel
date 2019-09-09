package com.gfsm;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.junit.Test;

import java.io.FileInputStream;


public class test {
    @Test
    public void testExcel2003NoModel() {
        try (FileInputStream inputStream = new FileInputStream("D:\\code\\gfsm-excel\\src\\main\\resources\\position-1.xlsx")) {
            // 解析每行结果在listener中处理
            ExcelListener listener = new ExcelListener();
            ExcelReader excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLSX, null, listener);
            excelReader.read();
        } catch (Exception e) {

        }
    }
}
