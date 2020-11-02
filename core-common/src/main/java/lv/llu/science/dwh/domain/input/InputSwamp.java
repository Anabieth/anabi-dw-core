package lv.llu.science.dwh.domain.input;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
public class InputSwamp {
    @Id
    private String id;
    private String objectId;

    private String type;
    private List<ObjectValue> values;

    @Indexed
    private LocalDateTime createdTs;

    private InputSwampStatus status;
    private LocalDateTime statusTs;
}
