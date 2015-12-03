package ntnu.no.trainlogger.monitorblockremote.component;

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
	private int i = 1;

	public HashMap<AMQPProperties, String> initAMQP() {
		logger.info("STARTING new session");
		HashMap<AMQPProperties, String> p = new HashMap<>();
		p.put(AMQPProperties.EXCHANGENAME, "trainlogs");
		p.put(AMQPProperties.USERNAME, "trainloger");
		p.put(AMQPProperties.PASSWORD, "ntnutrains");
		p.put(AMQPProperties.HOSTNAME, "localhost");
		return p;
	}

	public void printTrainInfo(Message m) {
		if(m.getEnvelope().getRoutingKey().contains("sync")){
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			logger.info("sync " + ti.toString());
		} else if(m.getEnvelope().getRoutingKey().contains("new")){
			logger.info("New test started - " + m.getJsonBody());
		} else if(m.getEnvelope().getRoutingKey().contains("stop")){
			sendToBlock("STOP");
		}
		else{
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			logger.info(tu.toString());
		}
		
	}
	
	public void printTimeDifference(Message m) {
		if(m.getEnvelope().getRoutingKey().contains("sync")){
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			logger.info(String.valueOf(System.currentTimeMillis() - ti.getTimeStamp()));
		} else if(m.getEnvelope().getRoutingKey().contains("start")){
			logger.info("New test started - " + m.getJsonBody() + " Current number of trains: " + i++);
		} else if(m.getEnvelope().getRoutingKey().contains("stop")){
			sendToBlock("STOP");
		} else if(m.getEnvelope().getRoutingKey().contains("new")){
			logger.info("New train added with id: " + m.getJsonBody());
		}else{
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			logger.info(String.valueOf(System.currentTimeMillis() - tu.getTimeStamp()));
		}
		
	}

	public String getTopic() {
		return localTopic;
	}

	public void stopp() {
		logger.info("STOPPED monitor");
	}
}
