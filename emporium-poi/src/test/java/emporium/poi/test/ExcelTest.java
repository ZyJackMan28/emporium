package emporium.poi.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ExcelTest {

    /*
     * 测试创建excel
     * @parame  * @param null
     * @return
     * @exception
     * @author silenter
     * @date 2019/9/27 13:51
     */
    @Test
    public void testCreateExcel() throws IOException {
        String []title = {"订单ID","流水号"};
        //创建HSSF工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建一个Sheet页
        HSSFSheet sheet = workbook.createSheet();
        //创建第一行（一般是表头）
        HSSFRow row0 = sheet.createRow(0);
        //创建列
        HSSFCell cell = null;
        //设置表头
        for (int i = 0; i <title.length ; i++) {
            cell=row0.createCell(i);
            cell.setCellValue(title[i]);
        }
        //填充20行数据
        for (int i = 1; i < 20; i++) {
            HSSFRow row =sheet.createRow(i);
            HSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(RandomStringUtils.randomNumeric(18));
            HSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(RandomStringUtils.randomNumeric(12));
        }
        //保存到本地
        File file = new File("E:\\temp\\test.xls");
        FileOutputStream outputStream = new FileOutputStream(file);
        //将Excel写入输出流中
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


    @Test
    public void testParseExcel() throws Exception{
        //读取本地文件
        File file = new File("E:\\temp\\test.xls");
        FileInputStream fis = new FileInputStream(file);
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet1 = workbook.getSheetAt(0);
        //获取当前sheet页的总行数
        int totalRowNums = sheet1.getPhysicalNumberOfRows();
        for (int i = 0; i < totalRowNums; i++) {
            Row row = sheet1.getRow(i);
            //获取每一行的总列数
            int totalCellNums = row.getPhysicalNumberOfCells();
            for (int j = 0; j < totalCellNums; j++) {
                Cell cell = row.getCell(j);
                String value = getCellValue(cell);
                System.out.println(value);
            }
        }
    }

    public static String getCellValue(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            Double d = cell.getNumericCellValue();
            return String.valueOf(d.intValue());
        }
        return String.valueOf(cell.getStringCellValue());
    }


}
