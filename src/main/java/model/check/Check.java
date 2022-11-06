package model.check;

import model.PersonModel;
import model.fill.FillStrategy;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class Check {

    public void start(String path, FillStrategy fillStrategy, int resultIndex) throws IOException, TesseractException, InterruptedException {
        File filePath = new File(path);
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            PersonModel person;
            try {
                person = fillStrategy.createPersonModel(sheet.getRow(i));
            } catch (Exception e) {
                continue;
            }
            String result;
            result = check(person);

            if (result == null) {
                XSSFCell cell = row.createCell(resultIndex);
                cell.setCellValue(result);
            }
        }
        file.close();

        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        outputStream.close();
    }

    public abstract String check(PersonModel row) throws TesseractException, IOException, InterruptedException;
}
