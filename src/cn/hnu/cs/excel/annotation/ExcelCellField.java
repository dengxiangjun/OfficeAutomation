package cn.hnu.cs.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * excel 单元格映射到对象属性
 *
 * @author dengxiangjun
 * @version 1.0
 * @created 17/3/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelCellField {

    /**
     * 表头名称
     *
     * @return
     */
    String name() default "";

    /**
     * 第几列,从0开始
     * index 不能超过,对象属性数目
     *
     * @return
     */
    int index();

    /**
     * 如果是日期,需要指定日期格式
     * 如果不指定,日期格式默认为:yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    String format() default "yyyy-MM-dd HH:mm:ss";
}
