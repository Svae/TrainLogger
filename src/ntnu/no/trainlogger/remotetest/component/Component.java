package ntnu.no.trainlogger.remotetest.component;

import java.util.HashMap;

import com.google.gson.Gson;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.AMQPProperties;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainDelta;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainStatus;

public class Component extends Block {

	
	private String localTopic = "train.info.";
	private String remoteTopic = "log.";
	private int trainId = 1;
	private int numberOfTrains = 4;
	private Gson gson = new Gson();
	
	public HashMap<AMQPProperties, String> initRemoteAMQP(){
		HashMap<AMQPProperties, String> p = new HashMap<>();
		p.put(AMQPProperties.EXCHANGENAME, "logs");
		p.put(AMQPProperties.HOSTNAME, "118.138.243.151");
		return p;
	}
	
	public TrainInfo parseInfoMessage(Message m){
		return gson.fromJson(m.getJsonBody(), TrainInfo.class);
	}
	
	public void printMessage(Message m){
		System.out.println(m.getJsonBody());
////		System.out.println("Size: " + m.);
//		
//		TrainDelta delta = gson.fromJson(m.getJsonBody(), TrainDelta.class);
//		if(m.getEnvelope().getRoutingKey().contains("sync")){
//			for (TrainInfo ti : delta.getTrains().values()) {
//				logger.info(ti.toString() + " | sync " + String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
////				logger.info(ti.toString());
//			}
//		} else {
//			for (TrainInfo ti : delta.getTrains().values()) {
//				logger.info(ti.printChanges() + " | " +String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
////				logger.info(String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
//
//			}
//		}
//			

	}

	public Message wrapTrainDelta(TrainDelta td){
		return new Message(remoteTopic.concat("delta"), td);
	}
	
	public Message wrapSyncDelta(TrainDelta td){
		return new Message(remoteTopic.concat("sync"), td);
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
	
	public TrainInfo copyTrainInfo(TrainInfo ti){
		TrainInfo newTi = new TrainInfo(ti.getId(), ti.getTrainLength());
		newTi.setDirection(ti.getDirection());
		newTi.setPosition(ti.getPosition());
		newTi.setSpeed(ti.getSpeed());
		newTi.setTimeStamp(ti.getTimeStamp());
		TrainStatus ts = new TrainStatus(ti.getStatus().getState());
		ts.setFromStation(ti.getStatus().getFromStation());
		ts.setToStation(ti.getStatus().getToStation());
		newTi.setStatus(ts);
		return newTi;
	}

}
