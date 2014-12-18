package inf.elte.parhalg.clientgui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectionFrame extends JFrame {

	private static final long serialVersionUID = 6543104953293935080L;

	private final JTextField tfAddress = new JTextField(15);

	private final JTextField tfPort = new JTextField(5);

	private final JButton btnConnect = new JButton("Connect");

	public ConnectionFrame(final GuiEventListener guiEventListener) {
		setTitle("Connection");

		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String address = tfAddress.getText();
					int port = Integer.parseInt(tfPort.getText());

					if (!address.isEmpty()) {
						guiEventListener.connectionRequest(address, port);
					}
				} catch (NumberFormatException ex) {
				}
			}
		});

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints gbcAddressLabel = new GridBagConstraints();
		gbcAddressLabel.gridx = 0;
		gbcAddressLabel.gridy = GridBagConstraints.RELATIVE;
		gbcAddressLabel.insets = new Insets(0, 0, 3, 5);
		gbcAddressLabel.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel("Address:"), gbcAddressLabel);

		GridBagConstraints gbcPortLabel = new GridBagConstraints();
		gbcPortLabel.gridx = 0;
		gbcPortLabel.gridy = GridBagConstraints.RELATIVE;
		gbcPortLabel.insets = new Insets(0, 0, 3, 5);
		gbcPortLabel.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel("Port:"), gbcPortLabel);

		GridBagConstraints gbcAddressTextField = new GridBagConstraints();
		gbcAddressTextField.gridx = 1;
		gbcAddressTextField.gridy = GridBagConstraints.RELATIVE;
		gbcAddressTextField.insets = new Insets(0, 0, 3, 0);
		gbcAddressTextField.weightx = 1;
		gbcAddressTextField.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tfAddress, gbcAddressTextField);

		GridBagConstraints gbcPortTextField = new GridBagConstraints();
		gbcPortTextField.gridx = 1;
		gbcPortTextField.gridy = GridBagConstraints.RELATIVE;
		gbcPortTextField.insets = new Insets(0, 0, 3, 0);
		gbcPortTextField.weightx = 1;
		gbcPortTextField.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tfPort, gbcPortTextField);

		GridBagConstraints gbcConnectButton = new GridBagConstraints();
		gbcConnectButton.gridx = 0;
		gbcConnectButton.gridy = GridBagConstraints.RELATIVE;
		gbcConnectButton.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(btnConnect, gbcConnectButton);

		setContentPane(panel);
		pack();
	}

}
