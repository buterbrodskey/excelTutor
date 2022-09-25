package excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExcelUtils {

    private static Workbook workbook;

    public static Workbook getWorkBook(String fileLocation) throws IOException {
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
}
