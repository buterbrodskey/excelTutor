package utils;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;

public class FrontUtils {
    public static void fillField(SelenideElement input, String value) {
        if (input.getAttribute("name").equals("otch") && (value == null || value.isEmpty())) {
            return;
        }
        StringSelection stringSelection = new StringSelection(value);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String currentInputText = null;
        while (currentInputText == null || currentInputText.isEmpty()) {
            try {
                clipboard.setContents(stringSelection, null);
            } catch (Exception e) {
                sleep(2000);
                clipboard.setContents(stringSelection, null);
            }
            input.click();
            input.sendKeys(Keys.CONTROL, "V");
            currentInputText = input.getValue();
        }
    }

    public static void setCookie(String s) {
        java.util.List<String> list = List.of(s.replace(" ", "").split(";"));
        for (String s1 : list) {
            WebDriverRunner.getWebDriver().manage().addCookie(new Cookie(s1.split("=", 2)[0], s1.split("=", 2)[1]));
        }
    }
}
