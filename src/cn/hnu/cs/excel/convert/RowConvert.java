package cn.hnu.cs.excel.convert;

import cn.hnu.cs.excel.annotation.ExcelCellField;
import cn.hnu.cs.excel.annotation.ExcelObJectInstanceType;
import cn.hnu.cs.excel.annotation.ExcelObjectInstance;
import cn.hnu.cs.excel.annotation.ExcelSheetField;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dengxiangjun
 * @version 1.0
 * @created 17/3/16
 */
public class RowConvert {

    public static <T> List<T> convertWithConstructor(Iterator<Row> rows, Class<T> clazz, boolean hasHeader) {
        List<T> dataList = Lists.newArrayList();
        while (rows.hasNext()) {
            Row row = rows.next();
            if (hasHeader && row.getRowNum() < 1) {
                continue;
            }
            try {
                dataList.add(convert(row, clazz));
            } catch (Exception e) {
                throw new RuntimeException(String.format(" convertWithConstructor has error: message=%s", e.getMessage()));
            }
        }
        return dataList;
    }

    public static <T> T convertWithConstructor(Map<String,Sheet> sheetMap, Class<T> clazz, boolean hasHeader) throws IllegalAccessException, InstantiationException, InvocationTargetException {
//        T object = (T) UnsafeUtil.getUnsafe().allocateInstance(clazz);
        T object = clazz.newInstance();   //可能没有无参构造方法
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(ExcelSheetField.class)){
                ExcelSheetField annotation = field.getAnnotation(ExcelSheetField.class);
                Sheet sheet = sheetMap.get(annotation.name());
                if(sheet == null) continue;
                Iterator<Row> iterator = sheet.iterator();
                Class type = field.getType();
                boolean isList = type.isAssignableFrom(List.class);
                if(isList){
                    type = TypeUtils.getRawType(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0],null);
                }
                List<T> list = convertWithConstructor(iterator, type, hasHeader);
                if (isList) {
                    setMethod(field, clazz).invoke(object, list);
                } else {
                    if(CollectionUtils.isNotEmpty(list)){
                        setMethod(field, clazz).invoke(object, list.get(0));
                    }
                }
            }
        }
        return object;
    }

    public static Method getMethod(Field field, Class clazz){
        String name = field.getName();
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name,clazz);
            Method readMethod = pd.getReadMethod();
            return readMethod;
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s的%s字段Get方法不存在", clazz.getSimpleName(), name));
        }
    }
    public static Method setMethod(Field field, Class clazz){
        String name = field.getName();
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name,clazz);
            Method readMethod = pd.getWriteMethod();
            return readMethod;
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s的%s字段Set方法不存在", clazz.getSimpleName(), name));
        }
    }


    public static <T> T convertWithConstructor(Row row, Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ParseException {
        Field[] fields = new Field[]{};
        Class temp = clazz;
        while(temp != null){
            fields = ArrayUtils.addAll(temp.getDeclaredFields(), fields);
            temp = temp.getSuperclass();
        }

        Class<?>[] params = new Class[fields.length];
        Object[] paramValues = new Object[fields.length];

        for (Field field : fields) {
            ExcelCellField filedExcelAnnotation = getAnnotationCellFiled(field.getAnnotations());
            if (filedExcelAnnotation == null) {
                continue;
            }
            Integer index = filedExcelAnnotation.index();
            Class<?> fieldType = field.getType();
            params[index] = fieldType;
            paramValues[index] = getValue(fieldType, row.getCell(index), filedExcelAnnotation);
        }
        Constructor<?> constructor = clazz.getDeclaredConstructor(params);
        return (T) constructor.newInstance(paramValues);
    }

    public static <T> T convert(Row row, Class<T> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, ParseException, IllegalAccessException {
        if (isConstructorOrMethod(clazz.getAnnotations())) {
            return convertWithConstructor(row, clazz);
        }
        return convertWithMethod(row, clazz);
    }

    private static boolean isConstructorOrMethod(Annotation[] annotations) {
        ExcelObjectInstance excelObejctInstance = getAnnotationObjectInstance(annotations);
        if (excelObejctInstance == null) {
            return false;
        }
        return ExcelObJectInstanceType.CONSTRUCTOR.equals(excelObejctInstance.value());
    }

    private static <T> T convertWithMethod(Row row, Class<T> clazz) throws IllegalAccessException, InstantiationException, ParseException, InvocationTargetException {
        Field[] fields = new Field[]{};
        Class temp = clazz;
        while(temp != null){
            fields = ArrayUtils.addAll(temp.getDeclaredFields(),fields);
            temp = temp.getSuperclass();
        }
        Object object = clazz.newInstance();
        for (Field field : fields) {
            ExcelCellField filedExcelAnnotation = getAnnotationCellFiled(field.getAnnotations());
            if (filedExcelAnnotation == null) {
                continue;
            }
            Class<?> fieldType = field.getType();
            Integer index = filedExcelAnnotation.index();
            String propertyName = field.getName();
            Object value = getValue(fieldType, row.getCell(index), filedExcelAnnotation);
            if (value != null) {
                BeanUtils.setProperty(object, propertyName, value);
            }
        }
        return (T) object;
    }

    private static Object getValue(Class<?> fieldType, Cell cell, ExcelCellField filedExcelAnnotation) throws ParseException {
        if (fieldType.isAssignableFrom(String.class)) {
            return CellConvert.getString(cell);
        }
        if(fieldType.isAssignableFrom(Byte.class)){
            return CellConvert.geByte(cell);
        }
        if (fieldType.isAssignableFrom(Integer.class)) {
            return CellConvert.getInteger(cell);
        }
        if (fieldType.isAssignableFrom(Float.class)) {
            return CellConvert.getFloat(cell);
        }
        if (fieldType.isAssignableFrom(Double.class)) {
            return CellConvert.getDouble(cell);
        }
        if (fieldType.isAssignableFrom(Short.class)) {
            return CellConvert.getShort(cell);
        }
        if (fieldType.isAssignableFrom(Long.class)) {
            return CellConvert.getLong(cell);
        }
        if (fieldType.isAssignableFrom(Boolean.class)) {
            return CellConvert.getBoolean(cell);
        }
        if (fieldType.isAssignableFrom(Date.class)) {
            return CellConvert.getDate(cell, filedExcelAnnotation.format());
        }
        throw new RuntimeException(String.format(" field (%s) is not support field =%S", filedExcelAnnotation.name(), fieldType.getName()));
    }

    public static ExcelCellField getAnnotationCellFiled(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ExcelCellField) {
                return (ExcelCellField) annotation;
            }
        }
        return null;
    }


    private static ExcelObjectInstance getAnnotationObjectInstance(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ExcelObjectInstance) {
                return (ExcelObjectInstance) annotation;
            }
        }
        return null;
    }
}
