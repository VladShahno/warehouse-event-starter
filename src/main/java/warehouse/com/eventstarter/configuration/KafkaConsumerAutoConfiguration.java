package warehouse.com.eventstarter.configuration;

import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import warehouse.com.eventstarter.model.Event;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ConsumerFactory.class)
@AutoConfigureBefore(KafkaAutoConfiguration.class)
@EnableConfigurationProperties({KafkaProperties.class})
@PropertySource("classpath:application.yaml")
public class KafkaConsumerAutoConfiguration implements InitializingBean {

  private final KafkaProperties kafkaProperties;
  private final ObjectProvider<DefaultKafkaConsumerFactoryCustomizer> customizers;
  private final AnnotatedEventDefinitionLoader eventDefinitionLoader;
  @Value(value = "${kafka.consumer.fixed-backoff.interval}")
  private Long interval;
  @Value(value = "${kafka.consumer.fixed-backoff.max-failure}")
  private Long maxAttempts;

  @Override
  public void afterPropertiesSet() {
    kafkaProperties.getConsumer().getProperties().computeIfPresent(
        JsonDeserializer.TRUSTED_PACKAGES,
        (key, value) -> {
          var additionalTopics = eventDefinitionLoader.getTopicsData();
          return additionalTopics.isEmpty()
              ? value
              : value.concat(",").concat(additionalTopics.stream()
                  .map(TopicData::getPackageName)
                  .collect(Collectors.joining(",")));
        });
  }

  @Bean
  @ConditionalOnMissingBean(ConsumerFactory.class)
  public ConsumerFactory<?, ?> kafkaConsumerFactory() {
    return initConsumerFactory(props -> {
    });
  }

  @Bean(name = Constants.MANUAL_ACK_CONTAINER_FACTORY)
  public ConcurrentKafkaListenerContainerFactory<String, Event> manualAckKafkaListenerContainerFactory() {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, Event>();
    factory.setConsumerFactory(
        initConsumerFactory(props -> props.put(ENABLE_AUTO_COMMIT_CONFIG, false)));
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    return factory;
  }

  @Bean(name = Constants.FIXED_BACK_OFF_RETRY_KAFKA_LISTENER_CONTAINER_FACTORY)
  public ConcurrentKafkaListenerContainerFactory<String, Event> fixedBackOffRetryKafkaListenerContainerFactory() {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, Event>();
    factory.setConsumerFactory(
        initConsumerFactory(props -> props.put(ENABLE_AUTO_COMMIT_CONFIG, false)));
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
    factory.setCommonErrorHandler(errorHandlerFixedBackOff());
    return factory;
  }

  @Bean(name = Constants.DYNAMIC_TYPE_CONTAINER_FACTORY)
  public ConcurrentKafkaListenerContainerFactory<String, Event> dynamicTypeKafkaListenerContainerFactory() {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, Event>();
    factory.setConsumerFactory(
        initConsumerFactory(
            props -> props.put(VALUE_DESERIALIZER_CLASS, StringDeserializer.class)));
    factory.setMessageConverter(new StringJsonMessageConverter());
    return factory;
  }

  @Bean
  public DefaultErrorHandler errorHandlerFixedBackOff() {
    BackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
    DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) -> {
      // logic to execute when all the retry attempts are exhausted
      log.warn(String.format("After %s attempts, stopped consuming %s due to %s",
          maxAttempts, consumerRecord.toString(), exception.getClass().getName()));
    }, fixedBackOff);
    return errorHandler;
  }

  private ConsumerFactory<String, Event> initConsumerFactory(
      Consumer<Map<String, Object>> propertiesCustomizer) {
    var consumerProperties = kafkaProperties.buildConsumerProperties();
    propertiesCustomizer.accept(consumerProperties);
    var factory = new DefaultKafkaConsumerFactory<String, Event>(consumerProperties);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
    return factory;
  }
}
