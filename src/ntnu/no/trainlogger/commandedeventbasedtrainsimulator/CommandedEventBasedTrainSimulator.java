package ntnu.no.trainlogger.commandedeventbasedtrainsimulator;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainCommand;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainInfoUpdate;
import ntnu.no.trainlogger.delta.TrainStatus;
import ntnu.no.trainlogger.enums.TrainCommandType;
import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainEventType;
import ntnu.no.trainlogger.enums.TrainState;

public class CommandedEventBasedTrainSimulator extends Block {
	private TrainInfo traininfo;
	private Random r;
	private int position = 0;
	private int deltaSequenceNumber = 0;
	public int tracklength = 900;

	private HashMap<String, Integer> stations;
	private String[] stationNames = { "Munkegata", "Solsiden", "Gløshaugen", "Lade" };
	
	private boolean running;

	private int stationPosition;
	private float currentspeed;
	private float setspeed;
	private float factor;
	private float currentacceleration;
	
	
	private boolean random;

	// Instance parameter. Edit only in overview page.
	public final int minTimeBetweenPropertyChange;
	// Instance parameter. Edit only in overview page.
	public final int maxTimeBetweenPropertyChange;
	// Instance parameter. Edit only in overview page.
	public final int acceleration;
	// Instance parameter. Edit only in overview page.
	public final int timeBetweenMovementGeneration;
	// Instance parameter. Edit only in overview page.
	public final int thresholdForSpeedUpdate;
	// Instance parameter. Edit only in overview page.
	public final int timeAtStation;
	// Instance parameter. Edit only in overview page.
	public final int timeBetweenSyncronization;
	// Instance parameter. Edit only in overview page.
	public final int maxSpeed;

	public static String getAlias(int i) {
		return String.valueOf(i);
	}

	public static String getAlias(TrainCommand command) {
		return String.valueOf(command.getId());
	}

	public void init(int i) {
		logger.info("Initilizing train with id: " + i);
		factor = (float) (timeBetweenMovementGeneration/1000.0);
		currentacceleration = acceleration * factor;
		r = new Random();
		running = true;
		addStations();
		initTrain(i);
	}

	private void addStations() {
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
		stationPosition = stations.get(ts.getToStation());
		position = stations.get(ts.getFromStation());
		traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
		traininfo.setStatus(ts);
		traininfo.setPosition(position);
		currentspeed = 0;
		setspeed = currentspeed;
		traininfo.setSpeed(currentspeed);
	}

	//Handles commands sent from remote client
	public void handleCommand(TrainCommand command) {
		if (!running)
			return;

		switch (command.getCommandType()) {
			case TO:
				traininfo.getStatus().setToStation(command.getValue());
				sendToBlock("UPDATE", TrainEventType.TO);
				break;
			case FROM:
				traininfo.getStatus().setFromStation(command.getValue());
				sendToBlock("UPDATE", TrainEventType.FROM);
				break;
			case DIRECTION:
				if (traininfo.getDirection() == TrainDirection.CLOCKWISE)
					traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
				else
					traininfo.setDirection(TrainDirection.CLOCKWISE);
				sendToBlock("UPDATE", TrainEventType.DIRECTION);
				break;
			case LENGTH:
				try {
					traininfo.setTrainLength(Integer.parseInt(command.getValue()));
					sendToBlock("UPDATE", TrainEventType.LENGTH);
				} catch (NumberFormatException e) {
					logger.error("Trainlength parameter is not a number: " + command.getValue());
					return;
				}
				break;
			case SPEED:
				try {
					traininfo.getStatus().setState(TrainState.RUNNING);
					setspeed = Float.parseFloat(command.getValue());
				} catch (NumberFormatException e) {
					logger.error("Speed parameter is not a number: " + command.getValue());
				}
				break;
			case STOP:
				setspeed = 0;
				traininfo.getStatus().setState(TrainState.STOPPING);
				sendToBlock("UPDATE", TrainEventType.STATE);
				break;
			default:
				break;
			}
		}

	public void changeProperty() {
		TrainCommand command = getNewCommand();
		if (command != null){
			handleCommand(command);
		}
	}

	private TrainCommand getNewCommand() {
		int i = (int) Math.round(r.nextGaussian() * 2);
		switch (i) {
			case 0:
				String speed = String.valueOf(r.nextFloat() * maxSpeed);
				logger.info("Changed speed for train with id " + traininfo.getId() + " to " + speed);
				return new TrainCommand(traininfo.getId(), TrainCommandType.SPEED, speed);
			case 1:
				String station = stationNames[r.nextInt(stationNames.length)];
				logger.info("Changed to station for train with id: " + traininfo.getId());
				return new TrainCommand(traininfo.getId(), TrainCommandType.TO, station);
			case 2:
				logger.info("Stopping train with id: " + traininfo.getId());
				return new TrainCommand(traininfo.getId(), TrainCommandType.STOP, null);
			case -2:
				if(traininfo.getStatus().getState() == TrainState.ATSTATION){
					logger.info("Changed direction for train with id: " + traininfo.getId());
					return new TrainCommand(traininfo.getId(), TrainCommandType.DIRECTION, null);
				}
			case 3:
				logger.info("Changed train length for train with id: " + traininfo.getId());
				return new TrainCommand(traininfo.getId(), TrainCommandType.LENGTH, String.valueOf(r.nextInt(20)));
			default:
				break;
			}
		return null;
	}

	public void generateMovement() {
		logger.info("Movement generator started");
		Runnable r = new Runnable() {
			@Override
			public void run() {
				float lastsentspeed = currentspeed;
				while (running) {
					if(currentspeed != 0 || setspeed != 0){
						
					
						if (currentspeed == 0 && setspeed > 0) {
							traininfo.getStatus().setState(TrainState.RUNNING);
							if(traininfo.getStatus().getState() == TrainState.STOPPED){
								sendToBlock("UPDATE", TrainEventType.STATE);
							} else if(traininfo.getStatus().getState() == TrainState.ATSTATION){
								traininfo.getStatus().setFromStation(getStationOnPosition(position));
								sendToBlock("UPDATE", TrainEventType.LEAVINGSTATION);
							}						
						}
	
						if (currentspeed != setspeed) {
							calculateSpeed();
							traininfo.setSpeed(currentspeed);
							if (lastsentspeed - currentspeed > thresholdForSpeedUpdate
									|| currentspeed - lastsentspeed > thresholdForSpeedUpdate) {
								lastsentspeed = currentspeed;
								sendToBlock("UPDATE", TrainEventType.SPEED);
							} else if(currentspeed == setspeed){
								lastsentspeed = currentspeed;
								sendToBlock("UPDATE", TrainEventType.SPEED);
							}
						}
						
						if (currentspeed == 0 && traininfo.getStatus().getState() == TrainState.STOPPING) {
							traininfo.getStatus().setState(TrainState.STOPPED);
							sendToBlock("UPDATE", TrainEventType.STATE);
						}
						
						calculatePosition();
						traininfo.setPosition(position);
						
						if(position == stationPosition && traininfo.getStatus().getState() != TrainState.ATSTATION){
							traininfo.getStatus().setState(TrainState.ATSTATION);
							currentspeed = 0;
							setspeed = 0;
							traininfo.setSpeed(currentspeed);
							sendToBlock("UPDATE", TrainEventType.ATSTATION);
						}
					}
					try {
						Thread.sleep(timeBetweenMovementGeneration);
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
					}
				}
			}
		};
		runAsync(r);
	}
	
	private void calculateSpeed(){
		if (currentspeed < setspeed) {
			if ((setspeed - currentspeed) <= currentacceleration)
				currentspeed = setspeed;
			else
				currentspeed += currentacceleration;
		} else {
			if ((currentspeed - setspeed) <= currentacceleration)
				currentspeed = setspeed;
			else
				currentspeed -= currentacceleration;
		}
	}
	
	public void calculatePosition(){
		if(currentspeed == 0) return;
		
		int prevPosition = position;
		int stationPosition = stations.get(traininfo.getStatus().getToStation());
		position = (traininfo.getDirection() == TrainDirection.COUNTERCLOCKWISE ? Math.round(position + (currentspeed * factor)) : Math.round(position - (currentspeed * factor)));
		
		if(position >=  stationPosition && prevPosition <= stationPosition) position = stationPosition;
		if(position <=  stationPosition && prevPosition >= stationPosition) position = stationPosition;

		if (position < 0)
			position += tracklength;
		if (position > tracklength)
			position -= tracklength;
	}
	
	public String getStationOnPosition(int position){
		for(Entry<String, Integer> entry : stations.entrySet()){
			if(entry.getValue() == position) return entry.getKey();
		}
		return null;
	}

	public TrainInfo getTrainInfo() {
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

	public void random() {
		random = !random;
	}

	public boolean isRandom() {
		return random;
	}

	// Creates a new TrainInfoUpdate object based on the update event
	public TrainInfoUpdate handleUpdate(TrainEventType event) {
		TrainInfoUpdate td = new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++, event);
		switch (event) {
		case SPEED:
			td.setSpeed(currentspeed);
			break;
		case TO:
			td.setToStation(traininfo.getStatus().getToStation());
			break;
		case FROM:
			td.setFromStation(traininfo.getStatus().getFromStation());
			break;
		case ATSTATION:
			td.setState(traininfo.getStatus().getState());
			getNewStation();
			break;
		case LEAVINGSTATION:
			td.setFromStation(traininfo.getStatus().getFromStation());
			td.setToStation(traininfo.getStatus().getToStation());
			td.setState(traininfo.getStatus().getState());
			break;
		case DIRECTION:
			td.setDirection(traininfo.getDirection());
			break;
		case STATE:
			td.setState(traininfo.getStatus().getState());
			break;
		case LENGTH:
			td.setTrainLength(traininfo.getTrainLength());
			break;
		default:
			logger.warn("Event of unknown type");
			break;
		}
		return td;
	}

	private void getNewStation() {
		if(!random) return;
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(timeAtStation * 1000);
					setspeed = new Random().nextInt(maxSpeed);
					traininfo.getStatus().setToStation(stationNames[new Random().nextInt(stationNames.length-1)]);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage());
				}
			}
		};
		runAsync(r);
		
	}

	// Do not edit this constructor.
	public CommandedEventBasedTrainSimulator(int minTimeBetweenPropertyChange, int maxTimeBetweenPropertyChange, int acceleration, int timeBetweenMovementGeneration, int thresholdForSpeedUpdate, int timeAtStation, int timeBetweenSyncronization, int maxSpeed) {
	    this.minTimeBetweenPropertyChange = minTimeBetweenPropertyChange;
	    this.maxTimeBetweenPropertyChange = maxTimeBetweenPropertyChange;
	    this.acceleration = acceleration;
	    this.timeBetweenMovementGeneration = timeBetweenMovementGeneration;
	    this.thresholdForSpeedUpdate = thresholdForSpeedUpdate;
	    this.timeAtStation = timeAtStation;
	    this.timeBetweenSyncronization = timeBetweenSyncronization;
	    this.maxSpeed = maxSpeed;
	}
}
