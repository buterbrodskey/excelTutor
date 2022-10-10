package actions;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Normal;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.io.FileHandler;
import pages.FSSPCaptchaPage;
import pages.PassportValidResultPage;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FSSPCaptchaPageActions {

    private FSSPCaptchaPage page;

    public FSSPCaptchaPageActions(FSSPCaptchaPage page) {
        this.page = page;
    }

    public void resolveCaptcha(TwoCaptcha solver) throws Exception {
        String path = "src/main/java/captcha/fsspCaptcha.png";
        File captchaImage = page.getCaptchaImage().getScreenshotAs(OutputType.FILE);
        FileHandler.copy(captchaImage, new File(path));

        Normal captcha = new Normal(path);
        solver.solve(captcha);
        System.out.println(captcha.getCode());
        page.getCaptchaInput().sendKeys(captcha.getCode());
        page.getCaptchaSubmit().click();
    }

    public void resolvCaptcha() throws IOException, TesseractException {
        ITesseract image = new Tesseract();
        image.setDatapath("src/main/resources/tessdata");
        image.setLanguage("rus");
        String path = "src/main/java/captcha/fsspCaptcha.png";

        String capthaResolve = null;

        while (true) {
            File captcha = page.getCaptchaImage().getScreenshotAs(OutputType.FILE);

            FileHandler.copy(captcha, new File(path));

            String str = image.doOCR(new File(path));

            Pattern pattern = Pattern.compile("\\d\\d\\d\\d\\d\\d");
            Matcher matcher = pattern.matcher(str);

            if (matcher.find()) {
                capthaResolve = str.substring(matcher.start(), matcher.end());
            } else {
                continue;
            }
            page.getCaptchaInput().clear();
            page.getCaptchaInput().sendKeys(capthaResolve);
            page.getCaptchaSubmit().click();
        }
    }
}
