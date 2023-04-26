package checks;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import model.Person;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.openqa.selenium.By;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static excel.ExcelUtils.createLinkCell;
import static utils.FrontUtils.fillField;

public class CheckFedresurs implements ICheck {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public void run(XSSFRow row) {
        Person person = createPersonModelForFedresurs(row);

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
            System.out.printf("%dне наш%n", person.getIndex());
            row.createCell(6).setCellValue("не нашли");
        } else {
            System.out.printf("%d %s наш%n", person.getIndex(), person.getLastName());
            XSSFCell cell = row.createCell(6);
            cell.setCellValue("наш");
            createLinkCell("наш", WebDriverRunner.url(), cell);
            closeWindow();
        }
    }

    public static Person createPersonModelForFedresurs(XSSFRow row) {
        Person person = new Person();
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

        String inn = row.getCell(5).getStringCellValue();
        if (inn != null || inn.length() == 12) {
            person.setInn(inn);
        }

        try {
            person.setNameOfFather(row.getCell(2).getStringCellValue().trim());
        } catch (NullPointerException e) {
            person.setNameOfFather(null);
        }

        person.setIndex(row.getRowNum());
        return person;
    }
}
