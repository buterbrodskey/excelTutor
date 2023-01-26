package application;

import actions.InnPageActions;
import actions.PassportValidPageActions;
import actions.PassportValidResultPageActions;
import checks.CheckPassports;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import model.Person;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import pages.InnPage;
import pages.PassportValidPage;
import pages.PassportValidResultPage;
import utils.InstantImpl;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static excel.ExcelUtils.getBackgroundColor;

public class Main {
    private final static String INN_DOCUMENT_PATH = "src/main/resources/20.01.2.xlsx";
    private final static String BANKRUPTS_PATH = "src/main/resources/fedresursChecked.xlsx";
    private final static String PASSPORT_DOCUMENT_PATH = "src/main/resources/1000.xlsx";
    private final static String INN_KIRG_PATH1 = "src/main/resources/12 12 .xlsx";
    private final static String INN_KIRG_PATH2 = "src/main/resources/6 12.xlsx";
    private final static int LAST_NAME_INDEX = 0;
    private final static int NAME_INDEX = 1;
    private final static int NAME_OF_FATHER_INDEX = 2;
    private final static int DATE_INDEX = 3;
    private final static int PASSPORT_INDEX = 4;

    private final static int INN_INDEX = 12;
    private final static int PASSPORT_INVALID_MESSAGE_INDEX = 7;

    private final static String CAPTCHA_API_KEY = "d80ed595bdf19e4399db95b673fe281d";

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int INN_INDEX_RU = 5;

    public static void main(String[] args) throws IOException, TesseractException, InterruptedException {
        //Configuration.holdBrowserOpen = true;
        Configuration.timeout = 15000;
        Configuration.pageLoadTimeout = 120000;
        Configuration.remoteReadTimeout = 120000;
        Configuration.browser = "edge";
        //Executor.run("src/main/resources/700.xlsx", new CheckPassports(), 328);
        checkPassportManual("src/main/resources/700.xlsx");
        //checkValidOfPassport("src/main/resources/700.xlsx");
        //checkAvailabilityOfInn(INN_DOCUMENT_PATH);
        //preparePassport("src/main/resources/700.xlsx");
        //new Executor().run("src/main/resources/700.xlsx", new CheckRuInn());
        //fillRuInn("src/main/resources/2progon3k.xlsx");
        //checkFedresurs(BANKRUPTS_PATH, 5991);
        //tabFour();
        //fillRuInn("src/main/resources/20.01.1.xlsx");
        //fillRuInn("src/main/resources/20.01.3.xlsx");
    }

    private static void preparePassport(String s) throws IOException {
        File filePath = new File(s);
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        file.close();
        try {
            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                System.out.println(i);
                sheet.getRow(i).getCell(4).setCellType(CellType.STRING);
            }
        } finally {
            FileOutputStream outputStream = new FileOutputStream(s);
            workbook.write(outputStream);
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
            for (int i = 321; i <= sheet.getLastRowNum(); i++) {

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
                    passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
                    passportValidPageActions.fillData(series, number);
                    passportValidResultPageActions = new PassportValidResultPageActions(passportValidPageActions.resolveCaptcha());
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
            for (int i = 341; i <= sheet.getLastRowNum(); i++) {

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
                    System.out.println(
                            person.getLastName() + " " +
                                    person.getName() + " " +
                                    person.getNameOfFather() + " " +
                                    person.getPassport());
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
        InstantImpl.start();
        File filePath = new File(path);
        FileInputStream file = new FileInputStream(filePath);
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
                if (row.getCell(6).getStringCellValue().equals("не нашли")) continue;
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
                        if ($(withText("Дата рождения:")).is(Condition.visible)) {
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
                    row.createCell(6).setCellValue("не нашли");
                } else {
                    System.out.print(/*person.getIndex() + */"наш ");
                    System.out.println(
                            person.getLastName() + " " +
                                    person.getName() + " " +
                                    person.getNameOfFather() + " " +
                                    person.getDate());
                    XSSFCell cell = row.createCell(6);
                    cell.setCellValue("наш");
                    createLinkCell("наш", WebDriverRunner.url(), cell, workbook);
                    closeWindow();
                }
            }
        } finally {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            outputStream.close();
            InstantImpl.showDuration();
        }
    }

    private static void createLinkCell(String label, String url, XSSFCell cell, XSSFWorkbook workbook) {
        XSSFHyperlink link = new XSSFCreationHelper(workbook).createHyperlink(HyperlinkType.URL);
        link.setLabel(label);
        link.setAddress(url);
        cell.setHyperlink(link);
    }

    public static void checkAvailabilityOfInn(String path) throws IOException, InterruptedException {
        File filePath = new File(path);
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        file.close();
        Person person;
        for (int i = 1; i <= rowNum; i++) {
            FileInputStream fileWork = new FileInputStream(filePath);
            XSSFWorkbook workbookWork = new XSSFWorkbook(fileWork);
            XSSFSheet sheetWork = workbookWork.getSheetAt(0);
            XSSFRow row = sheetWork.getRow(i);
            fileWork.close();
            try {
                person = createPersonForInn(row);
            } catch (NullPointerException e) {
                continue;
            }
            InnPageActions innPageActions = new InnPageActions(open("https://service.nalog.ru/inn.html", InnPage.class));
            innPageActions.clickAgree();
            innPageActions.fillPersonInnDate(person);
            int a = i;

            boolean result = innPageActions.isExistInn();

            if (!result) {
                System.out.print(++a + " ");
                System.out.println(
                        person.getLastName() + " " +
                                person.getName() + " " +
                                person.getNameOfFather() + " " +
                                person.getDate() + " " +
                                person.getPassport());
            } else {
                System.out.print(++a + " ");
                String inn = innPageActions.getInn();
                System.out.println("найден " + inn);
                XSSFCell cell = row.createCell(INN_INDEX_RU);
                cell.setCellValue(inn);
                FileOutputStream outputStream = new FileOutputStream(filePath);
                workbookWork.write(outputStream);
                outputStream.close();
            }
        }


    }

    private static void fillField(SelenideElement input, String value) {
        if (input.getAttribute("name").equals("otch") && (value == null || value.isEmpty())) {
            return;
        }
        StringSelection stringSelection = new StringSelection(value);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String currentInputText = null;
        while (currentInputText == null || currentInputText.isEmpty()) {
            try {
                clipboard.setContents(stringSelection, null);
            } catch (Exception e) {
                sleep(2000);
                clipboard.setContents(stringSelection, null);
            }
            input.click();
            input.sendKeys(Keys.CONTROL, "V");
            currentInputText = input.getValue();
        }
    }

    public static void fillRuInn(String path) throws IOException, InterruptedException {
        File filePath = new File(path);
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        file.close();
        XSSFSheet sheet = workbook.getSheetAt(0);
        try {
            Person person;
            for (int i = 1;
                 i <= sheet.getLastRowNum();
                 i++) {

                XSSFRow row = sheet.getRow(i);
                try {
                    person = createPersonForInn(row);
                } catch (NullPointerException e) {
                    continue;
                }

                InnPageActions innPageActions = new InnPageActions(open("https://service.nalog.ru/inn.html", InnPage.class));
                innPageActions.clickAgree();
                //innPageActions.changePassportType();
                innPageActions.fillPersonInnDate(person);
                int a = i;

                boolean result = innPageActions.isExistInn();

                if (!result) {
                    System.out.print(++a + " ");
                    System.out.println(
                            person.getLastName() + " " +
                                    person.getName() + " " +
                                    person.getNameOfFather() + " " +
                                    person.getDate() + " " +
                                    person.getPassport());
                } else {
                    String inn = innPageActions.getInn();
                    XSSFCell cell = row.createCell(5);
                    cell.setCellValue(inn);
                }
            }
        } finally {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            outputStream.close();
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

        String inn = row.getCell(5).getStringCellValue();
        if (inn != null || inn.length() == 12) {
            person.setInn(inn);
        }

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
//        String backgroundColor = "";
//
//        try {
//            backgroundColor = getBackgroundColor(row.getCell(1)).trim();
//        } catch (NullPointerException e) {
//
//        }
//
//        if (backgroundColor.equals("ff0000")) {
//            throw new NullPointerException("Помечено красным");
//        }
//
        String passport = row.getCell(4).getStringCellValue();
        if (passport == null) throw new NullPointerException("Нет пасспорта");
        if (passport.length() == 9) {
            passport = "0" + passport;
        }
        if (passport.length() == 8) {
            passport = "00" + passport;
        }
        String lastName = row.getCell(0).getStringCellValue().replace((char) 160, 'e');
        person.setLastName(lastName.replace("e", ""));
        person.setPassport(passport);
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
