package lv.llu.science.dwh.vaults;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log
public class DataVaultProvider {

    Map<String, DataVault> vaults = new HashMap<>();

    @Autowired(required = false)
    public void setVaults(List<DataVault> vaults) {
        vaults.forEach(v -> {
            if (this.vaults.containsKey(v.provides())) {
                throw new Error("Multiple vaults provide same topic: " + v.provides());
            }
            this.vaults.put(v.provides(), v);
            log.info("Data vault registered: " + v);
        });

        if (this.vaults.isEmpty()) {
            log.warning("No data vaults registered!");
        }
    }

    public DataVault get(String topic) {
        return vaults.get(topic);
    }
}
