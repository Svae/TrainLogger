package ntnu.no.trainlogger.delta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ntnu.no.trainlogger.enums.SleeperType;
import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainState;

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
	
	public static void main(String[] args) {
		Gson g = new Gson();
		/*TrainInfo ti = new TrainInfo(1, 10);
		TrainStatus ts = new TrainStatus(TrainState.RUNNING);
		ts.setFromStation("Lade");
		ts.setToStation("Solsiden");
		ti.setStatus(ts);
		ti.setSpeed(5);
		ti.setDirection(TrainDirection.CLOCKWISE);
		ti.setPosition(10);
		String s = g.toJson(ti);
		System.out.println(s);*/
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(TrainInfoUpdate.class, new TrainInfoUpdateSerializer());
		Gson g1 = gb.create();
		TrainInfoUpdate tu = new TrainInfoUpdate(2, System.currentTimeMillis(), 0);
		String s = g1.toJson(tu);
		System.out.println(s);
		TrainInfoUpdate tu1 = g.fromJson(s, TrainInfoUpdate.class);
		System.out.println(tu.toString());
		Object l = s;
	}

}
