package warehouse.com.eventstarter.configuration;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
public class EventPropertiesLoader implements EnvironmentPostProcessor {

  private static final String DEFAULT_EVENT_PROPERTIES = "defaultEventProperties";
  private static final String DEFAULT_EVENT_FILE = "application.yaml";

  private final YamlPropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader();

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    var frameworkDefaults = new ClassPathResource(DEFAULT_EVENT_FILE);
    load(environment, frameworkDefaults);
  }

  private void load(ConfigurableEnvironment environment, Resource resource) {
    try {
      var propertySources = propertySourceLoader.load(DEFAULT_EVENT_PROPERTIES, resource);
      propertySources.forEach(
          propertySource -> environment.getPropertySources().addLast(propertySource));
    } catch (IOException ex) {
      log.error("Can't load auth properties from {} file", DEFAULT_EVENT_FILE, ex);
    }
  }
}
