package warehouse.com.eventstarter.configuration;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import warehouse.com.eventstarter.annotation.EventDefinition;

@Getter
@Component
public class AnnotatedEventDefinitionLoader implements InitializingBean {

  private final Set<TopicData> topicsData = new HashSet<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    var scanner = new ClassPathScanningCandidateComponentProvider(false);

    scanner.addIncludeFilter(new AnnotationTypeFilter(EventDefinition.class));

    for (var beanDefinition : scanner.findCandidateComponents("warehouse.com.eventstarter")) {
      var aClass = Class.forName(beanDefinition.getBeanClassName());
      var eventDefinition = aClass.getAnnotation(EventDefinition.class);
      var topicData = new TopicData(eventDefinition.topicName(),
          aClass.getPackageName(), aClass.getSimpleName(),
          eventDefinition.numPartitions(), eventDefinition.replicationFactor());
      topicsData.add(topicData);
    }
  }
}
