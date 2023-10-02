package warehouse.com.eventstarter.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class Event implements Serializable {

  private String eventId;
  private String eventType;
  private Date publishedAt;
}
