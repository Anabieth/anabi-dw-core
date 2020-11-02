package lv.llu.science.dwh.reports;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;

import java.time.ZonedDateTime;
import java.util.List;

import static java.text.MessageFormat.format;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static lv.llu.science.dwh.vaults.ValueBundle.hourly;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Log
public abstract class BasicReport implements Report {

    protected MongoOperations operations;
    protected ReportItemConverter converter;

    @Autowired
    public void setOperations(MongoOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public void setConverter(ReportItemConverter converter) {
        this.converter = converter;
    }

    @Override
    public abstract String getReportCode();

    @Override
    public abstract String getReportName();

    public abstract ReportDataBean getReportData(String objectId, ZonedDateTime from, ZonedDateTime to, Integer limit);

    public List<ReportItem> getReportItems(String collectionName, String objectId, ZonedDateTime from, ZonedDateTime to, Integer limit) {
        log.info(format("Estimated elements: hours={0}, minutes={1}", HOURS.between(from, to), MINUTES.between(from, to)));
        //TODO: choose query type and target collection depending on estimated number of records?

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
                        .and("arr.v").as("values." + this.getReportCode()),
                match(where("_id").gte(from).lte(to)),
                sample(limit),
                sort(Sort.by("_id"))
        );

        return operations.aggregate(pipeline, collectionName, ReportItem.class).getMappedResults();
    }
}
