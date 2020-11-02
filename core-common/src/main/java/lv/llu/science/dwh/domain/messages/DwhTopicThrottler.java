package lv.llu.science.dwh.domain.messages;

import lombok.extern.java.Log;
import lv.llu.science.utils.time.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.text.MessageFormat.format;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@Log
public class DwhTopicThrottler {

    private final MongoOperations operations;
    private final TimeMachine timeMachine;
    private final JmsTemplate jmsTemplate;

    @Value("${jms.throttling.delay}")
    private int delay;

    @Autowired
    public DwhTopicThrottler(MongoOperations operations, TimeMachine timeMachine, JmsTemplate jmsTemplate) {
        this.operations = operations;
        this.timeMachine = timeMachine;
        this.jmsTemplate = jmsTemplate;
    }

    @Scheduled(fixedDelayString = "${jms.throttling.delay}000", initialDelayString = "${jms.throttling.initialDelay}000")
    public void sendDataFlowMessages() {
        int limit = 2000;
        while (limit > 0) {
            DataFlowMessage message = operations.findAndRemove(
                    query(where("updateTs").lt(timeMachine.zonedNow().minusSeconds(delay)))
                            .with(Sort.by("updateTs")),
                    DataFlowMessage.class);
            if (message == null) {
                break;
            }
            String topic = message.getType() + ":" + message.getBundleType();
            jmsTemplate.convertAndSend(topic, message);
            log.info(format("Data-flow message sent to {0}", topic));
            limit--;
        }
    }
}
