package actions;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.io.FileHandler;
import pages.PassportValidPage;
import pages.PassportValidResultPage;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.page;

public class PassportValidPageActions {

    private final PassportValidPage page;

    public PassportValidPageActions(PassportValidPage page) {
        this.page = page;
    }

    public void fillData(final long series, final long number) {
        String strNumber = String.valueOf(number);
        String strSeries = String.valueOf(series);
        if (strNumber.length() == 5) {
            strNumber = "0" + strNumber;
        }
        if (strNumber.length() == 4) {
            strNumber = "00" + strNumber;
        }
        if (strSeries.length() == 3) {
            strSeries = "0" + strSeries;
        }
        page.getSeries().sendKeys(strSeries);
        page.getNumber().sendKeys(strNumber);
    }

    public PassportValidResultPage clickSubmit() throws InterruptedException {
        page.getSubmit().click();
        return page(PassportValidResultPage.class);
    }

    public PassportValidResultPage resolveCaptcha() throws IOException, TesseractException, InterruptedException {
        ITesseract image = new Tesseract();
        image.setDatapath("src/main/resources/tessdata");
        image.setLanguage("eng");
        String path = "src/main/java/captcha/captcha.png";

        String capthaResolve = null;

        while (true) {
            File captcha = page.getCaptcha().getScreenshotAs(OutputType.FILE);

            FileHandler.copy(captcha, new File(path));

            String str = image.doOCR(new File(path));

            Pattern pattern = Pattern.compile("\\d\\d\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(str);

            if (matcher.find()) {
                capthaResolve = str.substring(matcher.start(), matcher.end());
            } else {
                page.getSubmit().click();
                continue;
            }
            page.getCaptchaInput().clear();
            page.getCaptchaInput().sendKeys(capthaResolve);
            PassportValidResultPage resultPage = clickSubmit();
            if (resultPage.getResult().isDisplayed()) {
                return resultPage;
            }
        }
    }

}
