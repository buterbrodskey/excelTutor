package model.check;

import actions.PassportValidPageActions;
import actions.PassportValidResultPageActions;
import model.PersonModel;
import model.fill.FillStrategy;
import net.sourceforge.tess4j.TesseractException;
import pages.PassportValidPage;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.open;

public class RealityPassCheck extends Check {
    @Override
    public String check(PersonModel person) throws TesseractException, IOException, InterruptedException {
        String series = person.getPassport().substring(0, 4);
        String number = person.getPassport().substring(4, 10);

        PassportValidPageActions passportValidPageActions;
        PassportValidResultPageActions passportValidResultPageActions;
        passportValidPageActions = new PassportValidPageActions(open("http://xn--b1afk4ade4e.xn--b1ab2a0a.xn--b1aew.xn--p1ai/info-service.htm?sid=2000", PassportValidPage.class));
        passportValidPageActions.fillData(series, number);
        passportValidResultPageActions = new PassportValidResultPageActions(passportValidPageActions.resolveCaptcha());
        if (!passportValidResultPageActions.isValid(series, number)) {
            return "Не действителен";
        }
        return null;
    }

}
