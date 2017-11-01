package cn.hnu.cs.excel.convert;

import cn.hnu.cs.util.NumberUtil;
import com.google.common.primitives.Longs;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.text.ParseException;
import java.util.Date;

/**
 * @author dengxiangjun
 * @version 1.0
 * @created 17/3/16
 */
public class CellConvert {


    public static String getString(Cell cell) {
        if (isNullCell(cell)) {
            return null;
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return cell.getStringCellValue();
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return NumberUtil.numberStr(cell.getNumericCellValue());
        }
        throw new RuntimeException("can not convertWithConstructor cell value to String!");
    }

    private static boolean isNullCell(Cell cell) {
        return cell == null || Cell.CELL_TYPE_BLANK == cell.getCellType();
    }


    /**
     * 设置Cell值
     * @param cell
     * @param value
     */
    public static void setValue(Cell cell,String value){
        if(value==null){
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        }else{
            cell.setCellValue(value);
        }
    }
    

    public static Integer getInteger(Cell cell) {
        if (isNullCell(cell)) {
            return null;
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return (int) cell.getNumericCellValue();
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return Integer.parseInt(cell.getStringCellValue());
        }
        throw new RuntimeException("can not convertWithConstructor cell value to Integer!");
    }

    public static Float getFloat(Cell cell) {
        if (isNullCell(cell)) {
            return null;
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return (float) cell.getNumericCellValue();
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return Float.parseFloat(cell.getStringCellValue());
        }
        throw new RuntimeException("can not convertWithConstructor cell value to Float!");
    }

    public static Double getDouble(Cell cell) {
        if (isNullCell(cell)) {
            return null;
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return cell.getNumericCellValue();
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return Double.parseDouble(cell.getStringCellValue());
        }
        throw new RuntimeException("can not convertWithConstructor cell value to Double!");
    }

    public static Short getShort(Cell cell) {
        if (isNullCell(cell)) {
            return null;
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return (short) cell.getNumericCellValue();
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return Short.parseShort(cell.getStringCellValue());
        }
        throw new RuntimeException("can not convertWithConstructor cell value to Short!");
    }

    public static Long getLong(Cell cell) {
        if (isNullCell(cell)) {
            return null;
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return (long) cell.getNumericCellValue();
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return Longs.tryParse(cell.getStringCellValue());
        }
        throw new RuntimeException("can not convertWithConstructor cell value to Long!");
    }

    public static Boolean getBoolean(Cell cell) {
        if (isNullCell(cell)) {
            return null;
        }
        if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
            return cell.getBooleanCellValue();
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            String value = cell.getStringCellValue();
            return "1".equals(value) || "是".equals(value);
        }
        throw new RuntimeException("can not convertWithConstructor cell value to Boolean!");
    }

    public static Date getDate(Cell cell, String format) throws ParseException {
        if (isNullCell(cell)) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:// 数字类型
                return getCellDate(cell);
            case Cell.CELL_TYPE_STRING:
                if(StringUtils.isNotEmpty(cell.getStringCellValue())){
                    return DateUtils.parseDate(cell.getStringCellValue(), format);
                }else{
                    return null;
                }
            default:
                throw new RuntimeException("can not convertWithConstructor cell value to Date!");
        }
    }

    private static Date getCellDate(Cell cell) {
        if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
            return cell.getDateCellValue();
        }
        return DateUtil.getJavaDate(cell.getNumericCellValue());
    }

    public static Byte geByte(Cell cell) {
        if (isNullCell(cell)) {
            return null;
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return (byte) cell.getNumericCellValue();
        }
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            String value = cell.getStringCellValue();
            if(StringUtils.isNotEmpty(value)){
                Byte.valueOf(cell.getStringCellValue());
            }else{
                return null;
            }
        }
        throw new RuntimeException("can not convertWithConstructor cell value to Integer!");
    }
}
