package model.check;

import actions.InnPageActions;
import model.PersonModel;
import model.fill.FillStrategy;
import pages.InnPage;

import static com.codeborne.selenide.Selenide.open;

public class InnCheck extends Check {
    @Override
    public String check(PersonModel person) {
        InnPageActions innPageActions = new InnPageActions(open("https://service.nalog.ru/inn.html", InnPage.class));
        innPageActions.clickAgree();
        innPageActions.changePassportType();
        innPageActions.fillPersonInnDate(person);

        if (innPageActions.isExistInn()) {
            return innPageActions.getInn();
        }

        return null;
    }
}
