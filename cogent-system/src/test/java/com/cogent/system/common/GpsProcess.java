package com.cogent.system.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.*;
import cn.hutool.core.util.CharsetUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/4
 * {@code @description:}
 */
public class GpsProcess {

    @SneakyThrows
    @Test
    void gps() {
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\gps.csv"));

        List<CsvRow> rows = data.getRows();
//遍历行
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）

            csvRow.remove(2);
            csvRow.remove(2);
            csvRow.remove(2);
        }

        //指定路径和编码
        CsvWriter writer = CsvUtil.getWriter("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\gps2.csv", CharsetUtil.CHARSET_UTF_8);
//按行写出
        writer.write(data);

    }

    @SneakyThrows
    @Test
    void gps2() {
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\gps2.csv"));

        List<CsvRow> rows = data.getRows();
        for (CsvRow csvRow : rows) {
            String date = csvRow.get(2);
            String time = csvRow.get(3);
            String dateTime = date + " " + time;
            csvRow.add(dateTime);
            csvRow.add("C10821A0212");
        }

        CsvWriter writer = CsvUtil.getWriter("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\gps3.csv", CharsetUtil.CHARSET_UTF_8);
//按行写出
        writer.write(data);

    }

    @Test
    void getSql() {
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\gps3.csv"));

        List<CsvRow> rows = data.getRows();
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `cogent-admin`.`gps_info` (`lat`, `lon`, `create_time`, `sn`) VALUES ");
        for (CsvRow csvRow : rows) {
            sb.append("(" + csvRow.get(0) + "," + csvRow.get(1) + ",'" + csvRow.get(4) + "','" + csvRow.get(5) + "'),");
        }
        sb.deleteCharAt(sb.length() - 1);
        System.out.println(sb.toString());
    }
}
