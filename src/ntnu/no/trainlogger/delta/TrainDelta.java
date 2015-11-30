package ntnu.no.trainlogger.delta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TrainDelta implements Serializable{

	private HashMap<Integer, TrainInfo> trains;
	private long timeStamp;
	
	public TrainDelta() {
		trains = new HashMap<Integer, TrainInfo>();
	}
	
	public void addTrainInfo(TrainInfo train){
		trains.put(train.getId(), train);
	}
	
	public void removeTrain(int id){
		trains.remove(id);
	}
	
	public void updateTrainInfo(TrainInfo traininfo){
		trains.put(traininfo.getId(), traininfo);
	}
	
	public HashMap<Integer, TrainInfo> getTrains(){
		return trains;
	}

	public TrainDelta getChanges(TrainDelta other) {
		TrainDelta changes = new TrainDelta();
		HashMap<Integer, TrainInfo> otherTrains = other.getTrains();
		TrainInfo otherTrainInfo;
		for (Map.Entry<Integer, TrainInfo> entry : trains.entrySet()) {
			if(otherTrains.containsKey(entry.getKey())){
				otherTrainInfo = otherTrains.get(entry.getKey());
				if(otherTrainInfo.hasChanges(entry.getValue())) changes.addTrainInfo(entry.getValue());
			} else {
				changes.addTrainInfo(entry.getValue());
			}
		}
		return changes;
	}

		
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
}
