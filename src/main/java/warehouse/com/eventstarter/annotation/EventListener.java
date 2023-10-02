package warehouse.com.eventstarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaListener;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@KafkaListener
public @interface EventListener {

  @AliasFor(annotation = KafkaListener.class, attribute = "topics")
  String[] value() default {};

  @AliasFor(annotation = KafkaListener.class, attribute = "groupId")
  String groupId() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "containerFactory")
  String containerFactory() default "kafkaListenerContainerFactory";

  @AliasFor(annotation = KafkaListener.class, attribute = "errorHandler")
  String errorHandler() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "filter")
  String filter() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "properties")
  String[] properties() default {};
}
