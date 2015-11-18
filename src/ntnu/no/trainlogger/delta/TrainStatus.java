package ntnu.no.trainlogger.delta;

import ntnu.no.trainlogger.enums.TrainState;

public class TrainStatus {

	private TrainState state = TrainState.WAITING;
	private String toStation = "";
	private String fromStation = "";
	
	public TrainStatus(TrainState state) {
		this.state = state;
	}
	
	public TrainStatus() {}

	public TrainState getState() {
		return state;
	}
	public void setState(TrainState state) {
		this.state = state;
	}
	public String getToStation() {
		return toStation;
	}
	public void setToStation(String destination) {
		this.toStation = destination;
	}
	public String getFromStation() {
		return fromStation;
	}
	public void setFromStation(String from) {
		this.fromStation = from;
	}
	
	public boolean hasStateChanges(TrainStatus other){
		if(other.getState() != this.state || !other.getFromStation().equals(this.fromStation) || other.getToStation().equals(this.toStation)) return true;
		return false;
	}
	
	public TrainStatus getChanges(TrainStatus other){
		TrainStatus changes = new TrainStatus();
		if(other.getState() != this.state) changes.setState(this.state);
		if(!other.getFromStation().equals(this.fromStation)) changes.setFromStation(this.fromStation); 
		if(!other.getToStation().equals(this.toStation)) changes.setToStation(this.toStation);
		return changes;
	}
	
	@Override
	public String toString() {
		return String.format("State: %s To: %s From: %s", state.toString(), toStation, fromStation);
	}
}
