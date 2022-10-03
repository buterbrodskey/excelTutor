package pages;


import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class InnPage {

    @FindBy(how = How.ID, using = "unichk_0")
    private SelenideElement agreeCheckBox;

    @FindBy(how = How.ID, using = "btnContinue")
    private SelenideElement agreeButton;

    @FindBy(how = How.NAME, using = "fam")
    private SelenideElement lastNameInput;

    @FindBy(how = How.NAME, using = "nam")
    private SelenideElement nameInput;

    @FindBy(how = How.NAME, using = "otch")
    private SelenideElement nameOfFatherInput;

    @FindBy(how = How.NAME, using = "bdate")
    private SelenideElement bdateInput;

    @FindBy(how = How.NAME, using = "docno")
    private SelenideElement docnoInput;

    @FindBy(how = How.ID, using = "btn_send")
    private SelenideElement sendButton;

    public SelenideElement getVisibleResult() {
        SelenideElement selenideElement = $(By.xpath("//div[@style=\"display: block;\"]"));
        return selenideElement;
    }
    public SelenideElement getNoOtchCheckBox() {
        return $(By.id("unichk_0"));
    }
}