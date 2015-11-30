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

	public HashMap<AMQPProperties, String> initAMQP() {
		HashMap<AMQPProperties, String> p = new HashMap<>();
		p.put(AMQPProperties.EXCHANGENAME, "logs");
		p.put(AMQPProperties.HOSTNAME, "localhost");
		return p;
	}

	public void printTrainInfo(Message m) {
		if(m.getEnvelope().getRoutingKey().contains("sync")){
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			System.out.println(ti);
		} else{
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			System.out.println(tu.printChanges());
		}
		
	}
	
	public void printTimeDifference(Message m) {
		if(m.getEnvelope().getRoutingKey().contains("sync")){
			TrainInfo ti = g.fromJson(m.getJsonBody(), TrainInfo.class);
			System.out.println(ti.getTimeStamp() - System.currentTimeMillis());
		} else{
			TrainInfoUpdate tu = g.fromJson(m.getJsonBody(), TrainInfoUpdate.class);
			System.out.println(tu.getTimeStamp() - System.currentTimeMillis());
		}
		
	}

	public String getTopic() {
		return localTopic;
	}
}
