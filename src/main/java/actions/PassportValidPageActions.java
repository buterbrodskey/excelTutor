package actions;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Normal;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.io.FileHandler;
import pages.PassportValidPage;
import pages.PassportValidResultPage;

import java.io.File;

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
}
