package actions;

import pages.PassportValidResultPage;

public class PassportValidResultPageActions {

    private final PassportValidResultPage passportValidResultPage;

    private final String expectedResultMessage = "По Вашему запросу о действительности паспорта РФ %s № %s получен ответ о том, что данный паспорт «Cреди недействительных не значится».";

    public PassportValidResultPageActions(PassportValidResultPage passportValidResultPage) {
        this.passportValidResultPage= passportValidResultPage;
    }

    public boolean isValid(long series, long number) {
        String strNumber = String.valueOf(number);
        String strSeries = String.valueOf(series);
        if (strNumber.length() == 5) {
            strNumber = "0" + strNumber;
        }
        if (strNumber.length() == 4) {
            strNumber = "00" + strNumber;
        }
        if (strSeries.length() == 3) {
            strSeries = "0" + strSeries;
        }
        while (true) {
            if (passportValidResultPage.getResult().isDisplayed()) {
                return passportValidResultPage.getResult().getText().equals(String.format(expectedResultMessage, strSeries, strNumber));
            }
        }


    }
}
