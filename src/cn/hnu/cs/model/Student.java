package cn.hnu.cs.model;

import cn.hnu.cs.excel.annotation.ExcelCellField;

/**
 * 学生实体
 * Created by deng on 2017/10/31.
 */
public class Student {
    @ExcelCellField(name = "学号", index = 1)
    private String stuNo;//学号
    @ExcelCellField(name = "姓名", index = 0)
    private String name;//姓名
    @ExcelCellField(name = "邮箱", index = 2)
    private String email;//邮箱

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
