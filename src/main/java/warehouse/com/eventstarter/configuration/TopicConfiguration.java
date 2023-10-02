package warehouse.com.eventstarter.configuration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import warehouse.com.eventstarter.model.Event;

@RequiredArgsConstructor
public class TopicConfiguration implements InitializingBean {

  private static final String EVENTS_PACKAGE = "warehouse.com.eventstarter.model";

  private final Map<String, String> topicNamesByModel = new ConcurrentHashMap<>();
  private final PublicTopicsProperties internalProperties;
  private final AnnotatedEventDefinitionLoader eventDefinitionLoader;

  /**
   * NOTE: Required to map the Topic-Name with the Event-Model
   */
  @Override
  public void afterPropertiesSet() {
    internalProperties.getTopics().values()
        .forEach(topic -> topicNamesByModel.put(getEventClassByName(
            EVENTS_PACKAGE, topic.getEventClass()), topic.getName()));
    eventDefinitionLoader.getTopicsData()
        .forEach(topic -> topicNamesByModel.put(getEventClassByName(
            topic.getPackageName(), topic.getEventClass()), topic.getName()));
  }

  public String getTopicName(Event event) {
    return Optional.of(event.getClass())
        .map(Class::getName)
        .map(topicNamesByModel::get)
        .orElseThrow(() -> new IllegalArgumentException(
            "Kafka topic is not registered for given event " + event.getClass()));
  }

  private String getEventClassByName(String basePackage, String className) {
    try {
      String fullName = basePackage + "." + className;
      Class.forName(fullName);
      return fullName;
    } catch (ClassNotFoundException ex) {
      throw new IllegalArgumentException(ex);
    }
  }
}
