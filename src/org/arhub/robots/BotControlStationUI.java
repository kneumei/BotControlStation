package org.arhub.robots;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jssc.SerialPortException;

public class BotControlStationUI {

	private GamepadController controller;
	private ArduinoSerialConnection connection;

	public BotControlStationUI(GamepadController controller, ArduinoSerialConnection connection) {
		this.controller = controller;
		this.connection = connection;
	}

	public void Show() {

		JFrame jframe = new JFrame("Robot Control Station");

		JPanel contentPanel = new JPanel();

		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints connectionPaneConstraints = new GridBagConstraints();
		connectionPaneConstraints.gridx = 0;
		connectionPaneConstraints.gridy = 0;
		connectionPaneConstraints.anchor = GridBagConstraints.WEST;
		connectionPaneConstraints.weighty = 0;
		connectionPaneConstraints.weightx = 1;
		connectionPaneConstraints.insets = new Insets(3, 3, 3, 3);
		contentPanel.add(getSerialConnectionPane(this.connection), connectionPaneConstraints);

		GridBagConstraints gamepadPaneConstraints = new GridBagConstraints();
		gamepadPaneConstraints.gridx = 0;
		gamepadPaneConstraints.gridy = 1;
		gamepadPaneConstraints.anchor = GridBagConstraints.WEST;
		gamepadPaneConstraints.weighty = 1;
		gamepadPaneConstraints.weightx = 1;
		gamepadPaneConstraints.fill = GridBagConstraints.BOTH;

		contentPanel.add(getGamepadFrame(this.controller), gamepadPaneConstraints);

		jframe.setContentPane(contentPanel);
		jframe.pack();
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(800, 600);

	}

	private Component getSerialConnectionPane(ArduinoSerialConnection connection2) {

		JPanel panel = new JPanel();

		final JComboBox<String> comList = new JComboBox<String>(connection.GetComPorts().toArray(new String[] {}));
		panel.add(comList);

		final JLabel connectionStatusLabel = new JLabel("Disconnected");

		final JButton connectBtn = new JButton("Connect");
		final JButton disconnectBtn = new JButton("Disconnect");
		disconnectBtn.setEnabled(false);
		connectBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				connect(connectionStatusLabel, disconnectBtn, connectBtn, comList.getItemAt(comList.getSelectedIndex()));
			}
		});
		disconnectBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				disconnect(connectionStatusLabel, disconnectBtn, connectBtn);

			}
		});

		panel.add(connectBtn);
		panel.add(disconnectBtn);
		panel.add(connectionStatusLabel);

		return panel;
	}

	private Component getGamepadFrame(GamepadController controller2) {
		JPanel panel = new JPanel();

		panel.setLayout(new GridBagLayout());

		final JComboBox<GamepadControllerConfiguration> gamePads = new JComboBox<GamepadControllerConfiguration>(
				controller.getAvailableGamepadControllers().toArray(new GamepadControllerConfiguration[] {}));

		gamePads.setMaximumSize(gamePads.getPreferredSize());

		final GamepadControllerConfigurationPane compontentConfigPane = new GamepadControllerConfigurationPane();

		gamePads.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GamepadControllerConfiguration gamepadConfiguration = gamePads.getItemAt(gamePads.getSelectedIndex());
				compontentConfigPane.setGamepadControllerConfiguration(gamepadConfiguration);
				compontentConfigPane.revalidate();
			}
		});

		final JButton enableButton = new JButton("Enable Controller");
		final JButton disableButton = new JButton("Disable Controller");
		disableButton.setEnabled(false);

		enableButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				disableButton.setEnabled(true);
				enableButton.setEnabled(false);
				controller.setControllerConfiguration(compontentConfigPane.getComponentConfiguration());
				controller.Start();

			}
		});

		disableButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				disableButton.setEnabled(false);
				enableButton.setEnabled(true);
				controller.Stop();

			}
		});

		JPanel controlPane = new JPanel();
		controlPane.setLayout(new FlowLayout());
		controlPane.add(gamePads);
		controlPane.add(enableButton);
		controlPane.add(disableButton);

		GridBagConstraints controlPaneConstraints = new GridBagConstraints();
		controlPaneConstraints.gridx = 0;
		controlPaneConstraints.gridy = 0;
		controlPaneConstraints.anchor = GridBagConstraints.WEST;
		controlPaneConstraints.weightx = 0;
		controlPaneConstraints.weighty = 0;
		controlPaneConstraints.insets = new Insets(3, 3, 3, 3);

		panel.add(controlPane, controlPaneConstraints);

		JScrollPane scrollPane = new JScrollPane(compontentConfigPane);
		GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
		scrollPaneConstraints.gridx = 0;
		scrollPaneConstraints.gridy = 1;
		scrollPaneConstraints.anchor = GridBagConstraints.WEST;
		scrollPaneConstraints.weightx = 1;
		scrollPaneConstraints.weighty = 1;
		scrollPaneConstraints.fill = GridBagConstraints.BOTH;

		panel.add(scrollPane, scrollPaneConstraints);
		return panel;
	}

	private void connect(final JLabel connectionStatusLabel, final JButton disconnectBtn, final JButton connectBtn,
			final String comPort) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					updateLabel(connectionStatusLabel, "Connecting...");
					connection.Connect(comPort);
					updateLabel(connectionStatusLabel, "Connected!");
					disconnectBtn.setEnabled(true);
					connectBtn.setEnabled(false);
				} catch (SerialPortException e) {
					e.printStackTrace();
					updateLabel(connectionStatusLabel, "Error!");
				}

			}
		}).start();
	}

	private void disconnect(final JLabel connectionStatusLabel, final JButton disconnectBtn, final JButton connectBtn) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					connection.disconnect();
					updateLabel(connectionStatusLabel, "Disconnected");
					disconnectBtn.setEnabled(false);
					connectBtn.setEnabled(true);
				} catch (SerialPortException e) {
					e.printStackTrace();
					updateLabel(connectionStatusLabel, "Error!");
				}

			}
		}).start();
	}

	private void updateLabel(final JLabel label, final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				label.setText(message);

			}
		});
	}

	private class GamepadControllerConfigurationPane extends JPanel {

		private static final long serialVersionUID = 1L;

		private GamepadControllerConfiguration gamepadConfiguration;
		private List<GamepadControllerComponentConfigurationPane> panes = new ArrayList<>();

		public GamepadControllerConfigurationPane() {
			setLayout(new GridLayout(0, 2));
		}

		public GamepadControllerConfiguration getComponentConfiguration() {

			for (GamepadControllerComponentConfigurationPane pane : panes) {
				pane.updateGamepadControllerComponentConfiguration();
			}

			return gamepadConfiguration;
		}

		public void setGamepadControllerConfiguration(GamepadControllerConfiguration gamepadConfiguration) {
			this.gamepadConfiguration = gamepadConfiguration;

			this.removeAll();

			for (GamepadControllerComponentConfiguration config : this.gamepadConfiguration.getComponentConfiguration()) {
				GamepadControllerComponentConfigurationPane pane = new GamepadControllerComponentConfigurationPane();
				pane.setGamepadControllerComponentConfiguration(config);
				this.add(pane);
				panes.add(pane);
			}

		}
	}

	private class GamepadControllerComponentConfigurationPane extends JPanel {
		private static final long serialVersionUID = 1L;

		private GamepadControllerComponentConfiguration config;
		JCheckBox checkBox;
		JTextField aliasBox;

		public GamepadControllerComponentConfiguration updateGamepadControllerComponentConfiguration() {
			config.setAlias(aliasBox.getText());
			config.setActive(checkBox.isSelected());
			return config;
		}

		public void setGamepadControllerComponentConfiguration(GamepadControllerComponentConfiguration config) {
			this.config = config;

			JPanel pane = new JPanel();
			pane.setAlignmentY(Component.LEFT_ALIGNMENT);

			this.checkBox = new JCheckBox(config.getName());
			this.aliasBox = new JTextField(config.getAlias());
			aliasBox.setPreferredSize(new Dimension(120, aliasBox.getPreferredSize().height));

			pane.add(checkBox);
			pane.add(aliasBox);
			add(pane);
		}

	}

}
