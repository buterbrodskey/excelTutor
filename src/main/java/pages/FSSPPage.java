package pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
@Getter
public class FSSPPage {

    @FindBy(how = How.ID, using = "debt-form01")
    private SelenideElement debtInput;

    @FindBy(how = How.XPATH, using = "//button[@class='btn btn-primary']")
    private SelenideElement submitButton;
}
