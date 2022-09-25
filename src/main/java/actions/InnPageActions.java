package actions;

import com.codeborne.selenide.SelenideElement;
import model.InnSearchModel;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import pages.InnPage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class InnPageActions {

    public InnPage page;

    public InnPageActions(InnPage page) {
        this.page = page;
    }

    public void fillPersonInnDate(InnSearchModel search) {
        fillField(page.getNameInput(), search.getName());
        fillField(page.getLastNameInput(), search.getLastName());
        fillField(page.getNameOfFatherInput(), search.getNameOfFather());
        fillField(page.getBdateInput(), search.getDate());
        fillField(page.getDocnoInput(), String.valueOf(search.getPassport()));
        page.getSendButton().click();
    }

    public void clickAgree() {
        if (page.getAgreeButton().isDisplayed()) {
            page.getAgreeCheckBox().click();
            page.getAgreeButton().click();
        }
    }

    public void fillField(SelenideElement input, String value) {
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

    public boolean isExistInn() {
        SelenideElement visibleResult = page.getVisibleResult();
        String text = visibleResult.find(By.className("pane-header")).getText();
        if (text.equals("Информация об ИНН найдена.")) return true;
        else if (text.equals("Информация об ИНН не найдена.")) return false;
        else throw new RuntimeException(text);
    }
}
