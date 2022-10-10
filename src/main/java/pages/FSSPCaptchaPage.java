package pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
@Getter
public class FSSPCaptchaPage {

    @FindBy(how = How.ID, using = "capchaVisual")
    private SelenideElement captchaImage;

    @FindBy(how = How.ID, using = "captcha-popup-code")
    private SelenideElement captchaInput;

    @FindBy(how = How.ID, using = "ncapcha-submit")
    private SelenideElement captchaSubmit;
}
