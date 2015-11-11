package ntnu.no.trainlogger.delta;

import java.util.ArrayList;
import java.util.Date;

public class Delta {
	
	private Date timeStamp;
//	private String modelVersion;
//	private String receiverVersion;
//	
//	private ArrayList<SleeperDelta> sleepers;
	private ArrayList<TrainDelta> trains;
	
	public Delta(Date timeStamp, String modelVerison, String receiverVersion, ArrayList<TrainDelta> trains) {
		this.timeStamp = timeStamp;
		this.trains = trains;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public ArrayList<TrainDelta> getTrains() {
		return trains;
	}
	

}
