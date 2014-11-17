package org.arhub.robots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jssc.SerialPortException;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class GamepadController {

	public GamepadController(ArduinoSerialConnection arduinoSerialConnection) {
		this.arduinoSerialConnection = arduinoSerialConnection;
	}

	private ArduinoSerialConnection arduinoSerialConnection;
	private Controller controller;
	private Map<Component, ComponentConfig> components = new HashMap<Component, GamepadController.ComponentConfig>();
	private boolean running = false;

	public List<String> getAvailableControllers() {
		List<String> controllerNames = new ArrayList<String>();
		for (Controller controller : ControllerEnvironment
				.getDefaultEnvironment().getControllers()) {
			controllerNames.add(controller.getName());
		}
		return controllerNames;
	}

	public void setController(String controllerName) {
		for (Controller controller : ControllerEnvironment
				.getDefaultEnvironment().getControllers()) {
			if (controller.getName().equals(controllerName)) {
				this.controller = controller;
			}
		}
	}

	public List<String> getAvailableComponents() {
		List<String> componentNames = new ArrayList<String>();
		for (Component component : controller.getComponents()) {
			componentNames.add(component.getName());
		}
		return componentNames;
	}

	public void ObserveController(Component gamepadComponent, String name,
			ValueRange range) {

	}

	public void Start() {
		if (running == true)
			return;
		running = true;

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (running) {
					List<ControlState> controlStates = new ArrayList<ControlState>();
					for (Component component : components.keySet()) {
						ComponentConfig config = components.get(component);
						controlStates.add(new ControlState(config.name,
								config.valueRange.GetValue(component
										.getPollData())));
					}
					try {
						arduinoSerialConnection.SendGamepadState(controlStates);
					} catch (SerialPortException e1) {
						running = false;
					}

					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						running = false;
					}

				}

			}
		}).start();
	}

	public void Stop() {
		running = false;
	}

	private class ComponentConfig {
		public String name;
		public ValueRange valueRange;
	}

}
