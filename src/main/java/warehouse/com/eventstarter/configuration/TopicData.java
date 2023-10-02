package warehouse.com.eventstarter.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicData {

  private String name;
  private String packageName;
  private String eventClass;
  private Integer numPartitions;
  private Short replicationFactor;
}
