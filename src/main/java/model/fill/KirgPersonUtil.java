package model.fill;

import model.PersonModel;
import org.apache.poi.xssf.usermodel.XSSFRow;

import static excel.ExcelUtils.getBackgroundColor;

public class KirgPersonUtil implements FillStrategy {
    @Override
    public PersonModel createPersonModel(XSSFRow row) {
        PersonModel person = new PersonModel();
        person.setIndex(row.getRowNum());
        String backgroundColor = "";

        try {
            backgroundColor = getBackgroundColor(row.getCell(LAST_NAME_INDEX));
        } catch (NullPointerException e) {

        }

        if (backgroundColor.equals("ff0000")) {
            throw new NullPointerException("Помечено красным");
        }

        String numericPassport = row.getCell(PASSPORT_INDEX).getStringCellValue();
        if (numericPassport == null) throw new NullPointerException("Нет пасспорта");
        String passport = numericPassport;


        person.setLastName(row.getCell(LAST_NAME_INDEX).getStringCellValue());
        person.setPassport(passport);
        person.setDate(row.getCell(DATE_INDEX).getLocalDateTimeCellValue().format(DATE_FORMATTER));
        person.setName(row.getCell(NAME_INDEX).getStringCellValue());

        if (person.getDate() == null) {
            throw new NullPointerException("Пустая дата");
        }

        try {
            person.setNameOfFather(row.getCell(NAME_OF_FATHER_INDEX).getStringCellValue());
        } catch (NullPointerException e) {
            person.setNameOfFather(null);
        }
        return person;
    }
}
