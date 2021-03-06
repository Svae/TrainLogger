package ntnu.no.trainlogger.eventsimulatortest.component;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainInfoUpdate;
import ntnu.no.trainlogger.delta.TrainInfoUpdateSerializer;

public class Component extends Block {

	private GsonBuilder gb;
	private Gson g;

	int seqSync = 1;
	int seqUpdate = 0;
	HashMap<Integer, Integer> syncSeq;
	HashMap<Integer, Integer> updateSeq;
	long prev;
	
	public int init(){
		gb = new GsonBuilder();
		gb.registerTypeAdapter(TrainInfoUpdate.class, new TrainInfoUpdateSerializer());
		g = gb.create();
		prev = System.currentTimeMillis();
		return 1;
		
	}

	public void printSync(TrainInfo ti){
		
//		if(ti.getSequenceNumber() != syncSeq.get(ti.getId())){
//			logger.error("Expected packet with seqnr. " + syncSeq.get(ti.getId()) + " got " + ti.getSequenceNumber());
//			syncSeq.put(ti.getId(), ti.getSequenceNumber() + 1);
//		} else syncSeq.put(ti.getId(), ti.getSequenceNumber() + 1);
		
		logger.info(g.toJson(ti));
	}
	
	public void printUpdate(TrainInfoUpdate tu){
		String s = g.toJson(tu);
		System.out.println(s);
	}

}
