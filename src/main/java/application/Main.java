package application;

import actions.PassportValidPageActions;
import actions.PassportValidResultPageActions;
import checks.CheckPassportManual;
import checks.CheckRuInn;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import excel.FsspExcelInsert;
import model.Person;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import pages.PassportValidPage;
import pages.PassportValidResultPage;
import utils.InstantImpl;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static excel.ExcelUtils.*;
import static java.lang.String.format;
import static utils.FrontUtils.fillField;
import static utils.FrontUtils.setCookie;
import static utils.InstantImpl.showDuration;
import static utils.InstantImpl.start;

public class Main {
    private final static String BASE_PATH = "src/main/resources/";
    private final static String EXCEL_FILE = "C:/Users/stant/Downloads/fssp/excel.xlsx";
    private final static String TXT_FILE = "C:/Users/stant/Downloads/fssp/excel.txt";
    private final static int LAST_NAME_INDEX = 0;
    private final static int NAME_INDEX = 1;
    private final static int NAME_OF_FATHER_INDEX = 2;
    private final static int DATE_INDEX = 3;
    private final static int PASSPORT_INDEX = 4;

    private final static int INN_INDEX = 12;
    private final static int PASSPORT_INVALID_MESSAGE_INDEX = 7;

    private final static String CAPTCHA_API_KEY = "d80ed595bdf19e4399db95b673fe281d";

    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int INN_INDEX_RU = 5;


    public static void main(String[] args) throws IOException, TesseractException, InterruptedException {
        //Configuration.holdBrowserOpen = true;
        Configuration.timeout = 120000;
        Configuration.pageLoadTimeout = 120000;
        Configuration.remoteReadTimeout = 120000;
        Configuration.browser = "edge";

        //preparePassport(BASE_PATH+"1539_05.03.xlsx");
        //Executor.run(BASE_PATH+"1100.xlsx", new CheckPassportManual(), 985);
        Executor.run(BASE_PATH+"basic.xlsx", new CheckRuInn(), 11354);
        //checkValidOfPassport(BASE_PATH+"1539_05.03.xlsx");
        //copyInn(BASE_PATH+"final_test.xlsx");
        //checkFedresurs(BASE_PATH + "basic.xlsx", 1);
        //fromTxtToXlsx();
        //moveIncorrectMonthToDay(BASE_PATH + "basic.xlsx", 1);
    }

    private static void moveIncorrectMonthToDay(String file, int start) throws IOException {
        start();
        var wb = readWorkbook(file);
        var sheet = wb.getSheetAt(0);
        XSSFCell cellDate;
        XSSFCell cellInn;
        try {
            for (int i = 11354; i <= sheet.getLastRowNum(); i++) {
                cellDate = sheet.getRow(i).getCell(3);
                cellInn = sheet.getRow(i).getCell(5);
                try {
                    if (cellInn.getStringCellValue().contains("не найден")) {
                        String[] strings = cellDate.getLocalDateTimeCellValue().format(DATE_FORMATTER).split("\\.");
                        cellDate.setCellValue(format("%s.%s.%s", strings[1], strings[0], strings[2]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            writeWorkbook(file);
            showDuration();
        }
    }

    private static void moveDate(String file) throws IOException {
        start();
        var wb = readWorkbook(file);
        var sheet = wb.getSheetAt(0);
        XSSFCell cell;
        try {
            for (int i = 11354; i <= sheet.getLastRowNum(); i++) {
                cell = sheet.getRow(i).getCell(3);
                try {
                    if (cell.getStringCellValue().contains("/")) {
                        String[] strings = cell.getStringCellValue().split("/");
                        cell.setCellValue(format("%s.%s.%s", strings[1], strings[0], strings[2]));
                    }
                } catch (Exception e) {

                }
            }
        } finally {
            writeWorkbook(file);
            showDuration();
        }
    }

    private static void fromTxtToXlsx() throws IOException {
        var inputStream = new FileInputStream(EXCEL_FILE);
        var workbook = new XSSFWorkbook(inputStream);
        inputStream.close();
        var sheet = workbook.getSheetAt(0);
        var scanner = new Scanner(new File(TXT_FILE)).useDelimiter("\n");
        Person person = new Person();
        try {
            while (scanner.hasNext()) {
                String personString = scanner.next();
                System.out.println(personString);
                System.out.println(getFio(personString));
                System.out.println(getDate(personString));
                System.out.println(getDebtor(personString));
                FsspExcelInsert.insertResult(sheet, getFio(personString),getDate(personString),getDebtor(personString));
            }
        } finally {
            workbook.write(new FileOutputStream(EXCEL_FILE));
        }
        scanner.close();
    }

    private static String getDebtor(String personString) {
        String debtor = "нет данных";
        if (personString.contains("Должник:")) {
            return personString.substring(personString.indexOf("Должник:") + 8).trim();
        } else {
            return debtor;
        }
    }

    private static String getDate(String personString) {
        String date;
        try {
            date = personString.substring(personString.indexOf("ДР:") +3, personString.indexOf("| До")).trim();
        } catch (Exception e) {
            date = personString.substring(personString.indexOf("ДР:") +3).trim();
        }
        return date;
    }

    private static String getFio(String str) {
        String fio;
        fio = str.substring(5, str.indexOf("|")).trim();
        return fio;
    }

    private static void moveToStringFssp() throws IOException {
        var inputStream = new FileInputStream(EXCEL_FILE);
        var workbook = new XSSFWorkbook(inputStream);
        inputStream.close();
        var sheet = workbook.getSheetAt(0);
        var writter = new FileWriter(TXT_FILE);
        Person person = new Person();
        try {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                person.setLastName(sheet.getRow(i).getCell(0).getStringCellValue());
                person.setName(sheet.getRow(i).getCell(1).getStringCellValue());
                person.setNameOfFather(sheet.getRow(i).getCell(2).getStringCellValue());
                person.setDate(sheet.getRow(i).getCell(3).getLocalDateTimeCellValue().format(DATE_FORMATTER));

                writter
                        .append("ФИО: ").append(person.getLastName()).append(" ")
                        .append(person.getName()).append(" ")
                        .append(person.getNameOfFather()).append(" ")
                        .append("|").append(" ")
                        .append("ДР:")
                        .append(person.getDate())
                        .append("\n");
            }
        } finally {
            writter.flush();
            writter.close();
        }
    }

    private static void preparePassport(String s) throws IOException {
        File filePath = new File(s);
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        file.close();
        try {
            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                System.out.println(i);
                sheet.getRow(i).getCell(4).setCellType(CellType.STRING);
            }
        } finally {
            FileOutputStream outputStream = new FileOutputStream(s);
            workbook.write(outputStream);
        }
    }

    public static void copyInn(String path) throws IOException {
        start();
        XSSFWorkbook workbook = readWorkbook(path);
        try {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                var row = sheet.getRow(i);
                var passport = row.getCell(4).getStringCellValue();
                var inn = row.getCell(5).getStringCellValue();
                writeInnInCaseList(passport, inn, path);
            }
        } finally {
            writeWorkbook(path);
            showDuration();
        }
    }

    private static void writeInnInCaseList(String passport, String inn, String path) throws IOException {
        start();
        XSSFWorkbook workbook = readWorkbook(path);
        try {
            XSSFSheet sheet = workbook.getSheetAt(1);
            XSSFCell passportCell;
            XSSFRow row;
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                passportCell = row.getCell(2);
                if (passportCell.getStringCellValue().equals(passport)) {
                    row.createCell(3).setCellValue(inn);
                }
            }
        } finally {
            writeWorkbook(path);
            showDuration();
        }
    }

    private static void tabFour() throws IOException {
        File filePath = new File("src/main/resources/test.xlsx");
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFWorkbook outWorkbook = new XSSFWorkbook();
        XSSFSheet outSheet = outWorkbook.createSheet();
        int d = 1;
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            XSSFRow row = outSheet.createRow(d);
            row.createCell(LAST_NAME_INDEX).setCellValue(sheet.getRow(i).getCell(LAST_NAME_INDEX).getStringCellValue());
            row.createCell(NAME_INDEX).setCellValue(sheet.getRow(i).getCell(NAME_INDEX).getStringCellValue());
            row.createCell(NAME_OF_FATHER_INDEX).setCellValue(sheet.getRow(i).getCell(NAME_OF_FATHER_INDEX).getStringCellValue());
            try {
                row.createCell(DATE_INDEX).setCellValue(sheet.getRow(i).getCell(DATE_INDEX).getStringCellValue());
            } catch (Exception e) {
                row.createCell(DATE_INDEX).setCellValue(sheet.getRow(i).getCell(DATE_INDEX).getLocalDateTimeCellValue().format(DATE_FORMATTER));
            }
            try {
                row.createCell(PASSPORT_INDEX).setCellValue(sheet.getRow(i).getCell(PASSPORT_INDEX).getStringCellValue());
            } catch (Exception e) {
                row.createCell(PASSPORT_INDEX).setCellValue(sheet.getRow(i).getCell(PASSPORT_INDEX).getNumericCellValue());
            }
            d = i * 5 + 1;
        }
        FileOutputStream outputStream = new FileOutputStream("./src/main/resources/otstup1.xlsx");
        outWorkbook.write(outputStream);
    }


    private static void checkValidOfPassport(String path) throws IOException, TesseractException, InterruptedException {
        File filePath = new File(path);
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        file.close();
        XSSFSheet sheet = workbook.getSheetAt(0);
        try {
            Person person;
            for (int i = 29; i <= sheet.getLastRowNum(); i++) {

                XSSFRow row = sheet.getRow(i);
                try {
                    person = createPersonForInn(row);
                } catch (NullPointerException e) {
                    continue;
                }
                System.out.println(" " + person.getPassport());
                String series = person.getPassport().substring(0, 4);
                String number = person.getPassport().substring(4, 10);

                PassportValidPageActions passportValidPageActions;
                PassportValidResultPageActions passportValidResultPageActions;
                try {
                    if (i == 1) {
                        passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
                        setCookie("uid=Usq9fmPOz1EudV+CVgrrAg==; _ga=GA1.3.411278694.1674497895; JSESSIONID=619351d2b1c8e77625812d1a2d7b; _gid=GA1.3.342324882.1678091730");
                        passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
                    } else {
                        passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
                    }

                    passportValidPageActions.fillData(series, number);
                    passportValidResultPageActions = new PassportValidResultPageActions(passportValidPageActions.resolveCaptcha(1));
                } catch (Exception e) {
                    i -= 2;
                    continue;
                }

                int a = i;

                boolean result = passportValidResultPageActions.isValid(series, number);

                if (!result) {
                    row.createCell(13).setCellValue("не действителен");
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
        } finally {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            outputStream.close();
        }
    }

    public static void checkPassportManual(String path) throws IOException {
        File filePath = new File(path);
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        file.close();
        Person person;
        Scanner scanner = new Scanner(System.in);
        try {
            for (int i = 570; i <= sheet.getLastRowNum(); i++) {

                XSSFRow row = sheet.getRow(i);
                try {
                    person = createPersonForInn(row);
                } catch (NullPointerException e) {
                    continue;
                }
                if (i != 0) {
                    //sleep(10000);
                }
                String series = person.getPassport().substring(0, 4);
                String number = person.getPassport().substring(4, 10);

                PassportValidPageActions passportValidPageActions;
                PassportValidResultPageActions passportValidResultPageActions;
                passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
                passportValidPageActions.fillData(series, number);

                String captcha = scanner.next();
                if (captcha.equals("Q")) {
                    i -= 1;
                    continue;
                }
                passportValidPageActions.getCaptchaInput().sendKeys(captcha);
                passportValidPageActions.getSubmit().click();
                passportValidResultPageActions = new PassportValidResultPageActions(page(PassportValidResultPage.class));

                int a = i;

                boolean result = passportValidResultPageActions.isValid(series, number);

                if (!result) {
                    row.createCell(13).setCellValue("не действителен");

                    System.out.println();
                    System.out.print(++a + " ");
                    System.out.print(result + " ");
                    System.out.printf("%s %s %s %s%n", person.getLastName(), person.getName(), person.getNameOfFather(), person.getPassport());
                } else {
                    System.out.print(++a + " ");
                }
                FileOutputStream outputStream = new FileOutputStream(filePath);
                workbook.write(outputStream);
                outputStream.close();
            }
        } finally {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            outputStream.close();
        }
    }

    public static void checkFedresurs(String path, int index) throws IOException {
        start();
        File filePath = new File(path);
        FileInputStream file = new FileInputStream(filePath);
        ZipSecureFile.setMinInflateRatio(-1.0d);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        file.close();
        try {
            Person person;
            for (int i = index; i <= rowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                try {
                    person = createPersonModelForFedresurs(row);
                } catch (NullPointerException e) {
                    continue;
                }
                //if (row.getCell(6).getStringCellValue().equals("не нашли")) continue;
                if (i == index)
                    open("https://bankrot.fedresurs.ru/bankrupts?regionId=all&isActiveLegalCase=null&offset=0&limit=15");
                switchTo().window(0);
                SelenideElement selenideElement = $(By.xpath("//input[@formcontrolname='searchString']"));
                selenideElement.clear();
                fillField(selenideElement, person.getLastName() + " " + person.getName() + " " + person.getNameOfFather());
                $(By.xpath("//button")).click();
                sleep(2000);
                if ($(withText("Физические лица")).is(Condition.visible)) $(withText("Физические лица")).click();
                if ($(By.xpath("//button[text()=' Принять ']")).is(Condition.visible))
                    $(By.xpath("//button[text()=' Принять ']")).click();
                while ($(withText("Загрузить еще")).is(Condition.visible)) {
                    $(withText("Загрузить еще")).click();
                    sleep(1000);
                }
                List<SelenideElement> list = $$(By.xpath("//app-bankrupt-result-card-person"));
                boolean result = false;
                System.out.print(person.getIndex() + " ");
                if (!list.isEmpty()) {
                    if (list.size() > 1) {
                        System.out.println(" количество дел: " + list.size());
                    } else System.out.println("найдено одно дело");
                    for (SelenideElement element : list) {
                        element.find(By.xpath("./div/div/el-info-link")).click();
                        switchTo().window(1);
                        String birthday;
                        String inn;
                        $(withText("Физическое лицо")).shouldBe(Condition.visible);
                        if ($(withText("Дата рождения:")).is(Condition.exist)) {
                            birthday = $(withText("Дата рождения:")).parent().$(By.xpath("./div/div")).getText().trim();
                            if (birthday.equals(person.getDate())) {
                                result = true;
                                System.out.println("Дата совпала");
                                break;
                            } else {
                                System.out.println("Дата не совпала");
                                result = false;
                            }
                        } else {
                            inn = $(withText("ИНН:")).parent().$(By.xpath("./div[1]")).getText().trim();
                            if (inn.equals(row.getCell(6).getStringCellValue())) {
                                result = true;
                                System.out.println("Инн совпал");
                                break;
                            } else {
                                System.out.println("Инн не совпал");
                                result = false;
                            }
                        }
                        closeWindow();
                        switchTo().window(0);
                    }
                }
                if (!result) {
                    System.out.println(/*person.getIndex() + */"не наш");
                    //row.createCell(9).setCellValue("не нашли");
                } else {
                    System.out.print(/*person.getIndex() + */"наш ");
                    System.out.println(
                            person.getLastName() + " " +
                                    person.getName() + " " +
                                    person.getNameOfFather() + " " +
                                    person.getDate());
                    XSSFCell cell = row.createCell(9);
                    cell.setCellValue("наш");
                    createLinkCell("наш", WebDriverRunner.url(), cell);
                    closeWindow();
                }
            }
        } finally {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            outputStream.close();
            showDuration();
        }
    }

    public static Person createPersonModelForFedresurs(XSSFRow row) {
        Person person = new Person();
        String lastName = row.getCell(LAST_NAME_INDEX).getStringCellValue().replace((char) 160, 'e');
        person.setLastName(lastName.replace("e", ""));
        try {
            person.setDate(row.getCell(DATE_INDEX).getLocalDateTimeCellValue().format(DATE_FORMATTER));
        } catch (Exception e) {
            person.setDate(row.getCell(DATE_INDEX).getStringCellValue());
        }
        person.setName(row.getCell(NAME_INDEX).getStringCellValue().replace((char) 160, 'e').replace("e", ""));

        if (person.getDate() == null) {
            throw new NullPointerException("Пустая дата");
        }

//        String inn = row.getCell(6).getStringCellValue();
//        if (inn != null || inn.length() == 12) {
//            person.setInn(inn);
//        }

        try {
            person.setNameOfFather(row.getCell(NAME_OF_FATHER_INDEX).getStringCellValue().trim());
        } catch (NullPointerException e) {
            person.setNameOfFather(null);
        }

        person.setIndex(row.getRowNum());
        return person;
    }

    public static Person createPersonForInn(XSSFRow row) {
        Person person = new Person();
        String passport = row.getCell(4).getStringCellValue();
        if (passport == null) throw new NullPointerException("Нет пасспорта");
        if (passport.length() == 9) {
            passport = "0" + passport;
        }
        if (passport.length() == 8) {
            passport = "00" + passport;
        }
        person.setPassport(passport);
        String lastName = row.getCell(0).getStringCellValue().replace((char) 160, 'e');
        person.setLastName(lastName.replace("e", ""));
        try {
            person.setDate(row.getCell(3).getLocalDateTimeCellValue().format(DATE_FORMATTER));
        } catch (Exception e) {
            person.setDate(row.getCell(3).getStringCellValue());
        }
        person.setName(row.getCell(1).getStringCellValue().replace((char) 160, 'e').replace("e", ""));

        if (person.getDate() == null) {
            throw new NullPointerException("Пустая дата");
        }

        try {
            person.setNameOfFather(row.getCell(2).getStringCellValue().trim());
        } catch (NullPointerException e) {
            person.setNameOfFather(null);
        }
        return person;
    }


    public static Person createKirgPersonModel(XSSFRow row) {
        Person person = new Person();
        String backgroundColor = "";

        try {
            backgroundColor = getBackgroundColor(row.getCell(LAST_NAME_INDEX));
        } catch (NullPointerException e) {

        }

        if (backgroundColor.equals("ff0000")) {
            throw new NullPointerException("Помечено красным");
        }

        String numericPassport = row.getCell(PASSPORT_INDEX).getStringCellValue();
        if (numericPassport == null) throw new NullPointerException("Нет пасспорта");
        String passport = numericPassport;


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
}
