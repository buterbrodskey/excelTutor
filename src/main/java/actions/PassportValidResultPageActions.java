package actions;

import pages.PassportValidResultPage;

public class PassportValidResultPageActions {

    private final PassportValidResultPage passportValidResultPage;

    private final String expectedResultMessage = "По Вашему запросу о действительности паспорта РФ %s № %s получен ответ о том, что данный паспорт «Cреди недействительных не значится».";

    public PassportValidResultPageActions(PassportValidResultPage passportValidResultPage) {
        this.passportValidResultPage = passportValidResultPage;
    }

    public boolean isValid(String series, String number) {
        while (true) {
            if (passportValidResultPage.getResult().isDisplayed()) {
                return passportValidResultPage.getResult().getText().equals(String.format(expectedResultMessage, series, number));
            }
        }


    }
}
