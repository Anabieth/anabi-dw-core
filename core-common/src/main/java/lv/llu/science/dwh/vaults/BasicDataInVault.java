package lv.llu.science.dwh.vaults;

import lombok.Getter;
import lombok.extern.java.Log;
import lv.llu.science.dwh.domain.input.InputSwamp;
import lv.llu.science.dwh.domain.input.InputSwampStatus;
import lv.llu.science.dwh.domain.input.ObjectValue;
import lv.llu.science.dwh.domain.messages.DataInMessage;
import lv.llu.science.dwh.domain.messages.DwhTopicPublisher;
import lv.llu.science.utils.time.TimeMachine;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static lv.llu.science.dwh.domain.input.InputSwampStatus.Initial;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Log
public class BasicDataInVault {

    private final MongoOperations operations;
    private final TimeMachine timeMachine;
    private final DwhTopicPublisher publisher;
    private final VaultType vaultType;
    private final String collectionName;
    @Getter
    private final String dataInTopic;

    public BasicDataInVault(MongoOperations operations, TimeMachine timeMachine, DwhTopicPublisher publisher, VaultType vaultType, String collectionName, String dataInTopic) {
        this.operations = operations;
        this.timeMachine = timeMachine;
        this.publisher = publisher;
        this.vaultType = vaultType;
        this.collectionName = collectionName;
        this.dataInTopic = dataInTopic;
    }

    public void receiveDataInMessage(DataInMessage message) {
        InputSwamp swamp = operations.findAndModify(
                query(where("id").is(message.getSwampId())
                        .orOperator(
                                where("status").is(Initial),
                                where("statusTs").lt(timeMachine.now().minusMinutes(10))
                        )
                ),
                update("status", InputSwampStatus.InProcess).set("statusTs", timeMachine.now()), InputSwamp.class
        );
        Map<String, List<String>> bundles = new HashMap<>();

        if (swamp != null) {
            if (swamp.getValues() != null) {
                for (ObjectValue item : swamp.getValues()) {
                    ValueBundle bundle = ValueBundle.hourly(swamp.getObjectId(), item.getTs());
                    boolean saved = false;

                    switch (vaultType) {
                        case Scalar:
                            saved = saveScalarValue(bundle.getId(), bundle.getElement(), item.getValue());
                            break;
                        case Array:
                            saved = saveArrayValue(bundle.getId(), bundle.getElement(), item.getValues());
                            break;
                    }

                    if (saved) {
                        bundles.putIfAbsent(bundle.getId(), new ArrayList<>());
                        bundles.get(bundle.getId()).add(bundle.getElement());
                    }
                }
            }
            bundles.forEach((id, elements) -> publisher.sendDataFlow(message.getType(), "hourly", id, elements));
            operations.remove(swamp);
        } else {
            log.warning("Swamp not found or already in-process: " + message.getSwampId());
        }
    }

    private boolean saveScalarValue(String bundleId, String minute, Float value) {
        if (value == null) {
            return false;
        }

        Query query = new Query(where("_id").is(bundleId).and("values." + minute).exists(false));
        Update update = new Update();
        update.inc("count", 1);
        update.inc("sum", value);
        update.max("max", value);
        update.min("min", value);
        update.set("values." + minute, value);

        return tryToSave(bundleId, minute, query, update);
    }

    private boolean saveArrayValue(String bundleId, String minute, List<Float> values) {
        if (values == null) {
            return false;
        }

        Query query = new Query(where("_id").is(bundleId).and("values." + minute).exists(false));

        Update update = new Update();
        for (int i = 0; i < values.size(); i++) {
            Float val = values.get(i);
            update.inc("count." + i, 1);
            update.inc("sum." + i, val);
            update.max("max." + i, val);
            update.min("min." + i, val);
        }
        update.set("values." + minute, values);

        return tryToSave(bundleId, minute, query, update);
    }

    private boolean tryToSave(String bundleId, String minute, Query query, Update update) {
        try {
            operations.upsert(query, update, collectionName);
            return true;
        } catch (DuplicateKeyException ex) {
            log.warning(format("Value already exists: type={0}, bundle={1}, element={2}", collectionName, bundleId, minute));
        }
        return false;
    }

}
