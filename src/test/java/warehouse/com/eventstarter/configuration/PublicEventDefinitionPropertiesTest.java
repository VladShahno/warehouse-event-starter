package warehouse.com.eventstarter.configuration;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TopicPropertiesTestConfiguration.class})
class PublicEventDefinitionPropertiesTest {

  @Autowired
  private PublicTopicsProperties topicsProperties;

  @Test
  void shouldLoadTopicProperties() {
    assertFalse(topicsProperties.getTopics().isEmpty());
  }
}
