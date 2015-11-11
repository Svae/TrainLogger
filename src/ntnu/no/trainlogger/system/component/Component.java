package ntnu.no.trainlogger.system.component;

import java.util.Date;
import java.util.HashMap;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainDelta;
import ntnu.no.trainlogger.delta.TrainInfo;

import com.google.gson.Gson;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class Component extends Block {

	public HashMap<String, String> initAMQP() {
		HashMap<String, String> p = new HashMap<String, String>();
		p.put("HOST", "192.168.0.100");
		p.put("USERNAME", "alexander");
		p.put("PASSWORD", "broker123");
		return p;
	}

	public String subscribe() {
		return "trains.log";
	}

	public void getTrainDelta(Message msg) {
		TrainDelta delta = deserilaze(msg.getJsonBody());
		if(delta != null){
			System.out.println("------------------------------------------");
			System.out.println(msg.getSentTimeStamp());
			System.out.println(msg.getReceivedTimeStap());
			System.out.println("------------------------------------------");
			printTrainInfo(delta.getTrains());
		} else {
			logger.error("Wrong object type in message body");
		}
		System.out.println("------------------------------------------");
	}

	private void printTrainInfo(HashMap<Integer, TrainInfo> trains) {
		for (TrainInfo trainInfo : trains.values()) {
			System.out.println(trainInfo.toString());
		}		
	}

	private TrainDelta deserilaze(String json){
		return new Gson().fromJson(json, TrainDelta.class);
	}
}
