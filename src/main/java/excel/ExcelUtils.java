package excel;

import org.apache.commons.codec.binary.Hex;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtils {

    private static XSSFWorkbook workbook;

    public static String getBackgroundColor(XSSFCell cell) {
        byte[] rgbWithTint = cell.getCellStyle().getFillForegroundColorColor().getRGBWithTint();
        if (rgbWithTint == null) {
            return null;
        }
        return Hex.encodeHexString(rgbWithTint);
    }

    public static XSSFWorkbook readWorkbook(String path) throws IOException {
        File filePath = new File(path);
        FileInputStream inputStream = new FileInputStream(filePath);
        if (workbook == null) {
            workbook = new XSSFWorkbook(inputStream);
        }
        inputStream.close();
        return workbook;
    }

    public static XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public static void writeWorkbook(String path) throws IOException {
        File filePath = new File(path);
        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        outputStream.close();
    }
}
