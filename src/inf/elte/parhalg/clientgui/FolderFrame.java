package inf.elte.parhalg.clientgui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;

public class FolderFrame extends JFrame {

	private static final long serialVersionUID = -6059893989339625401L;

	private final DefaultTableModel tableModel;

	private final JTable tabFolders;

	private final GuiEventListener listener;

	public FolderFrame(final GuiEventListener guiEventListener) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Folders");
		
		listener = guiEventListener;
		tableModel = new DefaultTableModel(new String[] { "Mappa", "Utolsó mentés", "Méret", "Állapot" }, 0) {
			private static final long serialVersionUID = -1240735059148101121L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tabFolders = new JTable(tableModel);
		tabFolders.getColumnModel().getColumn(0).setPreferredWidth(200);
		tabFolders.getColumnModel().getColumn(1).setPreferredWidth(250);
		tabFolders.getColumnModel().getColumn(2).setPreferredWidth(60);
		tabFolders.getColumnModel().getColumn(3).setPreferredWidth(60);
		
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				listener.closeGUI();
			}

			public void windowOpened(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowActivated(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}
		});

		JButton btnAdd = new JButton("Add folder");
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(FolderFrame.this) == JFileChooser.APPROVE_OPTION) {
					File directory = fc.getSelectedFile();
					guiEventListener.addDirectoryRequest(directory);
				}
			}
		});

		JPanel panel = new JPanel(new BorderLayout(3, 3));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		panel.add(new JScrollPane(tabFolders), BorderLayout.CENTER);
		panel.add(btnAdd, BorderLayout.SOUTH);

		setContentPane(panel);
		pack();
	}

	// Hozzáad egy sort a táblázathoz.
	public void addFolder(File path, String status) {
		long size = FileUtils.sizeOfDirectory(path);
		String readableSize = FileUtils.byteCountToDisplaySize(size);
		tableModel.addRow(new Object[] { path, "never", readableSize, status });
	}

	// Frissíti a `path` sorát a táblázatban a többi értékkel,
	// ha az adott érték nem null.
	public void updateFolder(File path, Date lastSave, Integer size, String status) {
		for (int i = 0; i < tableModel.getRowCount(); ++i) {
			File rowFolder = (File) tableModel.getValueAt(i, 0);
			if (rowFolder.equals(path)) {
				if (lastSave != null) {
					tableModel.setValueAt(lastSave, i, 1);
				}
				if (size != null) {
					tableModel.setValueAt(size, i, 2);
				}
				if (status != null) {
					tableModel.setValueAt(status, i, 3);
				}
				break;
			}
		}
	}
}
