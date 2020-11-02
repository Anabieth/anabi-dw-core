package lv.llu.science.dwh.reports;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class LabeledValues<T> {

    public LabeledValues(String name) {
        this.name = name;
        this.type = name;
    }

    public LabeledValues(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public LabeledValues(String name, String... categories) {
        this.name = name;
        this.type = "category";
        this.categories = Arrays.asList(categories);
    }

    public LabeledValues() {
    }

    private String name;
    private String type;
    private List<String> categories;
    private List<T> values = new ArrayList<>();
}
