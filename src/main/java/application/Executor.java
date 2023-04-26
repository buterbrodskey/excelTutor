package application;

import checks.ICheck;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.InstantImpl;

import java.io.IOException;

import static excel.ExcelUtils.readWorkbook;
import static excel.ExcelUtils.writeWorkbook;

public class Executor {


    public static void run(final String path, final ICheck check) throws IOException {
        InstantImpl.start();
        XSSFWorkbook workbook = readWorkbook(path);
        try {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                check.run(sheet.getRow(i));
            }
        } finally {
            writeWorkbook(path);
            InstantImpl.showDuration();
        }
    }

    public static void run(final String path, final ICheck check, int startIndex) throws IOException {
        InstantImpl.start();
        XSSFWorkbook workbook = readWorkbook(path);
        try {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = startIndex; i <= sheet.getLastRowNum(); i++) {
                check.run(sheet.getRow(i));
            }
        } finally {
            writeWorkbook(path);
            InstantImpl.showDuration();
        }
    }

    public static void run(final String path, final ICheck check, int startIndex, int endIndex) throws IOException {
        InstantImpl.start();
        XSSFWorkbook workbook = readWorkbook(path);
        try {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = startIndex; i <= endIndex; i++) {
                check.run(sheet.getRow(i));
            }
        } finally {
            writeWorkbook(path);
            InstantImpl.showDuration();
        }
    }
}
