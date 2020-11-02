package lv.llu.science.dwh

import lv.llu.science.dwh.vaults.BasicDataInVault
import lv.llu.science.dwh.vaults.VaultType
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestingVaultConfig {
    @Bean
    BasicDataInVault testingVault() {
        return new BasicDataInVault(null, null, null,
                VaultType.Scalar, "testing_collection", "testingTopic")
    }
}
