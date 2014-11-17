package org.arhub.robots;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Program {

	public static void main(String[] args) throws Exception{
		
		 JFrame hello = new JFrame("Hello World");
	        hello.add(new JLabel("Hello World"));
	        hello.pack();
	        hello.setVisible(true);
		
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment()
				.getControllers();
		Controller rcc = null;
		for (int i = 0; i < ca.length; i++) {
			if (ca[i].getName().equals("Rock Candy Gamepad for PS3")) {
				rcc = ca[i];
				Component[] components = rcc.getComponents();
				while (true) {
					rcc.poll();
					String data="";
					for (int j = 0; j < components.length; j++) {
						Component component = components[j];
						
						if (component.isAnalog()) {
							data+=component.getName()+":"+maptoServoRange(component.getPollData());
						}
					}
					System.out.println(data);
					Thread.sleep(500);
				}
			}
		}
	}
	
	public static int maptoServoRange(float x){
		return map(x, -1, 1, 0, 180).intValue();
	}
	
	public static Float map(float x, float in_min, float in_max, float out_min, float out_max){
	  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
}
