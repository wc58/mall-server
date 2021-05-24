package top.chao58.propertis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("elasticsearch")
public class ElasticsearchProperties {

    private String host;
    private Integer port;

}
