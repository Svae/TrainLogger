package ntnu.no.trainlogger.commandedeventbasedtrainsimulator;

import java.util.HashMap;
import java.util.Random;
import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainCommand;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainInfoUpdate;
import ntnu.no.trainlogger.delta.TrainStatus;
import ntnu.no.trainlogger.enums.TrainCommandType;
import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainState;

public class CommandedEventBasedTrainSimulator extends Block {
	private TrainInfo traininfo;
	private Random r;
	private float currentspeed;
	private float setspeed;
	private boolean running;
	private int deltaSequenceNumber = 0;
	private int position = 0;
	private HashMap<String, Integer> stations;
	private String[] stationNames = { "Munkegata", "Solsiden", "Gløshaugen", "Lade" };
	private double accel;
	public int timeBetweenNewTrainInfo;
	public int tracklength = 900;
	public int timeBetweenSyncronization;
	private boolean random = false;
	// Instance parameter. Edit only in overview page.
	public final int minTimeBetweenPropertyChange;
	// Instance parameter. Edit only in overview page.
	public final int maxTimeBetweenPropertyChange;
	// Instance parameter. Edit only in overview page.
	public final int timeBetweenSync;
	// Instance parameter. Edit only in overview page.
	public final int acceleration;
	// Instance parameter. Edit only in overview page.
	public final int timeBetweenMovementGeneration;
	// Instance parameter. Edit only in overview page.
	public final int thresholdForSpeedUpdate;

	public static String getAlias(int i) {
		return String.valueOf(i);
	}

	public static String getAlias(TrainCommand command) {
		return String.valueOf(command.getId());
	}

	public void init(int i) {
		logger.info("Initilizing train with id: " + i);
		accel = acceleration / (1000.0/timeBetweenMovementGeneration);
		r = new Random();
		running = true;
		addStations();
		initTrain(i);
	}
	
	private void addStations(){
		int i = 100;
		stations = new HashMap<>();
		for (String stationname : stationNames) {
			stations.put(stationname, i);
			i += 200;
		}
	}

	private void initTrain(int i) {
		traininfo = new TrainInfo(i, r.nextInt(19) + 1);
		TrainStatus ts = new TrainStatus(TrainState.STOPPED);
		ts.setFromStation(stationNames[r.nextInt(4)]);
		ts.setToStation(stationNames[r.nextInt(4)]);
		traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
		traininfo.setStatus(ts);
		traininfo.setPosition(position);
		currentspeed = 0;
		setspeed = currentspeed;
		traininfo.setSpeed(currentspeed);
	}

	public void handleCommand(TrainCommand command) {
		if(!running)return;
		switch (command.getCommandType()) {
		case TO:
			if(traininfo.getStatus().getToStation().equals(command.getValue())) return;
			traininfo.getStatus().setToStation(command.getValue());
			break;
		case FROM:
			if(traininfo.getStatus().getFromStation().equals(command.getValue())) return;
			traininfo.getStatus().setFromStation(command.getValue());
			break;
		case DIRECTION:
			if (traininfo.getDirection() == TrainDirection.CLOCKWISE)
				traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
			else
				traininfo.setDirection(TrainDirection.CLOCKWISE);
			break;
		case LENGTH:
			try {
				if(traininfo.getTrainLength() == Integer.parseInt(command.getValue())) return;
				traininfo.setTrainLength(Integer.parseInt(command.getValue()));
			} catch (NumberFormatException e) {
				logger.error("Trainlength parameter is not a number: " + command.getValue());
				return;
			}
		case SPEED:
			try {
				traininfo.getStatus().setState(TrainState.RUNNING);
				setspeed = Integer.parseInt(command.getValue());
			} catch (NumberFormatException e) {
				logger.error("Given speed parameter is not a number: " + command.getValue());
			}
			return;
		case STOP:
			setspeed = 0;
			traininfo.getStatus().setState(TrainState.STOPPING);
			sendToBlock("STATECHANGE");
			return;
		default:
			break;
		}
		sendToBlock(command.getCommandType().toString().concat("CHANGE"));
	}

	public void changeProperty(){
		TrainCommand command = getNewCommand();
		if(command != null) handleCommand(command);
	}
	
	private TrainCommand getNewCommand() {
		int i = (int) Math.round(r.nextGaussian() * 2);
		switch (i) {
		case 0:
			return new TrainCommand(traininfo.getId(), TrainCommandType.SPEED, String.valueOf(r.nextFloat() * 10 + 5));
		case 1:
			return new TrainCommand(traininfo.getId(), TrainCommandType.TO, stationNames[r.nextInt(stationNames.length)]);
		case -1:
			return new TrainCommand(traininfo.getId(), TrainCommandType.FROM, stationNames[r.nextInt(stationNames.length)]);
		case 2:
			return new TrainCommand(traininfo.getId(), TrainCommandType.STOP, null);
		case -2:
			return new TrainCommand(traininfo.getId(), TrainCommandType.DIRECTION, null);
		case 3:
			return new TrainCommand(traininfo.getId(), TrainCommandType.LENGTH, String.valueOf(r.nextInt(20)));
		default:
			break;
		}
		return null;
	}

	public void generateMovement() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				float lastsentspeed = currentspeed;
				while (running) {
					if (currentspeed == 0 && (traininfo.getStatus().getState() == TrainState.STOPPED || traininfo.getStatus().getState() == TrainState.ATSTATION) && setspeed > 0) {
						traininfo.getStatus().setState(TrainState.RUNNING);
						sendToBlock("STATECHANGE");
					}
					if (currentspeed != setspeed) {
						if (currentspeed < setspeed) {
							if ((setspeed - currentspeed) <= accel)
								currentspeed = setspeed;
							else
								currentspeed += accel;
						} else {
							if ((currentspeed - setspeed) <= accel)
								currentspeed = setspeed;
							else
								currentspeed -= accel;
						}
						traininfo.setSpeed(currentspeed);
						if(lastsentspeed - currentspeed > thresholdForSpeedUpdate || currentspeed - lastsentspeed > thresholdForSpeedUpdate) {
							lastsentspeed = currentspeed;
							sendToBlock("SPEEDCHANGE");
						}
					}
					if (currentspeed == 0 && traininfo.getStatus().getState() == TrainState.STOPPING) {
						traininfo.getStatus().setState(TrainState.STOPPED);
						sendToBlock("STATECHANGE");
					}
					position = traininfo.getDirection() == TrainDirection.COUNTERCLOCKWISE
							? Math.round(position + (currentspeed / 10)) : Math.round(position - (currentspeed / 10));
					if (position < 0) position += tracklength;
					if (position > tracklength) position -= tracklength;
					if(traininfo.getStatus().getState() != TrainState.ATSTATION && position == stations.get(traininfo.getStatus().getToStation())){
						traininfo.getStatus().setState(TrainState.ATSTATION);
						sendToBlock("STATECHANGE");
					}
					if(traininfo.getStatus().getState() == TrainState.ATSTATION && position != stations.get(traininfo.getStatus().getToStation()) && currentspeed > 0){
						traininfo.getStatus().setState(TrainState.RUNNING);
						sendToBlock("STATECHANGE");
					}
					try {
						Thread.sleep(timeBetweenMovementGeneration);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		runAsync(r);
	}

	public TrainInfo getTrainInfo() {
		traininfo.setPosition(position);
		traininfo.incrementSequenceNumber();
		traininfo.setTimeStamp(System.currentTimeMillis());
		return running ? traininfo : null;
	}

	public void startStop() {
		running = !running;
		currentspeed = 0;
		setspeed = currentspeed;
		traininfo.setSpeed(0);
	}

	public boolean running() {
		return running;
	}

	public int getNewTimeout() {
		return minTimeBetweenPropertyChange
				+ r.nextInt(maxTimeBetweenPropertyChange - minTimeBetweenPropertyChange + 1);
	}

	public void stop() {
		running = false;
	}

	public void random(){
		random = !random;
	}
	public boolean isRandom(){
		return random ;
	}
	public TrainInfoUpdate speedChange() {
		TrainInfoUpdate td = new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++);
		td.setSpeed(currentspeed);
		return td;
	}

	public TrainInfoUpdate stateChange() {
		TrainInfoUpdate td = new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++);
		td.setState(traininfo.getStatus().getState());
		return td;
	}

	public TrainInfoUpdate directionChange() {
		TrainInfoUpdate td = new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++);
		td.setDirection(traininfo.getDirection());
		return td;
	}

	public TrainInfoUpdate toStationChange() {
		TrainInfoUpdate td = new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++);
		td.setToStation(traininfo.getStatus().getToStation());
		return td;
	}

	public TrainInfoUpdate fromStationChange() {
		TrainInfoUpdate td = new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++);
		td.setFromStation(traininfo.getStatus().getFromStation());
		return td;
	}

	public TrainInfoUpdate trainLengthChange() {
		TrainInfoUpdate td = new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++);
		td.setTrainLength(traininfo.getTrainLength());
		return td;
	}

	// Do not edit this constructor.
	public CommandedEventBasedTrainSimulator(int minTimeBetweenPropertyChange, int maxTimeBetweenPropertyChange, int timeBetweenSync, int acceleration, int timeBetweenMovementGeneration, int thresholdForSpeedUpdate) {
	    this.minTimeBetweenPropertyChange = minTimeBetweenPropertyChange;
	    this.maxTimeBetweenPropertyChange = maxTimeBetweenPropertyChange;
	    this.timeBetweenSync = timeBetweenSync;
	    this.acceleration = acceleration;
	    this.timeBetweenMovementGeneration = timeBetweenMovementGeneration;
	    this.thresholdForSpeedUpdate = thresholdForSpeedUpdate;
	}
}
