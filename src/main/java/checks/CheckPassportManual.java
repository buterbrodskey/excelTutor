package checks;

import actions.PassportValidPageActions;
import actions.PassportValidResultPageActions;
import model.Person;
import org.apache.poi.xssf.usermodel.XSSFRow;
import pages.PassportValidPage;
import pages.PassportValidResultPage;

import java.io.IOException;
import java.util.Scanner;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;
import static excel.ExcelUtils.getFile;
import static excel.ExcelUtils.writeWorkbook;
import static utils.FrontUtils.setCookie;

public class CheckPassportManual implements ICheck {
    int count = 0;

    @Override
    public void run(XSSFRow row) {
        Person person = createPersonDefault(row);

        String series = person.getPassport().substring(0, 4);
        String number = person.getPassport().substring(4, 10);
        System.out.printf("%s %s%n", series, number);
        PassportValidPageActions passportValidPageActions;
        PassportValidResultPageActions passportValidResultPageActions;
        if (count == 0) {
            passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
            setCookie("_ga=GA1.2.638371959.1664874160; uid=Usq9fmPGkK4uZl+BU4ozAg==; JSESSIONID=951b677e4efe9c7782a6b3780f06; _gid=GA1.2.189460755.1675193050; _gat=1");
        }
        passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
        ++count;
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

            try {
                writeWorkbook(getFile().getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.printf("%d %s", person.getIndex(), person.getLastName() + " не действителен");
        } else {
            System.out.printf("%d действителен%n", person.getIndex());
        }
    }
}
