package ntnu.no.trainlogger.controllergui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainInfo;

public class ControllerGUI extends Block {
	
	public JFrame mainFrame;
	private String[] properties = {"ID, Speed","Position", "Direction", "State", "To", "From"};
	private JTextField speed, to, from = new JTextField();
	private JButton startStop;
	private JLabel lid, lspeed, lposition, ldirection, lstate, lto, lfrom = new JLabel("");
	private int id;
	public void init(int id){
		this.id = id;
		logger.info("Initilizing GUI");
		openGUI();
	}
	
	public static String getAlias(int i) {
		return String.valueOf(i);
	}
	
	public static String getAlias(TrainInfo ti){
		return String.valueOf(ti.getId());
	}
	
	private void openGUI(){
		mainFrame = new JFrame("Train Controller");
		mainFrame.setLayout(new GridLayout(7, 3));
		mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
		        sendToBlock("STOPTRAINSIM", id);
		        sendToBlock("STOP");
	         }        	
	      });
		for (String string : properties) {
			mainFrame.add(new JLabel(string));
		}
		lid.setText(String.valueOf(id));
		mainFrame.add(lid);
		mainFrame.add(lspeed);
		mainFrame.add(lposition);
		mainFrame.add(ldirection);
		mainFrame.add(lstate);
		mainFrame.add(lto);
		mainFrame.add(lfrom);
		startStop = new JButton("Stop");
		startStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startStop.setText("Start");
				sendToBlock("NEWACTION", "STOPTRAINSIM,"+id);
				
			}
		});
		mainFrame.add(startStop);
		
		Action action = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = String.format("%s,%d,%s", ((JTextField)e.getSource()).getName(), id, ((JTextField)e.getSource()).getText());
				sendToBlock("NEWACTION", s);
				
			}
		};
		speed.setName("SPEED");
		speed.addActionListener(action);
		mainFrame.add(speed);
		
		to.setName("TO");
		to.addActionListener(action);
		mainFrame.add(to);
		
		from.setName("FROM");
		from.addActionListener(action);
		mainFrame.add(from);
		mainFrame.pack();
		mainFrame.setVisible(true);  
		System.out.println("FERDIG");
		
		
	}
	
	public void updateInfo(TrainInfo ti){
		lspeed.setText(String.valueOf(ti.getSpeed()));
		lposition.setText(String.valueOf(ti.getPosition()));
		ldirection.setText(ti.getDirection().toString());
		lstate.setText(ti.getStatus().getState().toString());
		lto.setText(ti.getStatus().getToStation());
		lfrom.setText(ti.getStatus().getFromStation());
	}

}
