package warehouse.com.eventstarter.configuration;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import warehouse.com.eventstarter.model.Event;
import warehouse.com.eventstarter.service.EventService;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KafkaTemplate.class)
@AutoConfigureAfter(KafkaAutoConfiguration.class)
@EnableConfigurationProperties({PublicTopicsProperties.class})
public class KafkaTopicsAutoConfiguration implements InitializingBean {

  private final PublicTopicsProperties publicTopicsProperties;
  private final ConfigurableBeanFactory beanFactory;
  private final AnnotatedEventDefinitionLoader eventDefinitionLoader;
  private final KafkaProperties kafkaProperties;

  @Override
  public void afterPropertiesSet() {
    publicTopicsProperties.getTopics().values().forEach(this::registerTopic);
    eventDefinitionLoader.getTopicsData().forEach(this::registerTopic);
  }

  @Bean
  public TopicConfiguration topicConfiguration() {
    return new TopicConfiguration(publicTopicsProperties, eventDefinitionLoader);
  }

  @Bean
  public DefaultKafkaProducerFactory<String, Event> defaultProducerFactory() {
    return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
  }

  @Bean
  public KafkaTemplate<String, Event> defaultKafkaTemplate(
      DefaultKafkaProducerFactory<String, Event> defaultProducerFactory) {
    return new KafkaTemplate<>(defaultProducerFactory);
  }

  @Bean
  public EventService eventService(
      KafkaTemplate<String, Event> defaultKafkaTemplate,
      TopicConfiguration topicConfiguration) {
    return new EventService(defaultKafkaTemplate, topicConfiguration);
  }

  private void registerTopic(TopicData topic) {
    var numPartitions = Optional.ofNullable(topic.getNumPartitions())
        .orElse(publicTopicsProperties.getDefaultNumPartitions());
    var replicationFactor = Optional.ofNullable(topic.getReplicationFactor())
        .orElse(publicTopicsProperties.getDefaultReplicationFactor());

    beanFactory.registerSingleton(topic.getName(),
        new NewTopic(topic.getName(), numPartitions, replicationFactor));
  }
}
