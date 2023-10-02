package warehouse.com.eventstarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventDefinition {

  String topicName();

  int numPartitions() default 1;

  short replicationFactor() default 1;
}
