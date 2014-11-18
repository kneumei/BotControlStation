package org.arhub.robots;

import net.java.games.input.Component.Identifier;

public class GamepadControllerComponentConfiguration {
	private String name;
	private String alias;
	private boolean isAnalog;
	private ValueRange valueRange;
	private Identifier identifier;
	private boolean isActive = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isAnalog() {
		return isAnalog;
	}

	public void setAnalog(boolean isAnalog) {
		this.isAnalog = isAnalog;
		if (isAnalog()) {
			this.valueRange = new ServoValueRange();
		} else {
			this.valueRange = new BinaryValueRange();
		}
	}

	public ValueRange getValueRange() {
		return valueRange;
	}

	public void setValueRange(ValueRange valueRange) {
		this.valueRange = valueRange;
	}

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
