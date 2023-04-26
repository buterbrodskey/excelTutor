package excel;

import org.apache.commons.codec.binary.Hex;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtils {

    private static XSSFWorkbook workbook;
    private static File file;

    public static String getBackgroundColor(XSSFCell cell) {
        byte[] rgbWithTint = cell.getCellStyle().getFillForegroundColorColor().getRGBWithTint();
        if (rgbWithTint == null) {
            return null;
        }
        return Hex.encodeHexString(rgbWithTint);
    }

    public static XSSFWorkbook readWorkbook(String path) throws IOException {
        file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        if (workbook == null) {
            workbook = new XSSFWorkbook(inputStream);
        }
        inputStream.close();
        return workbook;
    }

    public static File getFile() {
        return file;
    }

    public static void writeWorkbook(String path) throws IOException {
        File filePath = new File(path);
        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        outputStream.close();
    }

    public static void createLinkCell(String label, String url, XSSFCell cell) {
        XSSFHyperlink link = new XSSFCreationHelper(workbook).createHyperlink(HyperlinkType.URL);
        link.setLabel(label);
        link.setAddress(url);
        cell.setHyperlink(link);
    }
}
