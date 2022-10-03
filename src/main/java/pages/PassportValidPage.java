package pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

@Getter
public class PassportValidPage {
    @FindBy(how = How.ID, using = "form_DOC_SERIE")
    private SelenideElement series;

    @FindBy(how = How.ID, using = "form_DOC_NUMBER")
    private SelenideElement number;

    @FindBy(how = How.ID, using = "captcha_image")
    private SelenideElement captcha;

    @FindBy(how = How.ID, using = "form_captcha-input")
    private SelenideElement captchaInput;

    @FindBy(how = How.ID, using = "form_submit")
    private SelenideElement submit;
}
