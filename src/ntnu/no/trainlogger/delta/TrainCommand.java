package ntnu.no.trainlogger.delta;

import ntnu.no.trainlogger.enums.TrainCommandType;

public class TrainCommand {

	private int id;
	private TrainCommandType commandType;
	private String value;
	
	public TrainCommand(int id, TrainCommandType commandType, String value) {
		this.id = id;
		this.commandType = commandType;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public TrainCommandType getCommandType() {
		return commandType;
	}

	public String getValue() {
		return value;
	}
	
	
}
