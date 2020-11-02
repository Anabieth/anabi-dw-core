package lv.llu.science.dwh.domain.input;

import com.mongodb.BasicDBObject;
import lombok.extern.java.Log;
import lv.llu.science.dwh.domain.messages.DwhTopicPublisher;
import lv.llu.science.dwh.domain.metadata.MetadataService;
import lv.llu.science.utils.time.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static lv.llu.science.dwh.domain.input.InputSwampStatus.Initial;
import static lv.llu.science.dwh.domain.metadata.EventType.DataIn;

@Log
@Service
public class DataInService {

    private final MetadataService metadataService;
    private final InputSwampRepository repository;
    private final DwhTopicPublisher publisher;
    private final TimeMachine timeMachine;

    @Autowired
    public DataInService(MetadataService metadataService, InputSwampRepository repository, DwhTopicPublisher publisher,
                         TimeMachine timeMachine) {
        this.metadataService = metadataService;
        this.repository = repository;
        this.publisher = publisher;
        this.timeMachine = timeMachine;
    }

    public void startSwampProcessing() {
        log.info("Starting swamp processing");
        for (InputSwamp swamp : repository.findAll(Sort.by("createdTs"))) {
            publisher.sendDataIn(swamp.getType(), swamp.getId());
        }
    }

    public DataInResultBean storeData(ObjectValuesBean bean) {
        InputSwamp swamp = new InputSwamp();
        swamp.setObjectId(bean.getObjectId());
        swamp.setType(bean.getType());
        swamp.setValues(bean.getValues());
        swamp.setStatus(Initial);
        swamp.setCreatedTs(timeMachine.now());
        InputSwamp saved = repository.save(swamp);
        metadataService.registerEvent(DataIn, new BasicDBObject("swampId", saved.getId()));

        publisher.sendDataIn(saved.getType(), saved.getId());

        return new DataInResultBean(saved.getId());
    }

}
