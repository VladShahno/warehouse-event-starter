package warehouse.com.eventstarter.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

class EventPropertiesLoaderTest {

  private final EventPropertiesLoader propertiesLoader = new EventPropertiesLoader();

  @Test
  void shouldLoadEventProperties() {
    //given
    ConfigurableEnvironment environment = new StandardEnvironment();
    //when
    propertiesLoader.postProcessEnvironment(environment, null);
    //then
    assertThat(environment.getPropertySources().contains("defaultEventProperties")).isTrue();
  }

  //this one exists just to satisfy jacoco module
  @Test
  void shouldHandleIoExceptionQuietly() throws IOException {
    //given
    ConfigurableEnvironment environment = new StandardEnvironment();
    YamlPropertySourceLoader mockedSourceLoader = Mockito.mock(YamlPropertySourceLoader.class);
    ReflectionTestUtils.setField(propertiesLoader, "propertySourceLoader", mockedSourceLoader);
    given(mockedSourceLoader.load(any(), any())).willThrow(new IOException());

    //when
    propertiesLoader.postProcessEnvironment(environment, null);

    //then
    assertThat(environment.getPropertySources().contains("defaultEventProperties")).isFalse();
  }

  //this one exists just to satisfy jacoco module
  @Test
  void shouldDoNothingWhenResourcesAreEmpty() throws IOException {
    //given
    ConfigurableEnvironment environment = new StandardEnvironment();
    YamlPropertySourceLoader mockedSourceLoader = Mockito.mock(YamlPropertySourceLoader.class);
    ReflectionTestUtils.setField(propertiesLoader, "propertySourceLoader", mockedSourceLoader);
    given(mockedSourceLoader.load(any(), any())).willReturn(Collections.emptyList());

    //when
    propertiesLoader.postProcessEnvironment(environment, null);

    //then
    assertThat(environment.getPropertySources().contains("defaultEventProperties")).isFalse();
  }
}
