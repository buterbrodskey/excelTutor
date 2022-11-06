package model;

import lombok.Data;

@Data
public class PersonModel {
    private String name;
    private String lastName;
    private String nameOfFather;
    private String passport;
    private String date;
    private int index;

    @Override
    public String toString() {
        return index +
                " " +lastName +
                " " + name +
                " " + nameOfFather +
                " " + passport;
    }
}
