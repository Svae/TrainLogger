package ntnu.no.trainlogger.monitorblock.component;

import java.util.HashMap;

import com.google.gson.Gson;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.AMQPProperties;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainInfo;

public class Component extends Block {
	
	private Gson g = new Gson();
	private String localTopic = "#";
	public HashMap<AMQPProperties, String> initAMQP() {
		HashMap<AMQPProperties, String> p = new HashMap<AMQPProperties, String>();
		p.put(AMQPProperties.EXCHANGENAME, "train_test_log");
		p.put(AMQPProperties.HOSTNAME, "192.168.0.100");
		p.put(AMQPProperties.USERNAME, "alexander");
		p.put(AMQPProperties.PASSWORD, "broker123");
		return p;
	}
	
	public void printTimeDifference(Message m){
//		TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
//		logger.info(String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
		logger.info(m.getJsonBody());
	}
	
	public String getTopic(){
		return localTopic;
	}
}
