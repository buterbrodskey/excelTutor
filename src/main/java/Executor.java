import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Executor {

    private static XSSFWorkbook workbook = null;

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

    public void runOneByOne(String path, ICheck check) throws IOException {
        XSSFWorkbook workbook1 = readWorkbook(path);

        //check.check(workbook);
    }
}
