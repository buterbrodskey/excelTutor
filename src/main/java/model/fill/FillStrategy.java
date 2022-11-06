package model.fill;

import model.PersonModel;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.time.format.DateTimeFormatter;

public interface FillStrategy {
    int LAST_NAME_INDEX = 0;
    int NAME_INDEX = 1;
    int NAME_OF_FATHER_INDEX = 2;
    int DATE_INDEX = 3;
    int PASSPORT_INDEX = 4;
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    PersonModel createPersonModel(XSSFRow row);
}
