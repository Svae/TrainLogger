package ntnu.no.trainlogger.localtest.component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.AMQPProperties;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainDelta;
import ntnu.no.trainlogger.delta.TrainInfo;

public class Component extends Block {

	private int trainId = 1;
	private int numberOfTrains = 1;
	private String t = "log";
	private Gson g = new Gson();
	public int init() {
		logger.info("Starting fake simulator");
		return trainId++;
	}

	public void printdelta(TrainDelta delta) {
		printTrainInfo(delta.getTrains());
	}
	
	public void printTimeDifference(Message m){
		TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
		logger.info(String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
	}
	
	private void printTrainInfo(HashMap<Integer, TrainInfo> trains) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		for (TrainInfo trainInfo : trains.values()) {
			System.out.println("Time: " + sdf.format(trainInfo.getTimeStamp()) +" "+ trainInfo.printChanges());
		}		
	}

	public void p(String error) {
		logger.error(error);
	}

	public HashMap<AMQPProperties, String> initAMQP(){
		HashMap<AMQPProperties, String> p = new HashMap<AMQPProperties, String>();
		p.put(AMQPProperties.EXCHANGENAME, "train_test_log");
		p.put(AMQPProperties.HOSTNAME, "192.168.0.100");
		p.put(AMQPProperties.USERNAME, "alexander");
		p.put(AMQPProperties.PASSWORD, "broker123");
		return p;
	}

	public Message createMsg(TrainInfo ti) {
		return new Message(t, ti);
	}
	
	public String t(){
		return "*";
	}

	public String getTi(Message m) {
		return m.getJsonBody();
	}
	
	public boolean startNewSimulator(){
		return trainId <= numberOfTrains;
	}

}
