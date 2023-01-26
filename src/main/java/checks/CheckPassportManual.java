package checks;

import actions.PassportValidPageActions;
import actions.PassportValidResultPageActions;
import model.Person;
import org.apache.poi.xssf.usermodel.XSSFRow;
import pages.PassportValidPage;
import pages.PassportValidResultPage;

import java.util.Scanner;

import static checks.CheckRuInn.createPersonForInn;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;

public class CheckPassportManual implements ICheck {
    @Override
    public void run(XSSFRow row) {
        Person person;
        person = createPersonForInn(row);

        String series = person.getPassport().substring(0, 4);
        String number = person.getPassport().substring(4, 10);

        PassportValidPageActions passportValidPageActions;
        PassportValidResultPageActions passportValidResultPageActions;
        passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
        passportValidPageActions.fillData(series, number);

        String captcha = new Scanner(System.in).next();
        if (captcha.equals("Q")) {
            run(row);
        }
        passportValidPageActions.getCaptchaInput().sendKeys(captcha);
        passportValidPageActions.getSubmit().click();
        passportValidResultPageActions = new PassportValidResultPageActions(page(PassportValidResultPage.class));

        boolean result = passportValidResultPageActions.isValid(series, number);

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
