package lv.llu.science.dwh.web;

import lv.llu.science.utils.time.TimeMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("info")
public class InfoController {
    @Autowired
    private TimeMachine timeMachine;

    @GetMapping
    public Map<String, Object> info() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "dwh-core");
        map.put("localTime", timeMachine.now());
        map.put("zonedTime", timeMachine.zonedNow());
        return map;
    }
}
