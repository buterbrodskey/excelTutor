package checks;

import actions.PassportValidPageActions;
import actions.PassportValidResultPageActions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import model.Person;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.openqa.selenium.By;
import pages.PassportValidPage;

import static checks.CheckRuInn.createPersonForInn;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CheckPassports implements ICheck {
    @Override
    public void run(XSSFRow row) {
        Configuration.browser = "edge";
        Person person;
        person = createPersonForInn(row);

        System.out.println(" " + person.getPassport());
        String series = person.getPassport().substring(0, 4);
        String number = person.getPassport().substring(4, 10);

        PassportValidPageActions passportValidPageActions;
        PassportValidResultPageActions passportValidResultPageActions = null;
        try {
            passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
            passportValidPageActions.fillData(series, number);
            passportValidResultPageActions = new PassportValidResultPageActions(passportValidPageActions.resolveCaptcha(series, number));
        } catch (Exception e) {
            System.out.println("ошибка на " + person.getIndex());
        }
        if ($(By.className("b-logo-title")).is(not(visible))) {
            Selenide.refresh();
        }
        boolean result = passportValidResultPageActions.isValid(series, number);
        if ($(By.className("b-logo-title")).is(not(visible))) {
            Selenide.refresh();
        }
        if (!result) {
            row.createCell(13).setCellValue("не действителен");
            System.out.println();
            System.out.print(person.getIndex() + " ");
            System.out.print(result + " ");
            System.out.println(
                    person.getLastName() + " " +
                            person.getName() + " " +
                            person.getNameOfFather() + " " +
                            person.getPassport());
        } else {
            System.out.print(person.getIndex() + " ");
        }

    }
}
