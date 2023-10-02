package warehouse.com.eventstarter.configuration;

import warehouse.com.eventstarter.annotation.EventDefinition;
import warehouse.com.eventstarter.model.Event;

@EventDefinition(topicName = "testTopic")
class TestEvent extends Event {

}
