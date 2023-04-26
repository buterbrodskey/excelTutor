package checks;

import actions.InnPageActions;
import model.Person;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import pages.InnPage;

import static com.codeborne.selenide.Selenide.open;

public class CheckRuInn implements ICheck {

    @Override
    public void run(XSSFRow row) {
        Person person = createPersonDefault(row);

        InnPageActions innPageActions = new InnPageActions(open("https://service.nalog.ru/inn.html", InnPage.class));
        innPageActions.clickAgree();
        try {
            innPageActions.fillPersonInnDate(person);
        } catch (InterruptedException e) {
            System.out.printf("Ошибка в заполнении формы на %d%n", person.getIndex());
            throw new RuntimeException(e);
        }

        boolean result = innPageActions.isExistInn();

        if (result) {
            System.out.printf("%d %s найден%n", person.getIndex(), person.getLastName());
            String inn = innPageActions.getInn();
            XSSFCell cell = row.createCell(9);
            cell.setCellValue(inn);
        } else {
            System.out.printf("%d %s не найден%n", person.getIndex(), person.getLastName());
            XSSFCell cell = row.createCell(9);
            cell.setCellValue("не найден");
        }
    }
}
