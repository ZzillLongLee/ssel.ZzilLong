package ssel.jbnu.ZzilLongChain.Network.Kafka;

import java.util.ArrayList;

public interface KafkaMsgListener
{
	public void acceptMessages(ArrayList<String> messages);
}
