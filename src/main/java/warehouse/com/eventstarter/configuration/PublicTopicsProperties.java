package warehouse.com.eventstarter.configuration;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka")
public class PublicTopicsProperties {

  /**
   * Default number of partitions for kafka topics
   */
  private Integer defaultNumPartitions = 1;
  /**
   * Default replication factor for kafka topics
   */
  private Short defaultReplicationFactor = 1;
  /**
   * Kafka topics configuration
   */
  private Map<String, TopicData> topics;
}
