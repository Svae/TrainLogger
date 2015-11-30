package ntnu.no.trainlogger.loggerdelaytest.component;

import java.util.HashMap;

import com.google.gson.Gson;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.AMQPProperties;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainDelta;
import ntnu.no.trainlogger.delta.TrainInfo;

public class Component extends Block {
	
	private String localTopic = "train.info.";
	private String remoteTopic = "log.";
	private int trainId = 1;
	private int numberOfTrains = 2;
	private Gson gson = new Gson();

	public HashMap<AMQPProperties, String> initLocalAMQP() {
		HashMap<AMQPProperties, String> p = new HashMap<>();
		p.put(AMQPProperties.EXCHANGENAME, "trains");
		p.put(AMQPProperties.HOSTNAME, "192.168.0.100");
		p.put(AMQPProperties.USERNAME, "alexander");
		p.put(AMQPProperties.PASSWORD, "broker123");
		return p;
	}
	
	public HashMap<AMQPProperties, String> initRemoteAMQP(){
		HashMap<AMQPProperties, String> p = new HashMap<>();
		p.put(AMQPProperties.EXCHANGENAME, "logs");
		p.put(AMQPProperties.HOSTNAME, "118.138.243.151");
		return p;
	}
	
	public Message wrapTrainInfo(TrainInfo ti){
		return new Message(localTopic.concat(String.valueOf(ti.getId())), ti);
	}
	
	public Message wrapTrainDelta(TrainDelta td){
		return new Message(remoteTopic.concat("delta"), td);
	}
	
	public Message wrapSyncDelta(TrainDelta td){
		return new Message(remoteTopic.concat("sync"), td);
	}
	
	public TrainInfo parseInfoMessage(Message m){
		return gson.fromJson(m.getJsonBody(), TrainInfo.class);
	}
	
	public void printMessage(Message m){
		TrainDelta delta = gson.fromJson(m.getJsonBody(), TrainDelta.class);
		if(m.getEnvelope().getRoutingKey().contains("sync")){
			for (TrainInfo ti : delta.getTrains().values()) {
				//logger.info(ti.toString());
				logger.info("Sync: " + String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
			}
		} else {
			for (TrainInfo ti : delta.getTrains().values()) {
//				logger.info(ti.printChanges());
			logger.info(String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));

			}
		}
			

	}

	
	public int giveId() {
		return trainId++;
	}
	
	public boolean newSimulator(){
		return trainId <= numberOfTrains;
	}
	
	public String getRemoteTopic(){
		return remoteTopic.concat("*");
	}

	public String getLocalTopic() {
		return localTopic.concat("*");
	}

}
