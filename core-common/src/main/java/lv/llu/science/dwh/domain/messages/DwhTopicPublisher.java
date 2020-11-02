package lv.llu.science.dwh.domain.messages;

import lombok.extern.java.Log;
import lv.llu.science.utils.time.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.text.MessageFormat.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Log
@Service
public class DwhTopicPublisher {

    private final JmsTemplate jmsTemplate;
    private final MongoOperations operations;
    private final TimeMachine timeMachine;

    @Autowired
    public DwhTopicPublisher(JmsTemplate jmsTemplate, MongoOperations operations, TimeMachine timeMachine) {
        this.jmsTemplate = jmsTemplate;
        this.operations = operations;
        this.timeMachine = timeMachine;
    }

    public void sendDataFlow(String type, String bundleType, String bundleId, List<String> elements) {
        operations.upsert(
                query(where("type").is(type)
                        .and("bundleType").is(bundleType)
                        .and("bundleId").is(bundleId)),
                update("updateTs", timeMachine.zonedNow())
                        .addToSet("elements").each(elements),
                DataFlowMessage.class);

        log.info(format("Data-flow message stored {0}, {1}", type, bundleType));
    }

    public void sendDataIn(String type, String swampId) {
        DataInMessage message = new DataInMessage(type, swampId);
        String topic = "swamp:" + type;
        jmsTemplate.convertAndSend(topic, message);
        log.info(format("Data-in message sent to {0}", topic));
    }
}
