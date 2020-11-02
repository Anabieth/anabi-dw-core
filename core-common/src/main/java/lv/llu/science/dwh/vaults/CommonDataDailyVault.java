package lv.llu.science.dwh.vaults;

import lombok.extern.java.Log;
import lv.llu.science.dwh.domain.messages.DataFlowMessage;
import lv.llu.science.dwh.domain.messages.DwhTopicPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;

import static java.text.MessageFormat.format;
import static java.util.Collections.singletonList;
import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Log
public abstract class CommonDataDailyVault {

    private MongoOperations operations;
    private DwhTopicPublisher publisher;

    @Autowired
    public void setOperations(MongoOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public void setPublisher(DwhTopicPublisher publisher) {
        this.publisher = publisher;
    }

    public abstract void receiveMessage(DataFlowMessage message);

    public void receiveDataFlowMessage(DataFlowMessage message) {
        Query dataQuery = query(where("_id").is(message.getBundleId()));
        dataQuery.fields().exclude("values");
        DataBundle data = operations.findOne(dataQuery, DataBundle.class, message.getType() + "_" + message.getBundleType());

        if (data == null) {
            log.warning(format("Data bundle not found: type={0}, bundle={1}, id={2}",
                    message.getType(), message.getBundleType(), message.getBundleId()));
            return;
        }

        ValueBundle bundle = ValueBundle.daily(data.getObjectId(),
                ValueBundle.getZonedDateTime(data.getTimestamp()));

        Query query = query(where("_id").is(bundle.getId()));
        Float value = data.getAverage();

        DataBundle dataBundle = operations.findAndModify(query,
                update("values." + bundle.getElement(), value),
                options().returnNew(true).upsert(true),
                DataBundle.class,
                message.getType() + "_daily");

        if (dataBundle == null) {
            throw new RuntimeException("findAndModify with upsert=true returned null");
        }

        Collection<Float> values = dataBundle.getValues().values();
        Update aggUpdate = new Update();
        aggUpdate.set("count", values.size());
        aggUpdate.set("sum", values.stream().reduce(0.0f, Float::sum));
        aggUpdate.set("max", values.stream().reduce(Float.MIN_VALUE, Float::max));
        aggUpdate.set("min", values.stream().reduce(Float.MAX_VALUE, Float::min));
        operations.updateFirst(query, aggUpdate, message.getType() + "_daily");
        publisher.sendDataFlow(message.getType(), "daily", bundle.getId(), singletonList(bundle.getElement()));
    }
}
