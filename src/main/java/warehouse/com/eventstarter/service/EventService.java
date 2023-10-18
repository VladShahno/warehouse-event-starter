package warehouse.com.eventstarter.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import warehouse.com.eventstarter.configuration.TopicConfiguration;
import warehouse.com.eventstarter.model.Event;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {

  private final KafkaTemplate<String, Event> kafkaTemplate;
  private final TopicConfiguration topics;

  public CompletableFuture<SendResult<String, Event>> publish(Event event) {
    var topic = topics.getTopicName(event);
    var eventId = event.getEventId();
    return kafkaTemplate.send(topic, eventId, event);
  }

  public CompletableFuture<SendResult<String, Event>> publish(Event event, String partitionKey) {
    var topic = topics.getTopicName(event);
    return kafkaTemplate.send(topic, partitionKey, event);
  }

  public CompletableFuture<SendResult<String, Event>> publish(Event event, Integer partition) {
    var topic = topics.getTopicName(event);
    var eventId = event.getEventId();
    return kafkaTemplate.send(topic, partition, eventId, event);
  }

  public CompletableFuture<SendResult<String, Event>> publish(Event event, Integer partition,
      Long timestamp) {
    var topic = topics.getTopicName(event);
    var eventId = event.getEventId();
    return kafkaTemplate.send(topic, partition, timestamp, eventId, event);
  }

  public Integer getNumberOfPartitionsForEventTopic(Event event) {
    var topic = topics.getTopicName(event);
    return kafkaTemplate.partitionsFor(topic).size();
  }

  public Map<Integer, CompletableFuture<SendResult<String, Event>>> publishToAllPartitions(
      Event event) {
    var topic = topics.getTopicName(event);
    var partitionInfos = kafkaTemplate.partitionsFor(topic);
    var result = new HashMap<Integer, CompletableFuture<SendResult<String, Event>>>();
    var currentTimeMillis = System.currentTimeMillis();
    for (var partitionInfo : partitionInfos) {
      var partition = partitionInfo.partition();
      var eventId = event.getEventId();
      var future = kafkaTemplate.send(topic, partition, currentTimeMillis, eventId, event);
      result.put(partition, future);
    }
    return result;
  }
}
