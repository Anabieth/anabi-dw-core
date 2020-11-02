package lv.llu.science.dwh.domain.metadata;

import com.mongodb.DBObject;
import lv.llu.science.utils.time.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class MetadataService {

    private final MongoOperations operations;
    private final TimeMachine timeMachine;

    @Autowired
    public MetadataService(MongoOperations operations, TimeMachine timeMachine) {
        this.operations = operations;
        this.timeMachine = timeMachine;
    }

    public void registerEvent(EventType eventType, DBObject metadata) {
        Event event = new Event();
        event.setEventTs(timeMachine.now());
        event.setType(eventType);
        event.setMetadata(metadata);
        operations.save(event);

        if (eventType == EventType.ReportUpdated) {
            operations.save(new ReportMetadata(metadata.get("reportId").toString(), timeMachine.now()));
        }
    }

    public Optional<ReportMetadata> getReportMetadata(String reportId) {
        return ofNullable(operations.findById(reportId, ReportMetadata.class));
    }
}
