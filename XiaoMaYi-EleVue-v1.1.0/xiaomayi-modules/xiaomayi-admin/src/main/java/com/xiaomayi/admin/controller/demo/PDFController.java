package com.xiaomayi.admin.controller.demo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * <p>
 * 案例测试 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-26
 */
@Slf4j
@RestController
@RequestMapping("/pdf")
@AllArgsConstructor
public class PDFController {

    @GetMapping("/document")
    public void document() throws IOException, DocumentException {
        // 设置中文字体
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese, 12, Font.BOLD);
        // 创建文本
        Document document = new Document();
        String tempFilePath = "E:\\PDF\\项目资金申请.pdf";
        File tempFile = new File(tempFilePath);
        // 建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempFile));

        // 用户密码
        String userPassword = "123456";
        // 拥有者密码
        String ownerPassword = "admin";
        writer.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(), PdfWriter.ALLOW_PRINTING,
                PdfWriter.ENCRYPTION_AES_128);

        document.open();

        PdfPTable table = new PdfPTable(4); //  设置4列

        table.setWidths(new int[]{1, 2, 1, 2});
        table.setTotalWidth(540);// 设置绝对宽度
        table.setLockedWidth(true);// 使绝对宽度模式生效

        // 标题
        Font fontTitle = new Font(bfChinese, 22, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase("项目资金申请单", fontTitle));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setMinimumHeight(50);//设置表格行高
        cell.setBorderWidth(0f);//去除表格的边框
        cell.setColspan(4);
        table.addCell(cell);

        // 第一行
        cell = new PdfPCell(new Paragraph("申请编号：", fontChinese));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setMinimumHeight(25);//设置表格行高
        table.addCell(cell);
        //第一行填写值
        cell = new PdfPCell(new Paragraph("SQ202210270000055", fontChinese));
        cell.setColspan(3);//合并3列
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setMinimumHeight(25);//设置表格行高
        table.addCell(cell);

        // 第2行
        cell = new PdfPCell(new Paragraph("申请日期：", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cell = new PdfPCell(new Paragraph("2020-10-30", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        // 第2行
        cell = new PdfPCell(new Paragraph("申请人：", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("程某某", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // 第4行
        cell = new PdfPCell(new Paragraph("申请项目：", fontChinese));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setMinimumHeight(25);//设置表格行高
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("中国高铁项目实施资金申请", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(3);
        table.addCell(cell);

        //第5行
        cell = new PdfPCell(new Paragraph("资金来源：", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("国家财政", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("支出类型：", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("微信支出", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // 第6行
        cell = new PdfPCell(new Paragraph("申请部门：", fontChinese));
        cell.setMinimumHeight(15);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("铁路局", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("项目负责人：", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("张某某", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高

        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // 第7行
        cell = new PdfPCell(new Paragraph("手机号码：", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("18000000001", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // 第2行
        cell = new PdfPCell(new Paragraph("申请理由：", fontChinese));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setMinimumHeight(25);//设置表格行高
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("因社会发展需要", fontChinese));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setMinimumHeight(25);//设置表格行高
        table.addCell(cell);

        // 第8行
        cell = new PdfPCell(new Paragraph("申请金额：", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("100000", fontChinese));
        cell.setMinimumHeight(30);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("大写：", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("拾万元整", fontChinese));
        cell.setMinimumHeight(25);//设置表格行高
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", fontTitle));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setMinimumHeight(50);//设置表格行高
        cell.setBorderWidth(0f);//去除表格的边框
        cell.setColspan(4);

        table.addCell(cell);

        document.add(table);
        statement(document, fontChinese);
        document.close();
        writer.close();
    }

    //底部承诺
    public static void statement(Document document, Font fontChinese) throws DocumentException {
        //空白行
        setNullLine(document, fontChinese, 10);

        PdfPTable disclaimersTable = new PdfPTable(new float[]{540f});
        disclaimersTable.setTotalWidth(500);
        disclaimersTable.setLockedWidth(true);
        setBaseCell(disclaimersTable, "项目申请承诺：", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(disclaimersTable, "□本人对本次报销的真实性、合理性、相关性负责，由此引起的审计、检查责任由本人承担。", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(disclaimersTable, "□本人已核对本次报销的发票真实无误，电子发票承诺不重复报销。", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(disclaimersTable, "", fontChinese, 25, 2, true, Element.ALIGN_CENTER);
        document.add(disclaimersTable);

        //增加一行
        PdfPTable lastTable = new PdfPTable(new float[]{340f, 200f});
        lastTable.setTotalWidth(500);
        lastTable.setLockedWidth(true);
        setBaseCell(lastTable, "", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(lastTable, "项目负责人：高某某", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(lastTable, "", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(lastTable, "项目申请人：张某某", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(lastTable, "", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(lastTable, "联系电话：18000000001", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(lastTable, "", fontChinese, 25, 1, true, Element.ALIGN_LEFT);
        setBaseCell(lastTable, "申请时间：2024年6月20号", fontChinese, 25, 1, true, Element.ALIGN_LEFT);

        document.add(lastTable);
    }

    /**
     * 空行
     *
     * @param document
     * @param fontChinese
     * @param height
     */
    public static void setNullLine(Document document, Font fontChinese, Integer height) throws DocumentException {
        PdfPTable nullTable = new PdfPTable(1);
        nullTable.setTotalWidth(780);
        nullTable.setLockedWidth(true);
        setNullCell(nullTable, fontChinese, height, 1);
        document.add(nullTable);
    }

    /**
     * 添加空格项
     *
     * @param table
     * @param fontChinese
     * @param height
     * @param colspan
     */
    private static void setNullCell(PdfPTable table, Font fontChinese, Integer height, Integer colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph("", fontChinese));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(colspan);
        //设置表格行高
        cell.setMinimumHeight(height);
        //去除表格的边框
        cell.setBorderWidth(0f);
        table.addCell(cell);
    }

    /**
     * 初始化单元格
     *
     * @param payTable
     * @param value         值
     * @param fontChinese   样式
     * @param minimumHeight 最低高度
     * @param colspan       占用多少个单元格
     * @param hideBorder    是否去除边框
     * @param alignment     对齐方式
     */
    public static void setBaseCell(PdfPTable payTable, String value, Font fontChinese, Integer minimumHeight, Integer colspan, Boolean hideBorder, Integer alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value, fontChinese));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //最低高度
        cell.setMinimumHeight(minimumHeight);
        //去除边框
        if (hideBorder) {
            cell.setBorderWidth(0f);
        }
        //占用几格
        cell.setColspan(colspan);
        payTable.addCell(cell);
    }

}
