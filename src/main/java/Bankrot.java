import actions.InnPageActions;
import actions.KadArbitrPageActions;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import config.Config;
import model.PersonModel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import pages.KadArbitr;

import java.io.*;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;

public class Bankrot {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration.holdBrowserOpen = true;
        String path = "src/main/resources/bank.xlsx";
        checkBankrots(path);
    }

    private static void checkBankrots(String path) throws IOException, InterruptedException {
        File filePath = new File(path);
        FileInputStream file = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        PersonModel person;
        for (int i = 1;
             i <= sheet.getLastRowNum();
             i++) {

            XSSFRow row = sheet.getRow(i);
            try {
                person = Main.createPersonModel(row);
            } catch (NullPointerException e) {
                continue;
            }

            KadArbitrPageActions page = new KadArbitrPageActions(open(Config.getArbitrUri(), KadArbitr.class));
            page.fillFio(person);
            page.clickSubmit();
            page.wait(1000);
            SelenideElement table = page.getTableResponse();
            List<SelenideElement> strings = table.$$(By.xpath("/tr"));
            System.out.println(strings);

            int a = i;

            boolean result = false;

            if (result == false) {
                System.out.print(++a + " ");
                System.out.println(
                        person.getLastName() + " " +
                                person.getName() + " " +
                                person.getNameOfFather() + " " +
                                person.getDate() + " " +
                                person.getPassport());
            } else {
//                String inn = innPageActions.getInn();
//                XSSFCell cell = row.createCell(INN_INDEX);
//                cell.setCellValue(inn);
            }
        }
        file.close();

        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        outputStream.close();
    }
}
