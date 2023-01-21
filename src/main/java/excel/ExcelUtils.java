package excel;

import org.apache.commons.codec.binary.Hex;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExcelUtils {

    private static XSSFWorkbook workbook;

    public static XSSFWorkbook getWorkBookForRead(String fileLocation) throws IOException {
        if (workbook == null) {
            try {
                FileInputStream file = new FileInputStream(new File(fileLocation));
                workbook = new XSSFWorkbook(file);
            } catch (FileNotFoundException e) {
                throw new NullPointerException();
            }
        }
        return workbook;
    }

    public static String getBackgroundColor(XSSFCell cell) {
        byte[] rgbWithTint = cell.getCellStyle().getFillForegroundColorColor().getRGBWithTint();
        if (rgbWithTint == null) {
            return null;
        }
        return Hex.encodeHexString(rgbWithTint);
    }
}
