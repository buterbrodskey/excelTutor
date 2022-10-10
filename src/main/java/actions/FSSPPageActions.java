package actions;

import pages.FSSPCaptchaPage;
import pages.FSSPPage;

import static com.codeborne.selenide.Selenide.page;

public class FSSPPageActions {

    private final FSSPPage page;

    public FSSPPageActions(FSSPPage page) {
        this.page = page;
    }

    public FSSPCaptchaPage fillData(String str) {
        page.getDebtInput().sendKeys(str);
        page.getSubmitButton().click();
        return page(FSSPCaptchaPage.class);
    }
}
