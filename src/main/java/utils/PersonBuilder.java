package utils;

import model.Person;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.time.format.DateTimeFormatter;

public class PersonBuilder {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final XSSFRow row;
    private final Person person = new Person();

    public PersonBuilder(XSSFRow row) {
        this.row = row;
        person.setIndex(row.getRowNum());
    }

    public PersonBuilder addFio() {
        String lastName =
                row
                        .getCell(0)
                        .getStringCellValue()
                        .replace((char) 160, 'e')
                        .replace("e", "");
        person.setLastName(lastName);
        person.setName(
                row
                        .getCell(1)
                        .getStringCellValue()
                        .replace((char) 160, 'e')
                        .replace("e", ""));
        try {
            person.setNameOfFather(row.getCell(2).getStringCellValue().trim());
        } catch (NullPointerException e) {
            person.setNameOfFather(null);
        }
        return this;
    }

    public PersonBuilder addPassport() {
        String passport = row.getCell(4).getStringCellValue();
        if (passport == null) throw new NullPointerException("Нет пасспорта");
        if (passport.length() == 9) {
            passport = "0" + passport;
        }
        if (passport.length() == 8) {
            passport = "00" + passport;
        }
        person.setPassport(passport);
        return this;
    }

    public PersonBuilder addBDay() {
        try {
            person.setDate(row.getCell(3).getLocalDateTimeCellValue().format(DATE_FORMATTER));
        } catch (Exception e) {
            person.setDate(row.getCell(3).getStringCellValue());
        }
        if (person.getDate() == null) {
            throw new NullPointerException("Пустая дата");
        }
        return this;
    }

    public Person build() {
        return person;
    }
}
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