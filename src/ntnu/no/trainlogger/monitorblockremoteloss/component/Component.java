package ntnu.no.trainlogger.monitorblockremoteloss.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import com.google.gson.Gson;
import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.AMQPProperties;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainInfoUpdate;

public class Component extends Block {
	private Gson g = new Gson();
	private String localTopic = "#";
	private int i = 0;
	private HashMap<Integer,Integer> updateseq;
	private HashMap<Integer,Integer> syncseq;

	public HashMap<AMQPProperties, String> initAMQP() {
		updateseq = new HashMap<>();
		syncseq = new HashMap<>();
		logger.info("STARTING new session");
		HashMap<AMQPProperties, String> p = new HashMap<>();
		p.put(AMQPProperties.EXCHANGENAME, "trainlogs");
		p.put(AMQPProperties.USERNAME, "trainloger");
		p.put(AMQPProperties.PASSWORD, "ntnutrains");
		p.put(AMQPProperties.HOSTNAME, "localhost");
		return p;
	}

	public void printTrainInfo(Message m) {
		if (m.getEnvelope().getRoutingKey().contains("sync")) {
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			logger.info("sync " + ti.toString());
		} else if (m.getEnvelope().getRoutingKey().contains("new")) {
			logger.info("New test started - " + m.getJsonBody());
		} else if (m.getEnvelope().getRoutingKey().contains("stop")) {
			sendToBlock("STOP");
		} else {
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			logger.info(tu.toString());
		}
	}

	public void printTimeDifference(Message m) {
		long t = System.currentTimeMillis();
		if (m.getEnvelope().getRoutingKey().contains("sync")) {
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			logger.info(String.valueOf(t - ti.getTimeStamp()));
		} else if (m.getEnvelope().getRoutingKey().contains("start")) {
			logger.info("New test started - " + m.getJsonBody());
		} else if (m.getEnvelope().getRoutingKey().contains("stop")) {
			sendToBlock("STOP");
		} else if (m.getEnvelope().getRoutingKey().contains("trainstart")) {
			i++;
			logger.info("New train added with id: " + m.getJsonBody() + "Current number of trains: " + i);
		} else if (m.getEnvelope().getRoutingKey().contains("trainstop")) {
			i--;
			logger.info("Stopped train with id: " + m.getJsonBody() + " Current number of trains: " + i);
		} else {
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			logger.info(String.valueOf(t - tu.getTimeStamp()));
		}
	}

	public void checkForLoss(Message m){
		if (m.getEnvelope().getRoutingKey().contains("sync")) {
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			if(ti.getSequenceNumber() != syncseq.get(ti.getId())){
				logger.info("Got out of order pack for train with id " + ti.getId() + " - Received: " + ti.getSequenceNumber() + " Expected: " + syncseq.get(ti.getId()));
			}
			syncseq.put(ti.getId(), ti.getSequenceNumber() +1 );
		} else if (m.getEnvelope().getRoutingKey().contains("start")) {
			logger.info("New test started - " + m.getJsonBody());
		} else if (m.getEnvelope().getRoutingKey().contains("stop")) {
			sendToBlock("STOP");
		} else if (m.getEnvelope().getRoutingKey().contains("trainstart")) {
			i++;
			syncseq.put(Integer.parseInt(m.getJsonBody()), 1);
			updateseq.put(Integer.parseInt(m.getJsonBody()), 0);
			logger.info("New train added with id: " + m.getJsonBody() + "Current number of trains: " + i);
		} else if (m.getEnvelope().getRoutingKey().contains("trainstop")) {
			i--;
			logger.info("Stopped train with id: " + m.getJsonBody() + " Current number of trains: " + i);
		} else {
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			if(tu.getSequenceNumber() != updateseq.get(tu.getId())){
				logger.info("Got out of order pack for train with id " + tu.getId() + " - Received: " + tu.getSequenceNumber() + " Expected: " + updateseq.get(tu.getId()));
			}
			updateseq.put(tu.getId(), tu.getSequenceNumber() +1 ); 
		}
	}
	
	public String getTopic() {
		return localTopic;
	}

	public void stopp() {
		logger.info("STOPPED monitor");
	}
}
