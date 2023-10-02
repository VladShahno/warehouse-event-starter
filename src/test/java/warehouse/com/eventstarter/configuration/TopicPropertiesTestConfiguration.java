package warehouse.com.eventstarter.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration()
@EnableConfigurationProperties({PublicTopicsProperties.class})
public class TopicPropertiesTestConfiguration {

}
