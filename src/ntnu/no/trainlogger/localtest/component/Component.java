package ntnu.no.trainlogger.localtest.component;

import java.util.HashMap;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainDelta;
import ntnu.no.trainlogger.delta.TrainInfo;

public class Component extends Block {

	private int i = 0;
	public int init() {
		return i++;
	}

	public void printdelta(TrainDelta delta) {
		printTrainInfo(delta.getTrains());
	}
	
	private void printTrainInfo(HashMap<Integer, TrainInfo> trains) {
		for (TrainInfo trainInfo : trains.values()) {
			System.out.println(trainInfo.toString());
		}		
	}

	public TrainInfo p(TrainInfo ti) {
		//System.out.println("P:" + ti.toString());
		return ti;
	}

}
