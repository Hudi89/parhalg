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
	private static final long serialVersionUID = 1L;
	
	private JTextField tfAddress = new JTextField(15);
	private JTextField tfPort    = new JTextField(5);

	public ConnectionFrame(final GuiEventListener guiEventListener) {
		setTitle("Connection");
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String address = tfAddress.getText();
					int port = Integer.parseInt(tfPort.getText());
					
					if (!address.isEmpty()) {
						guiEventListener.connectionRequest(address, port);
					}
				} catch (NumberFormatException ex) {}
			}
		});
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.gridx  = 0;
		gbcLabel.gridy  = GridBagConstraints.RELATIVE;
		gbcLabel.insets = new Insets(0, 0, 3, 5);
		gbcLabel.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel("Address:"), gbcLabel);
		panel.add(new JLabel("Port:"),    gbcLabel);
		
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.gridx   = 1;
		gbcTextField.gridy   = GridBagConstraints.RELATIVE;
		gbcTextField.insets = new Insets(0, 0, 3, 0);
		gbcTextField.weightx = 1;
		gbcTextField.fill    = GridBagConstraints.HORIZONTAL;
		panel.add(tfAddress, gbcTextField);
		panel.add(tfPort,    gbcTextField);
		
		GridBagConstraints gbcButton = new GridBagConstraints();
		gbcButton.gridx     = 0;
		gbcButton.gridy     = GridBagConstraints.RELATIVE;
		gbcButton.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(btnConnect, gbcButton);
		
		setContentPane(panel);
		pack();
	}

}
