package excel;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.time.format.DateTimeFormatter;

public class FsspExcelInsert {
    public static void insertResult(XSSFSheet sheet, String fio, String date, String result) {
        XSSFRow row;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            if (row.getCell(3).getLocalDateTimeCellValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).equals(date)
                    && row.getCell(0).getStringCellValue().equals(fio.substring(0, fio.indexOf(" ")))) {
                row.createCell(4).setCellValue(result);
            }
        }
    }
}
