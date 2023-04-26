package checks;

import model.Person;
import org.apache.poi.xssf.usermodel.XSSFRow;
import utils.PersonBuilder;

public interface ICheck {
    void run(XSSFRow row);

    default Person createPersonDefault(XSSFRow row) {
        return new PersonBuilder(row)
                .addFio()
                .addBDay()
                .addPassport()
                .build();
    }
}
