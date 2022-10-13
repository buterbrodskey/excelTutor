package actions;

import com.codeborne.selenide.SelenideElement;
import model.PersonModel;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import pages.InnPage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class InnPageActions {

    private final InnPage page;

    public InnPageActions(InnPage page) {
        this.page = page;
    }

    public void fillPersonInnDate(PersonModel search) {
        fillField(page.getNameInput(), search.getName());
        fillField(page.getLastNameInput(), search.getLastName());
        fillNameOfFather(page.getNameOfFatherInput(), search.getNameOfFather());
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

    private void fillNameOfFather(SelenideElement input, String value) {
        if (value == null || value.equals("")) {
            page.getNoOtchCheckBox().click();
        } else {
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

    public boolean isExistInn() {
        SelenideElement visibleResult = page.getVisibleResult();
        String text = visibleResult.find(By.className("pane-header")).getText();
        if (text.equals("Информация об ИНН найдена.")) return true;
        else if (text.equals("Информация об ИНН не найдена.")) return false;
        else throw new RuntimeException(text);
    }

    public String getInn() {
        return page.getInn();
    }

    public void changePassportType() {
        page.getArrow().click();
        page.getInPass().click();
    }
}
