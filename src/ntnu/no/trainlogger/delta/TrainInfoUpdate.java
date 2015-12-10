package ntnu.no.trainlogger.delta;

import java.sql.Date;
import java.util.HashMap;

import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainEventType;
import ntnu.no.trainlogger.enums.TrainState;

public class TrainInfoUpdate {
	private int id;
	
	private int trainLength;
	private float speed;
	private TrainDirection direction;
	private TrainState state;
	private String toStation = "";
	private String fromStation = "";
	private long timeStamp;
	private int sequenceNumber;
	private TrainEventType event;
	
	public TrainInfoUpdate(int id) {
		this.id = id;
	}
	
	public TrainInfoUpdate(int id, long timeStamp, int sequenceNumber, TrainEventType event) {
		this.timeStamp = timeStamp;
		this.sequenceNumber = sequenceNumber;
		this.id = id;
		this.event = event;
	}
	
	public TrainEventType getEvent(){
		return event;
	}
	
	public void setEvent(TrainEventType event){
		this.event = event;
	}
	
	public int getId() {
		return id;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public int getTrainLength() {
		return trainLength;
	}

	public void setTrainLength(int trainLength) {
		this.trainLength = trainLength;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public TrainDirection getDirection() {
		return direction;
	}

	public void setDirection(TrainDirection direction) {
		this.direction = direction;
	}

	public TrainState getState() {
		return state;
	}

	public void setState(TrainState state) {
		this.state = state;
	}

	public String getToStation() {
		return toStation;
	}

	public void setToStation(String toStation) {
		this.toStation = toStation;
	}

	public String getFromStation() {
		return fromStation;
	}

	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}

	public String printChanges() {
		StringBuilder sb = new StringBuilder();
		sb.append("Time: " + new Date(timeStamp).toString());
		sb.append(" ID: " + id);
		if(trainLength != 0) sb.append(" Length: " + trainLength);
		if(speed != 0) sb.append(" Speed: " + speed);
		if(direction != null) sb.append(" Direction: " + direction);
		if(state != null)sb.append(" State: " + state);
		if(toStation != null)sb.append(" To: " + toStation);
		if(fromStation != null)sb.append(" From: " + fromStation);
		return sb.toString();
	}
	
	
	@Override
	public String toString() {

		return String.format("%s Id: %d, SequenceNumber: %d Speed: %f Direction: %s State: %s To: %s From: %s Trainlength: %d", 
				new Date(timeStamp).toString(), id, sequenceNumber, speed, direction == null ? "" : direction.toString(), state == null ? "" : state.toString(), toStation, fromStation, trainLength);
	}
	
	
	
}
