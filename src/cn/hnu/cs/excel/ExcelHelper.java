package cn.hnu.cs.excel;

import cn.hnu.cs.excel.convert.RowConvert;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dengxiangjun on 2017/11/1.
 */
public class ExcelHelper {

    public static final String EXCEL_EXT_2003 = "xls";

    public static final String EXCEL_EXT_2007 = "xlsx";

    /**
     * 根据excel文件绝对地址解析excel
     * @param filePathName excel文件绝对地址
     * @param sheetNum     sheet页
     * @param <T>          泛型目标类
     * @param hasHeader    excel是否有表头,默认第一行是有表头的;true有表头,false没有表头
     * @return 数据集合list
     */
    public static <T> List<T> parseData(String filePathName, Integer sheetNum, Class<T> clazz, boolean hasHeader) throws FileNotFoundException {
        InputStream input = new FileInputStream(filePathName);  //建立输入流
        return parseData(input, sheetNum, getFileName(filePathName), clazz, hasHeader);
    }

    public static String getFileName(String filePathName) {
        File tempFile = new File(filePathName.trim());
        return tempFile.getName();
    }


    /**
     * 解析excel文件流到list中
     *
     * @param input     excel文件流
     * @param sheetNum  sheet页
     * @param fileName  文件名,主要用来判断是
     * @param clazz     转化数据到目标类
     * @param <T>       泛型目标类
     * @param hasHeader excel是否有表头,默认第一行是有表头的;true有表头,false没有表头
     * @return 数据集合list
     */
    public static <T> List<T> parseData(InputStream input, Integer sheetNum, String fileName, Class<T> clazz, boolean hasHeader) {
        Iterator<Row> rows = getExcelRows(input, sheetNum, isE2007(fileName));
        return RowConvert.convertWithConstructor(rows, clazz, hasHeader);
    }

    private static Iterator<Row> getExcelRows(InputStream input, Integer sheetNum, boolean isE2007) {
        Workbook wb = generateWorkbook(input,isE2007);
        Sheet sheet = wb.getSheetAt(sheetNum);     //获得第一个表单
        return sheet.rowIterator();
    }

    private static Workbook generateWorkbook(InputStream input,boolean isE2007){
        Workbook wb;
        try {
            //根据文件格式(2003或者2007)来初始化
            if (isE2007) {
                wb = new XSSFWorkbook(input);
            } else {
                wb = new HSSFWorkbook(input);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return wb;
    }

    private static boolean isE2007(String fileName) {
        return fileName.endsWith(EXCEL_EXT_2007);
    }

}
