package ntnu.no.trainlogger.traineventloggermodulelocal.component;

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
	private String remoteTopic = "train.log.";
	private int trainId = 1;
	private int numberOfTrains = 1;
	private GsonBuilder gb;
	private Gson g;
	private HashMap<Integer, Integer> syncSeq;
	private HashMap<Integer, Integer> updateSeq;

	public HashMap<AMQPProperties, String> initLocalAMQP() {
		gb = new GsonBuilder();
		gb.registerTypeAdapter(TrainInfoUpdate.class, new TrainInfoUpdateSerializer());
		g = gb.create();
		HashMap<AMQPProperties, String> p = new HashMap<>();
		p.put(AMQPProperties.EXCHANGENAME, "trainlogs");
		p.put(AMQPProperties.USERNAME, "alexander");
		p.put(AMQPProperties.PASSWORD, "broker123");
		p.put(AMQPProperties.HOSTNAME, "192.168.0.100");
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

	public void checkSequenceNumber(Message m) {
		if (m.getEnvelope().getRoutingKey().contains("sync")) {
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			logger.info(String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
			if (ti.getSequenceNumber() != syncSeq.get(ti.getId())) {
				logger.error(
						"Expected packet with seqnr. " + syncSeq.get(ti.getId()) + " got " + ti.getSequenceNumber());
			}
			syncSeq.put(ti.getId(), ti.getSequenceNumber() + 1);
		} else {
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			logger.info(String.valueOf(System.currentTimeMillis() - tu.getTimeStamp()));
			if (tu.getSequenceNumber() != updateSeq.get(tu.getId())) {
				logger.error(
						"Expected packet with seqnr. " + syncSeq.get(tu.getId()) + " got " + tu.getSequenceNumber());
			}
			updateSeq.put(tu.getId(), tu.getSequenceNumber() + 1);
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

	public void error(String e) {
		logger.error(e);
	}

	public Message sendGreeting() {
		return new Message("start", "Local delay test, 1 train");
	}

	public Message sendNewTrain(int id) {
		return new Message("trainstart", id);
	}

	public boolean extendedWait() {
		return trainId % 10 == 0;
	}
}
