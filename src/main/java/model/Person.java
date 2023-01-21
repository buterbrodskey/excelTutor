package model;

import lombok.Data;

@Data
public class Person {
    private String name;
    private String lastName;
    private String nameOfFather;
    private String passport;
    private String date;
    private String inn;
    private int index;

    @Override
    public String toString() {
        return index +
                " " + lastName +
                " " + name +
                " " + nameOfFather +
                " " + passport +
                " " + inn;
    }
}
