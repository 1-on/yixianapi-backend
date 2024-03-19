package com.yixian.yixianapiclientsdk;

import com.yixian.yixianapiclientsdk.client.YixianApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("yixianapi.client")
@Data
public class YixianApiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public YixianApiClient yixianApiClient() {
        return new YixianApiClient(accessKey, secretKey);
    }
}
