import actions.*;
import com.codeborne.selenide.Configuration;
import com.twocaptcha.TwoCaptcha;
import excel.ExcelUtils;
import model.PersonModel;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import pages.FSSPCaptchaPage;
import pages.FSSPPage;
import pages.InnPage;
import pages.PassportValidPage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static excel.ExcelUtils.getBackgroundColor;

public class Main {

    private final static String INN_DOCUMENT_PATH = "src/main/resources/20.09.xlsx";
    private final static String PASSPORT_DOCUMENT_PATH = "src/main/resources/20.09.xlsx";
    private final static int LAST_NAME_INDEX = 0;
    private final static int NAME_INDEX = 1;
    private final static int NAME_OF_FATHER_INDEX = 2;
    private final static int DATE_INDEX = 3;
    private final static int PASSPORT_INDEX = 4;

    private final static String CAPTCHA_API_KEY = "d80ed595bdf19e4399db95b673fe281d";

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void main(String[] args) throws IOException, TesseractException, InterruptedException {
        Configuration.holdBrowserOpen = true;
        Configuration.timeout = 15000;
        Configuration.pageLoadTimeout = 120000;
        Configuration.remoteReadTimeout = 120000;
        System.out.println("Действительность");
        // Действительность
        checkValidOfPassport(PASSPORT_DOCUMENT_PATH);
        System.out.println("ИНН");
        // ИНН
        checkAvailabilityOfInn(INN_DOCUMENT_PATH);


        // Долги
        //checkDebt(INN_DOCUMENT_PATH);
    }

    private static void checkDebt(String path) throws IOException {
        TwoCaptcha solver = new TwoCaptcha(CAPTCHA_API_KEY);
        XSSFWorkbook workbook = ExcelUtils.getWorkBookForRead(path);
        XSSFSheet sheet = workbook.getSheetAt(0);
        PersonModel person;
        for (int i = 207; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            try {
                person = createPersonModel(row);
            } catch (NullPointerException e) {
                continue;
            }
            final String requestStr = person.getLastName() + " " + person.getName() + " " + (person.getNameOfFather() == null ? "" : person.getNameOfFather()) + " " + person.getDate();

            FSSPPageActions fsspPageActions = new FSSPPageActions(open("https://fssp.gov.ru/", FSSPPage.class));
            FSSPCaptchaPageActions fsspCaptchaPageActions = new FSSPCaptchaPageActions(fsspPageActions.fillData(requestStr));

            try {
                fsspCaptchaPageActions.resolvCaptcha();
            } catch (Exception e) {
                System.out.println("Не расшифровали");
            }

        }
    }

    private static void checkValidOfPassport(String path) throws IOException, TesseractException, InterruptedException {
        XSSFWorkbook workbook = ExcelUtils.getWorkBookForRead(path);
        XSSFSheet sheet = workbook.getSheetAt(0);
        PersonModel person;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            try {
                person = createPersonModel(row);
            } catch (NullPointerException e) {
                continue;
            }

            String series = person.getPassport().substring(0, 4);
            String number = person.getPassport().substring(4, 10);

            PassportValidPageActions passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
            passportValidPageActions.fillData(series, number);
            PassportValidResultPageActions passportValidResultPageActions = new PassportValidResultPageActions(passportValidPageActions.resolveCaptcha());
            int a = i;

            boolean result = passportValidResultPageActions.isValid(series, number);

            if (result == false) {
                System.out.println();
                System.out.print(++a + " ");
                System.out.print(result + " ");
                System.out.println(
                        person.getLastName() + " " +
                                person.getName() + " " +
                                person.getNameOfFather() + " " +
                                person.getPassport());
            } else {
                System.out.print(++a + " ");
            }
        }
    }

    public static void checkAvailabilityOfInn(String path) throws IOException {
        List<Integer> list = new ArrayList<>();
        XSSFWorkbook workbook = ExcelUtils.getWorkBookForRead(path);
        XSSFSheet sheet = workbook.getSheetAt(0);
        PersonModel person;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {

            XSSFRow row = sheet.getRow(i);
            try {
                person = createPersonModel(row);
            } catch (NullPointerException e) {
                continue;
            }

            InnPageActions innPageActions = new InnPageActions(open("https://service.nalog.ru/inn.html", InnPage.class));
            innPageActions.clickAgree();
            innPageActions.fillPersonInnDate(person);
            int a = i;

            boolean result = innPageActions.isExistInn();

            if (result == false) {
                list.add(i);
                System.out.print(++a + " ");
                System.out.println(
                        person.getLastName() + " " +
                                person.getName() + " " +
                                person.getNameOfFather() + " " +
                                person.getDate() + " " +
                                person.getPassport());
            }
        }
        //changeColorRows(list);
    }

    public static PersonModel createPersonModel(XSSFRow row) {
        PersonModel person = new PersonModel();
        String backgroundColor = "";

        try {
            backgroundColor = getBackgroundColor(row.getCell(LAST_NAME_INDEX));
        } catch (NullPointerException e) {

        }

        if (backgroundColor.equals("ff0000")) {
            throw new NullPointerException("Помечено красным");
        }

        long numericPassport = (long) row.getCell(PASSPORT_INDEX).getNumericCellValue();
        if (numericPassport == 0) throw new NullPointerException("Нет пасспорта");
        String passport = String.valueOf(numericPassport);
        if (passport.length() == 9) {
            passport = "0" + passport;
        }
        if (passport.length() == 8) {
            passport = "00" + passport;
        }


        person.setLastName(row.getCell(LAST_NAME_INDEX).getStringCellValue());
        person.setPassport(passport);
        person.setDate(row.getCell(DATE_INDEX).getLocalDateTimeCellValue().format(DATE_FORMATTER));
        person.setName(row.getCell(NAME_INDEX).getStringCellValue());

        if (person.getDate() == null) {
            throw new NullPointerException("Пустая дата");
        }

        try {
            person.setNameOfFather(row.getCell(NAME_OF_FATHER_INDEX).getStringCellValue());
        } catch (NullPointerException e) {
            person.setNameOfFather(null);
        }
        return person;
    }

    public static void changeColorRows(List<Integer> list) throws IOException {
        FileInputStream file = new FileInputStream(new File(INN_DOCUMENT_PATH));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        file.close();
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFCellStyle style = workbook.createCellStyle();
        byte[] rgb = new byte[3];
        rgb[0] = (byte) 255; // red
        rgb[1] = (byte) 0; // green
        rgb[2] = (byte) 0; // blue
        XSSFColor myColor = new XSSFColor(rgb);

        style.setFillForegroundColor(myColor);
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (list.contains(i)) {
                XSSFRow row = sheet.getRow(i);
                row.getCell(LAST_NAME_INDEX).setCellStyle(style);
                row.getCell(NAME_INDEX).setCellStyle(style);
                row.getCell(NAME_OF_FATHER_INDEX).setCellStyle(style);
            }
        }
        FileOutputStream fos = new FileOutputStream(new File(INN_DOCUMENT_PATH));

        workbook.write(fos);
        fos.close();
        System.out.println("Done");
    }

}
