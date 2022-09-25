import actions.InnPageActions;
import com.codeborne.selenide.Configuration;
import excel.ExcelUtils;
import model.InnSearchModel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import pages.InnPage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.open;

public class Main {
    public static void main(String[] args) throws IOException {
        InnSearchModel inn = new InnSearchModel();

        Configuration.holdBrowserOpen = true;

        String fileLocation = "src/main/resources/table.xlsx";
        Workbook workbook = ExcelUtils.getWorkBook(fileLocation);
        Sheet sheet = workbook.getSheetAt(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (Row row : sheet) {
            if (row.getRowNum() == 2) break;
            inn.setLastName(row.getCell(1).getStringCellValue());
            inn.setName(row.getCell(2).getStringCellValue());
            inn.setNameOfFather(row.getCell(3).getStringCellValue());
            inn.setPassport((long) row.getCell(4).getNumericCellValue());
            inn.setDate(row.getCell(5).getLocalDateTimeCellValue().format(formatter));

            InnPageActions innPageActions = new InnPageActions(open("https://service.nalog.ru/inn.html", InnPage.class));
            innPageActions.clickAgree();
            innPageActions.fillPersonInnDate(inn);

            System.out.println(innPageActions.isExistInn());
        }



    }
}
