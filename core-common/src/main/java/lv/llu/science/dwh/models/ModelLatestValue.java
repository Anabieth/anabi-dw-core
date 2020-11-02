package lv.llu.science.dwh.models;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ModelLatestValue<T> {
    private String modelCode;
    private ZonedDateTime timestamp;
    private String label;
    private String description;
    private T rawValue;
}
