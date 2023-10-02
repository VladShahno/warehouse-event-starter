package warehouse.com.eventstarter.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import warehouse.com.eventstarter.model.Event;

@ExtendWith(MockitoExtension.class)
class EventDefinitionConfigurationTest {

  @InjectMocks
  private TopicConfiguration topicConfiguration;

  @Mock
  private PublicTopicsProperties internalProperties;
  @Mock
  private AnnotatedEventDefinitionLoader annotatedEventDefinitionLoader;

  @Test
  void shouldReturnTopicNames() {

    Map<String, TopicData> publicTopics = new HashMap<>();
    publicTopics.put("key1",
        new TopicData("name1", "warehouse.com.eventstarter.model", "Event", 1, (short) 1));

    Set<TopicData> privateTopics = new HashSet<>();
    privateTopics.add(
        new TopicData("name2", "warehouse.com.eventstarter.configuration", "TestEvent", 1,
            (short) 1));

    when(internalProperties.getTopics()).thenReturn(publicTopics);
    when(annotatedEventDefinitionLoader.getTopicsData()).thenReturn(privateTopics);
    //when
    topicConfiguration.afterPropertiesSet();

    //then
    assertEquals("name1", topicConfiguration.getTopicName(new Event()));
    assertEquals("name2", topicConfiguration.getTopicName(new TestEvent()));
  }
}
