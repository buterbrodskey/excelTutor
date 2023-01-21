package model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class FedresursCase {
    private String title;
    private boolean our;
    public FedresursCase(String title) {
        this.title = title;
    }
}
