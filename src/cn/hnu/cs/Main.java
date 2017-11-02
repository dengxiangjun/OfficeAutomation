package cn.hnu.cs;

import cn.hnu.cs.excel.ExcelHelper;
import cn.hnu.cs.model.Student;
import cn.hnu.cs.util.MailUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * @author dengxiangjun
 * @version 1.0
 * @created 17/11/1
 */
public class Main {

    /**
     * 自动办公
     */
    public static void officeAutomation() {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入学生信息Excel表的路径：");
        String filePathName = sc.nextLine();
        System.out.println();
        //String filePathName = "input\\学生信息表.xlsx";
        List<Student> userDtoList = null;
        try {
            userDtoList = ExcelHelper.parseData(filePathName, 0, Student.class, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.print("请输入收集本系内学生材料的文件夹路径：");
        String material = sc.nextLine();
        System.out.println();
        //String material = "input";
        List<String> hasCommit = getFileList(material);

        List<Student> hasNotCommits = checkCommit(userDtoList, hasCommit);
        if (hasNotCommits.size() > 0) {
            System.out.println("目前未提交材料的学生如下：");
            for (Student s : hasNotCommits) {
                System.out.print(s.getName() + " ");
            }
            System.out.println();
            System.out.println("======================================");
            System.out.println("是否需要发送邮件通知他们?请输入Y|N,(Y代表是，N代表否)");

            String shouldSendEmail = sc.nextLine();
            if (shouldSendEmail.equalsIgnoreCase("y")) {
                sendEmail(hasNotCommits);
                System.out.println("邮件已发送");
            }

        } else System.out.println("本系内所有材料已经交齐。");
    }

    /**
     * 发送邮件
     *
     * @param students 学生列表
     */
    public static void sendEmail(List<Student> students) {
        for (Student s : students) {
            String subject = "黎孟温馨提示您提交材料";
            String text = "截止日期是今晚上";
            MailUtil.send(s.getEmail(), subject, text);
        }
    }

    /**
     * 获取目录下所有文件
     *
     * @param dirPath 路径名称
     * @return List<String>
     */
    public static List<String> getFileList(String dirPath) {
        File dirFile = new File(dirPath);
        File[] files = dirFile.listFiles();
        List<String> list = new ArrayList<String>();
        if (files != null) {
            for (File f : files) {
                String filename = f.getName();
                list.add(filename);
            }
        }
        return list;
    }

    /**
     * 检查谁还没有提交材料
     *
     * @param students  学生列表
     * @param hasCommit 已提交列表
     * @return List<Student>
     */
    public static List<Student> checkCommit(List<Student> students, List<String> hasCommit) {
        Iterator<Student> it = students.iterator();
        while (it.hasNext()) {
            Student s = it.next();
            for (String str : hasCommit) {
                if (str.contains(s.getName())) it.remove();
            }
        }
        return students;
    }


    public static void main(String[] args) {
        officeAutomation();
    }
}
