package ntnu.no.trainlogger.delta;

import ntnu.no.trainlogger.enums.SleeperType;

public class SleeperInfo {
	
	private int id;
	private SleeperType type;
	private int frontSleeperId;
	private int backSleeperId;
	
	public SleeperInfo(int id, SleeperType type, int frontSleeperId, int backSleeperID) {
		this.id = id;
		this.type = type;
		this.frontSleeperId = frontSleeperId;
		this.backSleeperId = backSleeperID;
	}

	public int getId() {
		return id;
	}

	public SleeperType getType() {
		return type;
	}

	public int getFrontSleeperId() {
		return frontSleeperId;
	}

	public int getBackSleeperId() {
		return backSleeperId;
	}

}
