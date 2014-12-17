package inf.elte.parhalg.clientgui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class FolderFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private DefaultTableModel tableModel;
	private JTable tabFolders;
  private final GuiEventListener listener;

	public FolderFrame(final GuiEventListener guiEventListener) {
		final FolderFrame this_ = this;
		listener = guiEventListener;
    setTitle("Folders");
		
		tableModel = new DefaultTableModel(new String[]{"Mappa", "Utolsó mentés", "Méret", "Állapot"}, 0) {
			private static final long serialVersionUID = 1L;
			
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
		
		JButton btnAdd = new JButton("Add folder");
		btnAdd.addActionListener(new ActionListener() {
		    @Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog(this_) == JFileChooser.APPROVE_OPTION) {
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
	public void addFolder(File path, Date lastSave, int size, String status) {
		tableModel.addRow(new Object[]{path, lastSave, size, status});
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
	
  @Override
  public void windowClosing(WindowEvent e) {
    listener.closeGUI();
  }


}
