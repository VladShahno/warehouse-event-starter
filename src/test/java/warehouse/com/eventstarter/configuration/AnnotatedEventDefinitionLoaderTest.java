package warehouse.com.eventstarter.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AnnotatedEventDefinitionLoaderTest {

  private final AnnotatedEventDefinitionLoader annotatedEventDefinitionLoader = new AnnotatedEventDefinitionLoader();

  @Test
  void shouldLoadEvents() throws Exception {
    annotatedEventDefinitionLoader.afterPropertiesSet();
    assertEquals(1, annotatedEventDefinitionLoader.getTopicsData().size());
  }
}
