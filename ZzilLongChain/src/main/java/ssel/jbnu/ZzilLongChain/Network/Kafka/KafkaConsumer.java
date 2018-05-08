package ssel.jbnu.ZzilLongChain.Network.Kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.google.common.io.Resources;


public class KafkaConsumer {

	private org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;
	private ArrayList<KafkaMsgListener> msgListeners = new ArrayList<KafkaMsgListener>();
	private String topic = "test";

	public KafkaConsumer() {
		try
		{
			InputStream is = Resources.getResource("kafka_consumer.props").openStream();
			Properties properties = new Properties();
			properties.load(is);
			// if (properties.getProperty("group.id") == null)
			// {
			properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
			// }
			consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(properties);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void subscribeTopics(List<String> topics) {
		System.out.println("this is subscribe");
		consumer.subscribe(topics);

		new Thread(new Runnable() {

			public void run() {
				while (true)
				{
					pumpMessages();
				}
			}
		}).start();
	}

	int timeouts = 0;

	public void pumpMessages() {
		System.out.println("this is pumpMessage");
		ConsumerRecords<String, String> records = consumer.poll(200);
		if (records.count() == 0)
		{
			timeouts++;
		}
		ArrayList<String> messages = new ArrayList<String>();

		for (ConsumerRecord<String, String> record : records)
		{
			String consumedTopic = record.topic();
			if (consumedTopic.equals(this.topic))
			{
				messages.add(record.value());
			}
		}

		if (messages != null && messages.size() > 0)
		{
			fireMessages(messages);
		}
	}

	private void fireMessages(ArrayList<String> messages) {
		ArrayList<KafkaMsgListener> clonedListeners = null;
		synchronized (msgListeners)
		{
			clonedListeners = (ArrayList<KafkaMsgListener>) msgListeners.clone();
		}

		for (int i = 0; i < clonedListeners.size(); i++)
		{
			KafkaMsgListener listener = clonedListeners.get(i);
			listener.acceptMessages(messages);
		}
	}

	public void addKafkaMsgListener(KafkaMsgListener kct) {
		if (kct != null)
		{
			msgListeners.add(kct);
		}
	}

	public void unregister(KafkaMsgListener kct) {
		if (msgListeners.contains(kct))
		{
			synchronized (msgListeners)
			{
				msgListeners.remove(kct);
			}
		}
	}

}
