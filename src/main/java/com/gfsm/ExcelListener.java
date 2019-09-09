package com.gfsm;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import java.util.*;

public class ExcelListener extends AnalysisEventListener {

    static HashSet<ArrayList> valueList;

    public static HashSet<ArrayList> getValueList() {
        return valueList;
    }

    public static void setValueList(HashSet<ArrayList> valueList) {
        ExcelListener.valueList = valueList;
    }

    public void invoke(Object object, AnalysisContext context) {
        if (object instanceof ArrayList) {
            if (context.getCurrentRowNum() == 0 ) {
                valueList = null;
                valueList = new HashSet<>();
            }
            ArrayList list = (ArrayList) object;
            ArrayList<String> excelData = new ArrayList<>();
            for (int i=0;i<list.size(); i++) {
                Object obj = list.get(i);
                String format = "";
                if (null != obj) {
                    try {
                        Double value = Double.parseDouble(obj.toString());
                        format = String.format("%.2f", value);
                    }catch (Exception e){
                        format = obj.toString();
                    }
                }
                excelData.add(format);
            }
            valueList.add(excelData);
        }
    }
    public void doAfterAllAnalysed(AnalysisContext context) {
        // datas.clear();//解析结束销毁不用的资源
    }
}