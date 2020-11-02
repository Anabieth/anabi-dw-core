package lv.llu.science.dwh.reports;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ReportItemConverter implements Function<List<ReportItem>, ReportDataBean> {

    @Override
    public ReportDataBean apply(List<ReportItem> result) {
        LabeledValues<ZonedDateTime> timestamp = new LabeledValues<>("timestamp");
        Map<String, LabeledValues<Float>> valueMap = new HashMap<>();

        result.forEach(item -> {
            timestamp.getValues().add(item.getId());

            item.getValues().forEach((key, value) -> {
                valueMap.putIfAbsent(key, new LabeledValues<>(key));
                valueMap.get(key).getValues().add(value);
            });
        });

        ReportDataBean bean = new ReportDataBean();
        bean.getData().add(timestamp);
        bean.getData().addAll(valueMap.values());
        return bean;
    }
}
