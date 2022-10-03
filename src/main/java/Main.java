import actions.InnPageActions;
import actions.PassportValidPageActions;
import actions.PassportValidResultPageActions;
import com.codeborne.selenide.Configuration;
import excel.ExcelUtils;
import model.InnSearchModel;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.ss.usermodel.*;
import pages.InnPage;
import pages.PassportValidPage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.open;

public class Main {

    private final static String INN_DOCUMENT_PATH = "src/main/resources/zap1.xlsx";
    private final static String PASSPORT_DOCUMENT_PATH = "src/main/resources/zap1.xlsx";
    private final static int LAST_NAME_INDEX = 0;
    private final static int NAME_INDEX = 1;
    private final static int NAME_OF_FATHER_INDEX = 2;
    private final static int DATE_INDEX = 3;
    private final static int PASSPORT_INDEX = 4;

    public static void main(String[] args) throws IOException, TesseractException, InterruptedException {
        Configuration.holdBrowserOpen = true;
        Configuration.timeout = 10000;


        // ИНН
        checkAvailabilityOfInn(INN_DOCUMENT_PATH);

        // Действительность
        checkValidOfPassport(PASSPORT_DOCUMENT_PATH);
    }

    private static void checkValidOfPassport(String path) throws IOException, TesseractException, InterruptedException {
        Workbook workbook = ExcelUtils.getWorkBook(path);
        Sheet sheet = workbook.getSheetAt(0);

        long series, number;

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            long passportCell = 0;
            LocalDateTime date = null;
            try {
                passportCell = (long) row.getCell(PASSPORT_INDEX).getNumericCellValue();
                date = row.getCell(DATE_INDEX).getLocalDateTimeCellValue();
            } catch (NullPointerException e) {

            }

            if (passportCell == 0 || date == null) {
                continue;
            }
            String passport = String.valueOf(passportCell);

            if (passport.length() == 9) {
                passport = "0" + passport;
            }

            series = Long.parseLong(passport.substring(0, 4));
            number = Long.parseLong(passport.substring(4, 10));

            PassportValidPageActions passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
            passportValidPageActions.fillData(series, number);
            PassportValidResultPageActions passportValidResultPageActions = new PassportValidResultPageActions(passportValidPageActions.resolveCaptcha());
            int a = i;

            boolean result = passportValidResultPageActions.isValid(series, number);

            if (result == false) {
                System.out.print(++a + " ");
                System.out.print(result + " ");
                System.out.println(
                        row.getCell(LAST_NAME_INDEX).getStringCellValue() + " " +
                                row.getCell(NAME_INDEX).getStringCellValue() + " " +
                                row.getCell(NAME_OF_FATHER_INDEX).getStringCellValue() + " " +
                                ((long) row.getCell(PASSPORT_INDEX).getNumericCellValue()));
            }
            else {
                System.out.print(++a + " ");
                System.out.println(result + " ");
            }
        }
    }

    public static void checkAvailabilityOfInn(String path) throws IOException {
        InnSearchModel inn = new InnSearchModel();
        Workbook workbook = ExcelUtils.getWorkBook(path);
        Sheet sheet = workbook.getSheetAt(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            String name = row.getCell(LAST_NAME_INDEX).getStringCellValue();
            String lastName = row.getCell(NAME_INDEX).getStringCellValue();
            String nameOfFather = null;

            inn.setLastName(name);
            inn.setName(lastName);
            long passport = 0;
            LocalDateTime date = null;
            try {
                 passport = (long) row.getCell(PASSPORT_INDEX).getNumericCellValue();
                 date = row.getCell(DATE_INDEX).getLocalDateTimeCellValue();
            } catch (NullPointerException e) {

            }


            if (passport == 0 || date == null) {
                continue;
            }
            inn.setPassport(passport);
            inn.setDate(date.format(formatter));

            nameOfFather = row.getCell(NAME_OF_FATHER_INDEX).getStringCellValue();
            inn.setNameOfFather(nameOfFather);

            InnPageActions innPageActions = new InnPageActions(open("https://service.nalog.ru/inn.html", InnPage.class));
            innPageActions.clickAgree();
            innPageActions.fillPersonInnDate(inn);
            int a = i;

            boolean result = innPageActions.isExistInn();

            if (result == false) {
                System.out.print(++a + " ");
                System.out.println(
                        lastName + " " +
                                name + " " +
                                nameOfFather + " " +
                                date.format(formatter) + " " +
                                passport);
            }
        }
    }

    public static void parsePDF() throws TesseractException {
        ITesseract image = new Tesseract();
        image.setDatapath("src/main/resources/tessdata");
        image.setLanguage("rus");
        String path = "src/main/resources/parse.png";

        String str = image.doOCR(new File(path));
        System.out.println(str);
    }

    public static void changeCellBackgroundColor(Cell cell) {
        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle == null) {
            cellStyle = cell.getSheet().getWorkbook().createCellStyle();
        }
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(cellStyle);
    }
}
