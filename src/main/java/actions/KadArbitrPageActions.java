package actions;

import com.codeborne.selenide.SelenideElement;
import model.PersonModel;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import pages.KadArbitr;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class KadArbitrPageActions {
    private KadArbitr kadArbitr;

    public KadArbitrPageActions(KadArbitr kadArbitr) {
        this.kadArbitr = kadArbitr;
    }

    public void fillFio(PersonModel person) {
        fillField(kadArbitr.getFioArea(), person.getLastName() + " " + person.getName() + " " + person.getNameOfFather());
    }

    public void clickSubmit() {
        JavascriptExecutor jse;
        jse = (JavascriptExecutor)getWebDriver();
        jse.executeScript("document.getElementById('oauth-auth-forgot-link').click();");
        kadArbitr.getFindButton().doubleClick();
    }

    public SelenideElement getTableResponse() {
        return kadArbitr.getTable();
    }

    private void fillField(SelenideElement input, String value) {
        if (input.getAttribute("name").equals("otch") && (value == null || value.isEmpty())) {
            return;
        }
        StringSelection stringSelection = new StringSelection(value);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String currentInputText = null;
        while (currentInputText == null || currentInputText.isEmpty()) {
            clipboard.setContents(stringSelection, null);
            input.click();
            input.sendKeys(Keys.CONTROL, "V");
            currentInputText = input.getValue();
        }
    }
}
