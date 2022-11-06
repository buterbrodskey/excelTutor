package pages;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class KadArbitr {
    @FindBy(how = How.XPATH, using = "//div/textarea")
    private SelenideElement fioArea;

    //@FindBy(how = How.XPATH, using = "//button[@type=\"submit\"]")
    @FindBy(how = How.XPATH, using = "//div[@class=\"b-button-container\"]")
    private SelenideElement findButton;

    @FindBy(how = How.XPATH, using = "//li[@class=\"bankruptcy\"]")
    private SelenideElement bankrotButton;

    public SelenideElement getTable() {
        return $(By.xpath("//div[@id=\"table\"]//tbody"));
    }
}

//td[@class="respondent"]//span[contains(text(), "Бондарев Анатолий Иванович")]
