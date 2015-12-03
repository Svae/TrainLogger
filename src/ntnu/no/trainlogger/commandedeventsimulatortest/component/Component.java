package ntnu.no.trainlogger.commandedeventsimulatortest.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainCommand;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainInfoUpdate;
import ntnu.no.trainlogger.delta.TrainInfoUpdateSerializer;
import ntnu.no.trainlogger.enums.TrainCommandType;

public class Component extends Block {
	private GsonBuilder gb;
	private Gson g;
	int id = 1;
	HashMap<Integer, Integer> syncSeq;
	HashMap<Integer, Integer> updateSeq;

	public void init() {
		syncSeq = new HashMap<>();
		updateSeq = new HashMap<>();
		gb = new GsonBuilder();
		gb.registerTypeAdapter(TrainInfoUpdate.class, new TrainInfoUpdateSerializer());
		g = gb.create();
		startInputThread();
	}
	
	private void startInputThread(){
		logger.info("Enter commands:");
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				while(true){
					try{
					    BufferedReader bufferRead = new BufferedReader(new InputStreamReader( tem.in));
					    String s = bufferRead.readLine();
					    if(s.equals("EXIT")){
					    	sendToBlock("STOP");
					    	return;
					    }
					    parseParams(s);
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		runAsync(r);
		
	}
	
	private void parseParams(String s){
		String[] p = s.split(",");
		switch (p[0]) {
		case "RANDOM":
			sendToBlock("RANDOM", p[1]);
			break;
		case "STOPTRAINSIM":
			sendToBlock("STOPTRAIN", p[1]);
			break;
		case "STARTTRAIN":
			syncSeq.put(id, 1);
			updateSeq.put(id, 0);
			sendToBlock("STARTTRAIN", id++);
			break;
		default:
			sendToBlock("NEWCOMMAND", new TrainCommand(Integer.parseInt(p[1]), TrainCommandType.valueOf(p[0]), p[2]));
			break;
		}
		
	}

	public void printSync(TrainInfo ti) {
		if (ti.getSequenceNumber() != syncSeq.get(ti.getId())) {
			logger.error("Expected packet with seqnr. " + syncSeq.get(ti.getId()) + " got " + ti.getSequenceNumber());
			syncSeq.put(ti.getId(), ti.getSequenceNumber() + 1);
		} else
			syncSeq.put(ti.getId(), ti.getSequenceNumber() + 1);
		System.out.println(g.toJson(ti));
	}

	public void printUpdate(TrainInfoUpdate tu) {
		String s = g.toJson(tu);
		System.out.println(s);
	}
}
