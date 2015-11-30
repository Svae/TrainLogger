package ntnu.no.trainlogger.faketrainsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainStatus;
import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainState;

public class FakeTrainSimulator extends Block {
	private TrainInfo traininfo;
	private Random r;
	private float speed;
	private boolean running;
	private String[] stations = { "Munkegata", "Solsiden", "Gløshaugen", "Lade" };
	private ArrayList<Integer> pr;

	public void init(int i) {
		r = new Random();
		traininfo = new TrainInfo(i, r.nextInt(19) + 1);
		TrainStatus ts = new TrainStatus(TrainState.RUNNING);
		ts.setFromStation(stations[r.nextInt(4)]);
		ts.setToStation(stations[r.nextInt(4)]);
		traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
		traininfo.setStatus(ts);
		traininfo.setPosition(0);
		running = true;
		speed = r.nextFloat() * 10;
		traininfo.setSpeed(speed);
		pr = new ArrayList<Integer>();
		pr.add(6);
		pr.add(3);
		pr.add(6);
		pr.add(0);
		pr.add(2);
		pr.add(3);
		pr.add(6);
		pr.add(2);
		pr.add(2);
	}

	public void changeProperty() {
		if(pr.size() > 0){
		int i = pr.remove(0);
		switch (i) {
		case 0:
			traininfo.getStatus().setToStation(stations[r.nextInt(4)]);
			break;
		case 1:
			traininfo.getStatus().setFromStation(stations[r.nextInt(4)]);
			break;
		case 2:
			traininfo.getStatus().setState(TrainState.RUNNING);
			speed = r.nextFloat() * 10;
			traininfo.setSpeed(speed);
			break;
		case 3:
			speed = 0;
			traininfo.setSpeed(speed);
			traininfo.getStatus().setState(TrainState.STOPPED);
			break;
		case 4:
			if(traininfo.getDirection() == TrainDirection.CLOCKWISE) traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
			else traininfo.setDirection(TrainDirection.CLOCKWISE);
		default:
			break;
		}
		}
	}

	public void generateMovement() {
		int pos = traininfo.getPosition();
		traininfo.setPosition(traininfo.getDirection() == TrainDirection.COUNTERCLOCKWISE ? pos + (int) (speed) : pos - (int) (speed));
	}

	public TrainInfo getTrainInfo() {
		return running ? traininfo : null;
	}

	public void startStop() {
		running = !running;
	}

	public boolean running() {
		return running;
	}
}
