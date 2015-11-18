package ntnu.no.trainlogger.delta;

import java.io.Serializable;
import java.util.Date;

public class TrainInfo implements Serializable{
	
	private float speed;
	private int id;
	private int position;
	private int trainLength;
	private boolean direction; // True = counterclockwise, False = clockwise
	private TrainStatus status;
	private Date timeStamp;
	
	public TrainInfo(int id, int trainLength) {
		this.id = id;
		this.trainLength = trainLength;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
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
	
	public int getId(){
		return id;
	}

	
	public boolean getDirection(){
		return direction;
	}
	
	public void setDirection(boolean direction){
		this.direction = direction;
	}


	public boolean hasChanges(TrainInfo other){
		if(other.getDirection() != this.direction || other.getSpeed() != this.speed || other.getTrainLength() != this.trainLength
				|| other.getPosition() !=  this.position || this.status.hasStateChanges(other.getStatus())) return true;
		return false;
	}
	
	public TrainInfo getChanges(TrainInfo other){
		TrainInfo changes = new TrainInfo(this.id, this.trainLength);
		if(other.getDirection() != this.direction) changes.setDirection(this.direction);
		if(other.getSpeed() != this.speed) changes.setSpeed(this.speed);
		if(other.getTrainLength() != this.trainLength) changes.setTrainLength(this.trainLength);
		if(this.status.hasStateChanges(other.getStatus())) changes.setStatus(this.status.getChanges(other.getStatus()));
		System.out.println("Changes: " + changes);
		return changes;
	}
	public TrainStatus getStatus() {
		return status;
	}

	public void setStatus(TrainStatus status) {
		this.status = status;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@Override
	public String toString() {
		return String.format("Id: %d, Position: %d, Speed: %f Direction: %s %s", id, position, speed, direction ? "counterclockwise" : "clockwise", status.toString());
	}

	
}
