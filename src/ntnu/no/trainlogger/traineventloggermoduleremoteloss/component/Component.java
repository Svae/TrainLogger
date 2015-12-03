package ntnu.no.trainlogger.traineventloggermoduleremoteloss.component;

import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.AMQPProperties;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainInfoUpdate;
import ntnu.no.trainlogger.delta.TrainInfoUpdateSerializer;

public class Component extends Block {
	private String remoteTopic = "train.info.";
	private int trainId = 1;
	private int numberOfTrains = 100;
	private GsonBuilder gb;
	private Gson g;

	public HashMap<AMQPProperties, String> initRemoteAMQP() {
		gb = new GsonBuilder();
		gb.registerTypeAdapter(TrainInfoUpdate.class, new TrainInfoUpdateSerializer());
		g = gb.create();
		HashMap<AMQPProperties, String> p = new HashMap<>();
		p.put(AMQPProperties.EXCHANGENAME, "logs");
		p.put(AMQPProperties.HOSTNAME, "118.138.243.151");
		return p;
	}

	public Message wrapTrainInfo(TrainInfo ti) {
		return new Message(remoteTopic.concat("sync"), ti);
	}

	public Message wrapTrainInfoUpdate(TrainInfoUpdate tu) {
		return new Message(remoteTopic.concat("update"), g.toJson(tu));
	}

	public void printTimeDifference(Message m) {
		if (m.getEnvelope().getRoutingKey().contains("sync")) {
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			logger.info(String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
		} else {
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			logger.info(String.valueOf(System.currentTimeMillis() - tu.getTimeStamp()));
		}
	}

	public int giveId() {
		return trainId++;
	}

	public boolean newSimulator() {
		return trainId <= numberOfTrains;
	}

	public String getLocalTopic() {
		return remoteTopic.concat("*");
	}
}
