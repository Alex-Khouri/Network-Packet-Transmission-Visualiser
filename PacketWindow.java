/**
* Compsci 230 (2019 Semester 1) - Assignment 2
* NAME: Alexander Khouri
* STUDENT ID#: 6402238
* UPI: akho225
*/

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PacketWindow extends JFrame {
	File file;
	HashMap<String, int[]> sourceData;
	HashMap<String, int[]> destData;
	String[] sourceIPs; // Sorted by PacketProcessor
	String[] destIPs;
	int xMax; // Used to define x-axis
	int yMax; // Used to define y-axis
	JRadioButton sourceButton = new JRadioButton("Source Hosts");
	JRadioButton destButton = new JRadioButton("Destination Hosts");
	ButtonGroup buttons = new ButtonGroup();
	JComboBox IPBox = new JComboBox();
	JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
	JPanel boxPanel = new JPanel();
	PacketGraph graphPanel;
	JFileChooser fileOpener = new JFileChooser();
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenuItem fileOpen = new JMenuItem("Open trace file");
	JMenuItem fileQuit = new JMenuItem("Quit");

	public PacketWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Network Packet Transmission Visualizer");
		setSize(1000, 500);
		setLayout(null);
		graphPanel = new PacketGraph(getX(), getHeight()/8,
									getWidth(), 4*getHeight()/5);
		setJMenuBar(menuBar);
		add(buttonPanel);
		add(boxPanel);
		add(graphPanel);
		buttons.add(sourceButton);
		buttons.add(destButton);
		sourceButton.setSelected(true);
		buttonPanel.setBounds(0, 0, getWidth()/5, getHeight()/8);
		buttonPanel.add(sourceButton);
		buttonPanel.add(destButton);
		sourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (file != null) {
					IPBox.setModel(new DefaultComboBoxModel(sourceIPs));
				}
			}
		});
		destButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (file != null) {
					IPBox.setModel(new DefaultComboBoxModel(destIPs));
				}
			}
		});
		boxPanel.setBounds(getWidth()/5, 0, 4*getWidth()/5, getHeight()/8);
		menuBar.add(fileMenu);
		fileMenu.add(fileOpen);
		fileMenu.add(fileQuit);
		fileOpen.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
				int returnVal = fileOpener.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = null;
					sourceData = new HashMap<String, int[]>();
					destData = new HashMap<String, int[]>();
					sourceButton.setSelected(true);
					file = fileOpener.getSelectedFile();
					xMax = PacketProcessor.processFile(file, sourceData, destData);
					sourceIPs = PacketProcessor.getSortedIPs(sourceData.keySet());
					destIPs = PacketProcessor.getSortedIPs(destData.keySet());
					boxPanel.add(IPBox);
					IPBox.setModel(new DefaultComboBoxModel(sourceIPs));
					IPBox.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String IP = (String) IPBox.getSelectedItem();
							int[] data = sourceButton.isSelected() ?
										sourceData.get(IP) : destData.get(IP);
							graphPanel.importData(data, xMax);
							graphPanel.repaint();
						}
					});
				}
			}
		});
		fileQuit.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) { // Resizes panels
				buttonPanel.setBounds(0, 0,
									getWidth()/5, getHeight()/8);
				boxPanel.setBounds(getWidth()/5, 0,
								4*getWidth()/5, getHeight()/8);
				graphPanel.setBounds(0, getHeight()/8,
									getWidth(), 4*getHeight()/5);
				graphPanel.repaint();
			}
		});
		setResizable(true);
		setVisible(true);
	}

   /** 
    * Creates an instance of the frame and runs it on an asynchronous thread.
	*
	* @param  args  Array of strings passed in front of the filename when
	*				the program is run.
	*/
	public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {new PacketWindow();}
		});
	}
}