package pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
@Getter
public class PassportValidResultPage {
    @FindBy(how = How.XPATH, using = "//*[@id=\"wrapper\"]/section/div/div/div[2]/h4")
    private SelenideElement result;
}
