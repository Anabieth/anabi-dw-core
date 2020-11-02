package lv.llu.science.dwh.bees.models;

import lombok.Data;
import lombok.Getter;
import lombok.extern.java.Log;
import lv.llu.science.dwh.domain.messages.DataFlowMessage;
import lv.llu.science.dwh.models.*;
import lv.llu.science.dwh.reports.LabeledValues;
import lv.llu.science.dwh.reports.ReportDataBean;
import lv.llu.science.dwh.vaults.BundleBase;
import lv.llu.science.dwh.vaults.CompoundModelBundle;
import lv.llu.science.dwh.vaults.DataBundle;
import lv.llu.science.dwh.vaults.ValueBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toList;
import static lv.llu.science.dwh.vaults.ValueBundle.hourly;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@Log
public class SimpleTemperatureDeltaModel implements Model {

    @Data
    public static class ResultItem {
        private String state;
        private Float tempIn;
        private Float tempOut;
    }

    public static class ResultBundle extends CompoundModelBundle<ResultItem> {

    }

    private final String definition_collection = "simple_temperature_model";
    private final String data_collection = "simple_temperature_model_data";
    private final MongoOperations operations;

    @Autowired
    public SimpleTemperatureDeltaModel(MongoOperations operations) {
        this.operations = operations;
    }

    @Getter
    private final ModelTemplate template = ModelTemplate.builder()
            .code("simpleTemperatureDelta")
            .name("Simple temperature delta model")
            .description("Demo model is used to show how the platform works. " +
                    "It calculates delta between two temperatures and compares it with threshold.")
            .param(ModelTemplateParam.builder()
                    .code("tempIn")
                    .name("Inside temperature")
                    .type(ModelTemplateParamType.nodeId)
                    .master(true)
                    .description("Temperature inside the hive")
                    .build())
            .param(ModelTemplateParam.builder()
                    .code("tempOut")
                    .name("Outside temperature")
                    .type(ModelTemplateParamType.nodeId)
                    .description("Ambient temperature near the hive")
                    .build())
            .param(ModelTemplateParam.builder()
                    .code("delta")
                    .name("Delta threshold")
                    .type(ModelTemplateParamType.number)
                    .description("Delta threshold between two temperatures when alert is produced")
                    .build())
            .build();

    private final List<String> nodeIdParamCodes = template.getParams().stream()
            .filter(p -> p.getType() == ModelTemplateParamType.nodeId)
            .map(ModelTemplateParam::getCode)
            .collect(toList());

    private final String masterNodeIdParamCode = template.getParams().stream()
            .filter(p -> p.getType() == ModelTemplateParamType.nodeId && p.getMaster())
            .findFirst()
            .map(ModelTemplateParam::getCode)
            .orElse(null);

    @Override
    public void saveModelDefinition(ModelDefinition definition) {
        operations.save(definition, definition_collection);
    }

    @Override
    public void deleteModelDefinition(String id) {
        operations.remove(query(where("_id").is(id)), definition_collection);
    }

    @Override
    public Optional<ModelLatestValue<?>> getLatestModelValue(String objectId) {
        Query query = query(where("_id").regex("^" + objectId))
                .with(Sort.by(desc("_id")))
                .limit(1);

        return operations.find(query, ResultBundle.class, data_collection)
                .stream().findFirst()
                .flatMap(bundle -> bundle.getValues().entrySet().stream()
                        .max(comparingByKey())
                        .map(entry -> {
                            ModelLatestValue<ResultItem> latestValue = new ModelLatestValue<>();
                            latestValue.setModelCode(template.getCode());
                            latestValue.setTimestamp(ValueBundle.getZonedDateTime(bundle.getTimestamp(), entry.getKey()));
                            ResultItem rawValue = entry.getValue();
                            latestValue.setRawValue(rawValue);
                            latestValue.setLabel(rawValue.getState());
                            latestValue.setDescription(
                                    String.format("Status: %s, Temp (in): %.1f, Temp (out): %.1f",
                                            rawValue.getState(), rawValue.getTempIn(), rawValue.getTempOut())
                            );
                            return latestValue;
                        })
                );
    }

    @JmsListener(destination = "temperature:hourly")
    public void receiveMessage(DataFlowMessage message) {
        BundleBase bundle = new BundleBase(message.getBundleId());
        String objectId = bundle.getObjectId();

        Criteria modelsCriteria = new Criteria()
                .orOperator(nodeIdParamCodes.stream()
                        .map(c -> where("params." + c).is(objectId))
                        .toArray(Criteria[]::new));
        List<ModelDefinition> models = operations.find(query(modelsCriteria), ModelDefinition.class, definition_collection);


        message.getElements().stream()
                .sorted()
                .map(el -> ValueBundle.getZonedDateTime(bundle.getTimestamp(), el))
                .forEachOrdered(ts ->
                        models.forEach(m -> processModel(m, ts))
                );
    }

    private void processModel(ModelDefinition model, ZonedDateTime timestamp) {
        Optional<Float> tempIn = getLatestTemperatureValue(timestamp, (String) model.getParams().get("tempIn"));
        if (tempIn.isEmpty()) {
            return;
        }

        Optional<Float> tempOut = getLatestTemperatureValue(timestamp, (String) model.getParams().get("tempOut"));
        if (tempOut.isEmpty()) {
            return;
        }

        float delta = ((Number) model.getParams().get("delta")).floatValue();

        ResultItem resultItem = new ResultItem();
        resultItem.setState((tempIn.get() - tempOut.get() > delta) ? "ok" : "nok");
        resultItem.setTempIn(tempIn.get());
        resultItem.setTempOut(tempOut.get());

        String id = Optional.ofNullable(masterNodeIdParamCode)
                .map(code -> (String) model.getParams().get(code))
                .orElse(model.getId());
        ValueBundle bundle = ValueBundle.hourly(id, timestamp);
        Query query = query(where("_id").is(bundle.getId()));
        Update update = new Update().set("values." + bundle.getElement(), resultItem);
        operations.upsert(query, update, data_collection);
    }

    private Optional<Float> getLatestTemperatureValue(ZonedDateTime timestamp, String objectId) {
        int pastWindowHours = 1;
        Query query = query(
                new Criteria().andOperator(
                        where("_id").regex("^" + objectId),
                        where("_id").lte(ValueBundle.hourly(objectId, timestamp).getId()),
                        where("_id").gte(ValueBundle.hourly(objectId, timestamp.minusHours(pastWindowHours)).getId())
                ))
                .with(Sort.by(desc("_id")))
                .limit(1);

        return operations.find(query, DataBundle.class, "temperature_hourly")
                .stream().findFirst()
                .flatMap(bundle -> bundle.getValues().entrySet().stream()
                        .filter(entry -> Integer.parseInt(entry.getKey()) <= timestamp.getMinute())
                        .max(comparingByKey())
                        .map(Map.Entry::getValue)
                );
    }

    @Data
    public static class ReportItem {
        private ZonedDateTime id;
        private ResultItem value;
    }

    @Override
    public ReportDataBean getReportData(String objectId, ZonedDateTime from, ZonedDateTime to, Integer limit) {
        ArrayOperators.ArrayElemAt tsPart = ArrayOperators.ArrayElemAt
                .arrayOf(StringOperators.Split.valueOf("_id").split(":"))
                .elementAt(1);

        DateOperators.DateFromString ts = DateOperators.DateFromString
                .fromStringOf(StringOperators.Concat.valueOf("_id").concatValueOf("arr.k"))
                .withFormat("%Y%m%d%H%M");

        Aggregation pipeline = newAggregation(
                match(where("_id")
                        .gte(hourly(objectId, from).getId())
                        .lte(hourly(objectId, to).getId())),
                project()
                        .and(tsPart).as("_id")
                        .and(ObjectOperators.ObjectToArray.valueOfToArray("values")).as("arr"),
                unwind("arr"),
                project()
                        .and(ts).as("_id")
                        .and("arr.v").as("value"),
                match(where("_id").gte(from).lte(to)),
                sample(limit),
                sort(Sort.by("_id"))
        );

        List<ReportItem> reportItems = operations.aggregate(pipeline, data_collection, ReportItem.class)
                .getMappedResults();

        LabeledValues<ZonedDateTime> timestamp = new LabeledValues<>("timestamp");
        LabeledValues<Float> tempIn = new LabeledValues<>("tempIn", "temperature");
        LabeledValues<Float> tempOut = new LabeledValues<>("tempOut", "temperature");
        LabeledValues<String> state = new LabeledValues<>("state", "ok", "nok");

        reportItems.forEach(item -> {
            timestamp.getValues().add(item.getId());
            state.getValues().add(item.getValue().getState());
            tempIn.getValues().add(item.getValue().getTempIn());
            tempOut.getValues().add(item.getValue().getTempOut());
        });

        ReportDataBean bean = new ReportDataBean();
        bean.getData().add(timestamp);
        bean.getData().add(state);
        bean.getData().add(tempIn);
        bean.getData().add(tempOut);
        return bean;
    }
}
