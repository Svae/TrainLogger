package ntnu.no.trainlogger.loggsimulator.component;

import java.util.Date;
import java.util.HashMap;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainDelta;
import ntnu.no.trainlogger.delta.TrainInfo;

public class Component extends Block {

	private Message msg;
	private TrainDelta delta;
	private int inc = 1;
	
	public HashMap<String, String> initAMQP() {
		HashMap<String, String> p = new HashMap<String, String>();
		p.put("HOST", "192.168.0.100");
		p.put("USERNAME", "alexander");
		p.put("PASSWORD", "broker123");
		p.put("EXCHANGE", "train_communication");
		return p;
	}

	public Message createLogMsg(TrainDelta delta) {
		msg = new Message("trains.log", delta);
		return msg;
	}

	public TrainDelta createTrainInfo() {
		TrainDelta delta = new TrainDelta();
		TrainInfo ti;
		for (int i = 0; i < 4; i++) {
			ti = new TrainInfo(i, 10);
			ti.setPosition(7*i + 3*inc);
			ti.setSpeed(10);
			delta.addTrainInfo(ti);
		}
		inc++;
		delta.setTimeStamp(new Date());
		return delta;
	}


}
