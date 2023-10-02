package warehouse.com.eventstarter.configuration;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String MANUAL_ACK_CONTAINER_FACTORY = "manualAckKafkaListenerContainerFactory";
  public static final String FIXED_BACK_OFF_RETRY_KAFKA_LISTENER_CONTAINER_FACTORY = "fixedBackOffRetryKafkaListenerContainerFactory";
  public static final String DYNAMIC_TYPE_CONTAINER_FACTORY = "dynamicTypeKafkaListenerContainerFactory";
}
