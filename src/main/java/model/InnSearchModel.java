package model;

import lombok.Data;

@Data
public class InnSearchModel {
    private String name;
    private String lastName;
    private String nameOfFather;
    private long passport;
    private String date;
}
