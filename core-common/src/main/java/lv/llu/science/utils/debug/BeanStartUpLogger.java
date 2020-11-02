package lv.llu.science.utils.debug;

import lombok.extern.java.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;

@Component
@Log
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BeanStartUpLogger implements BeanPostProcessor {

    private Map<String, Long> start = new HashMap<>();
    private Map<String, Long> time = new HashMap<>();
    private List<String> beans = new ArrayList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        start.put(beanName, System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Long removed = start.remove(beanName);
        if (removed != null) {
            time.put(beanName, System.currentTimeMillis() - removed);
        }

        if (bean.getClass().getCanonicalName().startsWith("lv.llu.science")) {
            beans.add(bean.getClass().getCanonicalName());
        }

        return bean;
    }

    public void printStartupTimes() {
        System.out.println("\n************************* COMPONENT STARTUP TIMES ****************************");

        time.entrySet().stream()
                .sorted(comparingByValue(reverseOrder()))
                .limit(10)
                .forEachOrdered(entry -> System.out.println(String.format("%6d\t%s", entry.getValue(), entry.getKey())));

        System.out.println("******************************************************************************\n");

        beans.stream().sorted().forEach(System.out::println);

        System.out.println("******************************************************************************\n");
    }

    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) {
        printStartupTimes();
    }


}

