package lv.llu.science.dwh.domain;

import lombok.extern.java.Log;
import lv.llu.science.dwh.domain.input.DataInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Log
public class DwhCoreStartUpListener {

    private final DataInService dataInService;

    @Autowired
    public DwhCoreStartUpListener(DataInService dataInService) {
        this.dataInService = dataInService;
    }

    @EventListener
    public void onStartUp(ContextRefreshedEvent event) {
        dataInService.startSwampProcessing();
    }

}
