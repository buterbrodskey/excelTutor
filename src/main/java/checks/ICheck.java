package checks;

import org.apache.poi.xssf.usermodel.XSSFRow;

public interface ICheck {
    void run(XSSFRow row);
}
