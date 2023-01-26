package checks;

import actions.InnPageActions;
import model.Person;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import pages.InnPage;

import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.open;

public class CheckRuInn implements ICheck {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static Person createPersonForInn(XSSFRow row) {
        Person person = new Person();
        person.setIndex(row.getRowNum());
//        String backgroundColor = "";
//
//        try {
//            backgroundColor = getBackgroundColor(row.getCell(1)).trim();
//        } catch (NullPointerException e) {
//
//        }
//
//        if (backgroundColor.equals("ff0000")) {
//            throw new NullPointerException("Помечено красным");
//        }
//
        String passport = row.getCell(4).getStringCellValue();
        if (passport == null) throw new NullPointerException("Нет пасспорта");
        if (passport.length() == 9) {
            passport = "0" + passport;
        }
        if (passport.length() == 8) {
            passport = "00" + passport;
        }
        String lastName = row.getCell(0).getStringCellValue().replace((char) 160, 'e');
        person.setLastName(lastName.replace("e", ""));
        person.setPassport(passport);
        try {
            person.setDate(row.getCell(3).getLocalDateTimeCellValue().format(DATE_FORMATTER));
        } catch (Exception e) {
            person.setDate(row.getCell(3).getStringCellValue());
        }
        person.setName(row.getCell(1).getStringCellValue().replace((char) 160, 'e').replace("e", ""));

        if (person.getDate() == null) {
            throw new NullPointerException("Пустая дата");
        }

        try {
            person.setNameOfFather(row.getCell(2).getStringCellValue().trim());
        } catch (NullPointerException e) {
            person.setNameOfFather(null);
        }
        return person;
    }

    @Override
    public void run(XSSFRow row) {
        Person person;

        person = createPersonForInn(row);

        InnPageActions innPageActions = new InnPageActions(open("https://service.nalog.ru/inn.html", InnPage.class));
        innPageActions.clickAgree();
        try {
            innPageActions.fillPersonInnDate(person);
        } catch (InterruptedException e) {
            System.out.println("Ошибка в заполнении формы на " + person.getIndex());
            throw new RuntimeException(e);
        }

        boolean result = innPageActions.isExistInn();

        if (!result) {
            System.out.print(person.getIndex() + " ");
            System.out.println(
                    person.getLastName() + " " +
                            person.getName() + " " +
                            person.getNameOfFather() + " " +
                            person.getDate() + " " +
                            person.getPassport());
        } else {
            String inn = innPageActions.getInn();
            XSSFCell cell = row.createCell(12);
            cell.setCellValue(inn);
        }
    }
}
