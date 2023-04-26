package actions;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Normal;
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

    public void fillData(final String series, final String number) {
        page.getSeries().sendKeys(series);
        page.getNumber().sendKeys(number);
    }

    public PassportValidResultPage clickSubmit() throws InterruptedException {
        page.getSubmit().click();
        return page(PassportValidResultPage.class);
    }

    public PassportValidResultPage resolveCaptcha() throws Exception {
        TwoCaptcha solver = new TwoCaptcha("d80ed595bdf19e4399db95b673fe281d");
        String path = "src/main/java/captcha/fsspCaptcha.png";
        File captchaImage = page.getCaptcha().getScreenshotAs(OutputType.FILE);
        FileHandler.copy(captchaImage, new File(path));

        Normal captcha = new Normal(path);
        solver.solve(captcha);
        System.out.println(captcha.getCode());
        page.getCaptchaInput().sendKeys(captcha.getCode());
        page.getSubmit().click();
        return page(PassportValidResultPage.class);
    }

    public SelenideElement getCaptchaInput() {
        return page.getCaptchaInput();
    }

    public SelenideElement getSubmit() {
        return page.getSubmit();
    }

    public PassportValidResultPage resolveCaptcha(String series, String number) throws Exception {
        TwoCaptcha solver = new TwoCaptcha("d80ed595bdf19e4399db95b673fe281d");
        String path = "src/main/java/captcha/fsspCaptcha.png";
        File captchaImage = page.getCaptcha().getScreenshotAs(OutputType.FILE);
        FileHandler.copy(captchaImage, new File(path));

        Normal captcha = new Normal(path);
        solver.solve(captcha);
        if (!captcha.getCode().matches("\\d{6}")) {
            Selenide.refresh();
            fillData(series, number);
            resolveCaptcha(series, number);
        }
        System.out.println(captcha.getCode());
        page.getCaptchaInput().sendKeys(captcha.getCode());
        page.getSubmit().click();
        return page(PassportValidResultPage.class);
    }

    public PassportValidResultPage resolveCaptcha(int q) throws IOException, TesseractException, InterruptedException {
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
