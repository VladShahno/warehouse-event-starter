package warehouse.com.eventstarter.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published by services when entities are changed.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AuditEvent extends Event {

  private String entityType;
  private Set<Entity> entities;
  private String initiatorId;
  private String action;
  private Date timestamp;
  private String description;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Entity implements Serializable {

    private String id;
    private String name;
  }
}
