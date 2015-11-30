package ntnu.no.trainlogger.delta;

import java.io.Serializable;
import java.util.Date;

import ntnu.no.trainlogger.enums.TrainDirection;

public class TrainInfo implements Serializable{
	
	private int id;
	private int position;
	private int trainLength;
	
	private float speed;
	private TrainDirection direction;
	private TrainStatus status;
	private long timeStamp;
	private int sequenceNumber = 0;
	
	public TrainInfo(int id, int trainLength) {
		this.id = id;
		this.trainLength = trainLength;
		direction = TrainDirection.NONE;
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

	
	public TrainDirection getDirection(){
		return direction;
	}
	
	public void setDirection(TrainDirection direction){
		this.direction = direction;
	}
	
	public int getSequenceNumber(){
		return sequenceNumber;
	}

	public void incrementSequenceNumber(){
		sequenceNumber++;
	}

	public boolean hasChanges(TrainInfo other){
		if(other.getDirection() != this.direction || other.getSpeed() != this.speed || other.getTrainLength() != this.trainLength
				|| other.getPosition() !=  this.position || this.status.hasStateChanges(other.getStatus())) return true;
		return false;
	}
	
	public TrainInfo getChanges(TrainInfo other){
		TrainInfo changes = new TrainInfo(this.id, this.trainLength);
		changes.setTimeStamp(this.timeStamp);
		if(other.getPosition() != this.position) changes.setPosition(this.position);
		if(other.getDirection() != this.direction) changes.setDirection(this.direction);
		if(other.getSpeed() != this.speed) changes.setSpeed(this.speed);
		if(other.getTrainLength() != this.trainLength) changes.setTrainLength(this.trainLength);
		if(this.status.hasStateChanges(other.getStatus())) changes.setStatus(this.status.getChanges(other.getStatus()));
		return changes;
	}
	public TrainStatus getStatus() {
		return status;
	}

	public void setStatus(TrainStatus status) {
		this.status = status;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@Override
	public String toString() {
		return String.format("Time: %s Id: %d, Length: %d Position: %d, Speed: %f Direction: %s %s", new Date(timeStamp).toString(), id, trainLength, position, speed, direction, status == null ? "" : status.toString());
	}

	public String printChanges() {
		StringBuilder s = new StringBuilder();
		s.append("ID: " + id);
		if(position != 0)s.append(" Position: " + position);
		if(speed != 0) s.append(" Speed: " + speed);
		if(direction != TrainDirection.NONE) s.append(" Direction: " + direction);
		if(status != null) s.append(status.printChanges());
		return s.toString();
	}

	
}
