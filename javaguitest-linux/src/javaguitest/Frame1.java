package javaguitest;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.JEditorPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.net.Socket;
import java.io.PrintWriter;

import nesslab.reader.api.*;
import nesslab.reader.api.ReaderApi.CloseType;
import nesslab.reader.api.ReaderApi.IEventHandler;
import nesslab.reader.api.ReaderApi.ReadType;
import nesslab.reader.api.ReaderApi.ReaderEventArgs;
import nesslab.reader.api.ReaderApi.TagType;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;

//import nesslab.reader.api.ReaderApi.VisitedEventArgs;
//import nesslab.reader.api.ReaderApi.MyEventListener;
//import nesslab.reader.*;

public class Frame1 {

	private JFrame frmTest;
	private JTextField IP;
	private JTextField rw_tagid_textField;
	private JTextField rw_password_textField;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private final Action action = new SwingAction();

	//connection
	JButton btnConnetion;
	JButton btnClose;
	JButton btnsocket;
	
	
	JPanel ver_panel;	
	JPanel configuration_panel;
	JPanel inventory_panel;
	JPanel read_write_panel;
	JPanel lock_kill_panel;
	JPanel select_panel;
	
	
	JTabbedPane tabbedPane;
	
	//version
	JLabel lblReader;	
	JLabel lblHardware;
	JLabel lblroot;	
	
	//antenna state
	JSpinner power;
	JCheckBox chckbxAntena;
	JCheckBox chckbxAntena_1;
	JCheckBox chckbxAntena_2;
	JCheckBox chckbxAntena_3;
	JButton config_btnRead;
	JButton config_btnWrite;
	
	//Inventory
	DefaultTableModel TagIDtable;
	
	//read/write
	JComboBox rw_bank_comboBox;
	JSpinner rw_location_spinner;	
	JSpinner rw_length_spinner;
	JTextArea rw_txtarea;
	JScrollPane rw_scrollPane;
	
	//lock/kill
	JComboBox lk_kill_pw_comboBox;
	JComboBox lk_access_pw_comboBox;
	JComboBox lk_epc_bank_comboBox;
	JComboBox lk_tid_bank_comboBox;
	JComboBox lk_user_bank_comboBox;
	JEditorPane lk_pw_editorPane;
	JTextArea lk_txtarea;
	JScrollPane lk_scrollPane;
	
	JLabel lblstatusLabel;
	
	
	String Readerver = "";
	String Hardwarever = "";
	String rootver = "";
	
	double testcount = 0;
	
	ReaderApi Reader  = new  ReaderApi();
	//ReaderApi Reader2 = new  ReaderApi();
	
	ReaderEventHandler readerhandler;
	//ReaderEventHandler2 readerhandler2;
	
	private JTable table;
	
    private enum InventoryAction
    {
        Start,
        Stop
    }
    
	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame1 window = new Frame1();					
					window.frmTest.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}



	/**
	 * Create the application.
	 */
		
	public Frame1() {
		
		initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTest = new JFrame();
		frmTest.setResizable(false);
		frmTest.setTitle("Reader Manager v0.0.0");
		frmTest.setBounds(100, 100, 570, 468);
		frmTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTest.getContentPane().setLayout(null);
		
		btnConnetion = new JButton("Connect");
		btnConnetion.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {	
				
				String connect = btnConnetion.getText();			 
				
				if(connect.equals("Connect"))
				{
					System.out.println("GUI.connect");
					
					readerhandler = new ReaderEventHandler(Reader);
					//readerhandler2 = new ReaderEventHandler2(Reader2);
					
					Reader.ConnectSocket(IP.getText(), 5578);
					//Reader2.ConnectSocket("192.168.10.95", 5578);
			

				}
				else
				{
					System.out.println("GUI.disconnect");
					
					Reader.Close(CloseType.Close);	
					//Reader2.Close(CloseType.Close);
					
					Reader.Visited.removeEventHandler(readerhandler);	
					//Reader2.Visited.removeEventHandler(readerhandler2);	
				}

				
			}
		});
		btnConnetion.setBounds(352, 13, 92, 23);
		frmTest.getContentPane().add(btnConnetion);

		btnsocket = new JButton("t");
		btnsocket.setBounds(310,13,40,23);
		frmTest.getContentPane().add(btnsocket);


		btnsocket.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){	
				
				try{
				System.out.println("socket");
				Socket sendsocket = new Socket("127.0.0.1",5001);

				if(sendsocket.isConnected()){
					PrintWriter writer = new PrintWriter(sendsocket.getOutputStream());
					writer.println("hihihi");
					writer.flush();

				}else{}
				
			}catch(Exception e){
				System.out.println("Error");
			}
				

				
			}
		});
		
		btnClose = new JButton("Close");
		btnClose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				System.exit(0);
			}
		});
		btnClose.setBounds(456, 13, 92, 23);
		frmTest.getContentPane().add(btnClose);
		
		JLabel lblIPAdressLabel = new JLabel("IP Adress");
		lblIPAdressLabel.setBounds(104, 10, 67, 29);
		frmTest.getContentPane().add(lblIPAdressLabel);
		
		IP = new JTextField("192.168.10.220");
		IP.setBounds(183, 14, 121, 21);
		frmTest.getContentPane().add(IP);
		IP.setColumns(10);
		
		JLabel lblTCPIPLabel = new JLabel("TCP/IP");
		lblTCPIPLabel.setBounds(28, 10, 52, 29);
		frmTest.getContentPane().add(lblTCPIPLabel);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setEnabled(false);
		tabbedPane.setBounds(12, 54, 536, 354);
		frmTest.getContentPane().add(tabbedPane);
		
		JPanel configuration_panel = new JPanel();
		tabbedPane.addTab("Configuration", null, configuration_panel, null);
		
		
		//JButton btnRead = new JButton("Read");
		config_btnRead = new JButton("Read");
		config_btnRead.setEnabled(false);
		config_btnRead.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				GetConfiguration();
			}
		});
		
		config_btnRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
			}
		});
		
		//JButton btnWrite = new JButton("Write");
		config_btnWrite = new JButton("Write");
		config_btnWrite.setEnabled(false);
		config_btnWrite.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SetConfiguration();				
			}
		});
		config_btnWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
				
		ver_panel = new JPanel();	
		
		JPanel ant_power_panel = new JPanel();
		GroupLayout gl_configuration_panel = new GroupLayout(configuration_panel);
		gl_configuration_panel.setHorizontalGroup(
			gl_configuration_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_configuration_panel.createSequentialGroup()
					.addGroup(gl_configuration_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_configuration_panel.createSequentialGroup()
							.addGap(27)
							.addGroup(gl_configuration_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(ant_power_panel, GroupLayout.PREFERRED_SIZE, 468, GroupLayout.PREFERRED_SIZE)
								.addComponent(ver_panel, GroupLayout.PREFERRED_SIZE, 468, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_configuration_panel.createSequentialGroup()
							.addGap(166)
							.addComponent(config_btnRead, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(config_btnWrite, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(36, Short.MAX_VALUE))
		);
		gl_configuration_panel.setVerticalGroup(
			gl_configuration_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_configuration_panel.createSequentialGroup()
					.addGap(25)
					.addComponent(ver_panel, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(ant_power_panel, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
					.addGap(30)
					.addGroup(gl_configuration_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(config_btnRead)
						.addComponent(config_btnWrite))
					.addContainerGap(113, Short.MAX_VALUE))
		);
		ver_panel.setLayout(null);
		
		//JLabel lblReader = new JLabel("* Reader : ");
		lblReader = new JLabel("* Reader : ");
		lblReader.setBounds(12, 10, 157, 53);
		lblReader.setEnabled(false);
		ver_panel.add(lblReader);
		
		//JLabel lblHardware = new JLabel("* Hardware :");
		lblHardware = new JLabel("* Hardware :");
		lblHardware.setBounds(153, 10, 157, 53);
		lblHardware.setEnabled(false);
		ver_panel.add(lblHardware);
		
		//JLabel lblroot = new JLabel("*Root :");
		lblroot = new JLabel("*Root :");
		lblroot.setBounds(311, 10, 157, 53);
		lblroot.setEnabled(false);
		ver_panel.add(lblroot);
		ant_power_panel.setLayout(null);
		
		//JSpinner power = new JSpinner();
		power = new JSpinner();
		power.setModel(new SpinnerNumberModel(new Integer(300), null, null, new Integer(1)));
		power.setBounds(372, 19, 65, 22);
		power.setEnabled(false);
		ant_power_panel.add(power);
		
		//JCheckBox chckbxAntena = new JCheckBox("Antena 1");
		chckbxAntena = new JCheckBox("Antena 1");
		chckbxAntena.setBounds(8, 18, 75, 23);
		chckbxAntena.setEnabled(false);
		ant_power_panel.add(chckbxAntena);
		
		//JCheckBox chckbxAntena_1 = new JCheckBox("Antena 2");
		chckbxAntena_1 = new JCheckBox("Antena 2");
		chckbxAntena_1.setBounds(87, 18, 75, 23);
		chckbxAntena_1.setEnabled(false);
		ant_power_panel.add(chckbxAntena_1);
		
		//JCheckBox chckbxAntena_2 = new JCheckBox("Antena 3");
		chckbxAntena_2 = new JCheckBox("Antena 3");
		chckbxAntena_2.setBounds(166, 18, 75, 23);
		chckbxAntena_2.setEnabled(false);
		ant_power_panel.add(chckbxAntena_2);
		
		//JCheckBox chckbxAntena_3 = new JCheckBox("Antena 4");
		chckbxAntena_3 = new JCheckBox("Antena 4");
		chckbxAntena_3.setBounds(245, 18, 87, 23);
		chckbxAntena_3.setEnabled(false);
		ant_power_panel.add(chckbxAntena_3);
		configuration_panel.setLayout(gl_configuration_panel);
		
		inventory_panel = new JPanel();
		tabbedPane.addTab("Inventory", null, inventory_panel, null);
		inventory_panel.setLayout(null);
		
		JButton btnNormalInventory = new JButton("Normal Inventory");
		btnNormalInventory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {	
				testcount = 0;
				Inventory(InventoryAction.Start);
			}
		});
		btnNormalInventory.setBounds(10, 10, 129, 23);
		inventory_panel.add(btnNormalInventory);
		
		JButton btnSelectInventory = new JButton("Select Inventory");
		btnSelectInventory.setBounds(151, 10, 129, 23);
		inventory_panel.add(btnSelectInventory);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Inventory(InventoryAction.Stop);
			}
		});
		btnStop.setBounds(388, 10, 67, 23);
		inventory_panel.add(btnStop);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				testcount = 0;
				table.setModel(new DefaultTableModel(
						new Object[][] {
							
						},
						new String[] {
								"Tag ID (HEX)", "TEXT"
							}
						));
  				//table.updateUI();
			}
		});
		btnClear.setBounds(456, 10, 67, 23);
		inventory_panel.add(btnClear);
		
		JPanel panel_10 = new JPanel();
		panel_10.setBounds(10, 44, 509, 283);
		inventory_panel.add(panel_10);
		panel_10.setLayout(null);
		
		table = new JTable();
		table.setBackground(Color.WHITE);
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Tag ID (HEX)", "TEXT"
			}
		));
		table.getColumnModel().getColumn(0).setPreferredWidth(289);
		table.getColumnModel().getColumn(1).setPreferredWidth(179);
		table.setBounds(12, 268, 485, -253);
		//panel_10.add(table);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(12, 10, 485, 263);
		panel_10.add(scrollPane);
		
		//read/write
		read_write_panel = new JPanel();
		tabbedPane.addTab("Read/Write", null, read_write_panel, null);
		read_write_panel.setLayout(null);
		
		JLabel lblBank = new JLabel("Bank :");
		lblBank.setBounds(62, 10, 100, 15);
		read_write_panel.add(lblBank);
		
		JLabel lblLocation = new JLabel("Location :");
		lblLocation.setBounds(62, 35, 100, 15);
		read_write_panel.add(lblLocation);
		
		JLabel lblLength = new JLabel("Length :");
		lblLength.setBounds(62, 60, 100, 15);
		read_write_panel.add(lblLength);
		
		JLabel lblTagIdhex = new JLabel("TAG ID (HEX) :");
		lblTagIdhex.setBounds(62, 85, 100, 15);
		read_write_panel.add(lblTagIdhex);
		
		JLabel lblPassword = new JLabel("Password :");
		lblPassword.setBounds(62, 106, 100, 15);
		read_write_panel.add(lblPassword);
		
		//JComboBox rw_bank_comboBox = new JComboBox();
		rw_bank_comboBox = new JComboBox();
		rw_bank_comboBox.setModel(new DefaultComboBoxModel(new String[] {"RESERVED", "EPC", "TID", "USER"}));
		rw_bank_comboBox.setBounds(174, 7, 126, 21);
		rw_bank_comboBox.setSelectedIndex(1);
		read_write_panel.add(rw_bank_comboBox);
		
		rw_location_spinner = new JSpinner();
		rw_location_spinner.setModel(new SpinnerNumberModel(new Integer(2), null, null, new Integer(1)));
		rw_location_spinner.setBounds(174, 32, 60, 22);
		read_write_panel.add(rw_location_spinner);
		
		rw_length_spinner = new JSpinner();
		rw_length_spinner.setModel(new SpinnerNumberModel(new Integer(6), null, null, new Integer(1)));
		rw_length_spinner.setBounds(174, 57, 60, 22);
		read_write_panel.add(rw_length_spinner);
		
		rw_tagid_textField = new JTextField();
		rw_tagid_textField.setBounds(174, 82, 254, 21);
		read_write_panel.add(rw_tagid_textField);
		rw_tagid_textField.setColumns(10);
		
		rw_password_textField = new JTextField();
		rw_password_textField.setBounds(174, 106, 116, 21);
		read_write_panel.add(rw_password_textField);
		rw_password_textField.setColumns(10);
		
		JLabel lblunitWord = new JLabel("(Unit : Word)");
		lblunitWord.setBounds(240, 34, 71, 21);
		read_write_panel.add(lblunitWord);
		
		JLabel label = new JLabel("(Unit : Word)");
		label.setBounds(240, 60, 71, 21);
		read_write_panel.add(label);
		
		JLabel lblex = new JLabel("(ex : 12345678)");
		lblex.setBounds(300, 106, 126, 21);
		read_write_panel.add(lblex);
		
		JButton rw_btnRead = new JButton("Read");
		rw_btnRead.setBounds(157, 141, 77, 23);
		rw_btnRead.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {				
	            int bank = rw_bank_comboBox.getSelectedIndex();
	            int location = (Integer)rw_location_spinner.getValue();
	            int length = (Integer)rw_length_spinner.getValue();
	            String password = rw_password_textField.getText();

	            System.out.println("bank = " + bank);
	            System.out.println("location = " + location);
	            System.out.println("length = " + length);
	            System.out.println("password = " + password);
	            
	            GetTagMemory(bank, location, length, password);
			}
		});
		read_write_panel.add(rw_btnRead);
		
		JButton rw_btnWrite = new JButton("Write");
		rw_btnWrite.setBounds(240, 141, 77, 23);
		rw_btnWrite.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {				
	            int bank =  rw_bank_comboBox.getSelectedIndex();
	            int location = (Integer)rw_location_spinner.getValue();
	            int length = (Integer)rw_length_spinner.getValue();
	            String data = rw_tagid_textField.getText();
	            String password = rw_password_textField.getText();

	            System.out.println("bank = " + bank);
	            System.out.println("location = " + location);
	            System.out.println("length = " + length);
	            System.out.println("data = " + data);
	            System.out.println("password = " + password);
	            SetTagMemory(bank, location, length, data, password);
			}
		});
		read_write_panel.add(rw_btnWrite);
		
		JLabel lblResponse = new JLabel("Response :");
		lblResponse.setBounds(12, 185, 77, 23);
		read_write_panel.add(lblResponse);
		
		JButton rw_btnClear = new JButton("Clear");
		rw_btnClear.setBounds(12, 268, 71, 21);
		rw_btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {		
				rw_txtarea.setText(null);
			}
		});
		read_write_panel.add(rw_btnClear);
		
		rw_txtarea = new JTextArea();
		read_write_panel.add(rw_txtarea);
		rw_txtarea.setRows(5);
		rw_txtarea.setLineWrap(true);
		rw_txtarea.setBounds(87, 185, 391, 104);
		rw_scrollPane = new JScrollPane(rw_txtarea);
		rw_scrollPane.setSize(391, 104);
		rw_scrollPane.setLocation(101, 184);
		//read_write_panel.add(txtrLll);
		read_write_panel.add(rw_scrollPane);
		
		//lock/kill
		lock_kill_panel = new JPanel();
		tabbedPane.addTab("Lock/Kill", null, lock_kill_panel, null);
		lock_kill_panel.setLayout(null);
		
		JLabel lblKillPassword = new JLabel("Kill Password");
		lblKillPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblKillPassword.setBounds(27, 10, 119, 25);
		lock_kill_panel.add(lblKillPassword);
		
		JLabel lblAccessPassword = new JLabel("Access Password");
		lblAccessPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAccessPassword.setBounds(27, 34, 119, 25);
		lock_kill_panel.add(lblAccessPassword);
		
		JLabel lblEpcBank = new JLabel("EPC Bank");
		lblEpcBank.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEpcBank.setBounds(27, 57, 119, 25);
		lock_kill_panel.add(lblEpcBank);
		
		JLabel lblTidBank = new JLabel("TID Bank");
		lblTidBank.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTidBank.setBounds(27, 80, 119, 25);
		lock_kill_panel.add(lblTidBank);
		
		JLabel lblUserBank = new JLabel("User Bank");
		lblUserBank.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUserBank.setBounds(27, 103, 119, 25);
		lock_kill_panel.add(lblUserBank);
		
		lk_kill_pw_comboBox = new JComboBox();
		lk_kill_pw_comboBox.setModel(new DefaultComboBoxModel(new String[] {"ACCESSIBLE", "ALWAYS ACCESSIBLE", "SECURED ACCESSIBLE", "ALWAYS NOT ACCESSIBLE", "NO CANGE"}));
		lk_kill_pw_comboBox.setBounds(168, 12, 198, 21);
		lk_kill_pw_comboBox.setSelectedIndex(4);
		lock_kill_panel.add(lk_kill_pw_comboBox);
		
		lk_access_pw_comboBox = new JComboBox();
		lk_access_pw_comboBox.setModel(new DefaultComboBoxModel(new String[] {"ACCESSIBLE", "ALWAYS ACCESSIBLE", "SECURED ACCESSIBLE", "ALWAYS NOT ACCESSIBLE", "NO CANGE"}));
		lk_access_pw_comboBox.setBounds(168, 36, 198, 21);
		lk_access_pw_comboBox.setSelectedIndex(4);
		lock_kill_panel.add(lk_access_pw_comboBox);
		
		lk_epc_bank_comboBox = new JComboBox();
		lk_epc_bank_comboBox.setModel(new DefaultComboBoxModel(new String[] {"ACCESSIBLE", "ALWAYS ACCESSIBLE", "SECURED ACCESSIBLE", "ALWAYS NOT ACCESSIBLE", "NO CANGE"}));
		lk_epc_bank_comboBox.setBounds(168, 59, 198, 21);
		lk_epc_bank_comboBox.setSelectedIndex(4);
		lock_kill_panel.add(lk_epc_bank_comboBox);
		
		lk_tid_bank_comboBox = new JComboBox();
		lk_tid_bank_comboBox.setModel(new DefaultComboBoxModel(new String[] {"ACCESSIBLE", "ALWAYS ACCESSIBLE", "SECURED ACCESSIBLE", "ALWAYS NOT ACCESSIBLE", "NO CANGE"}));
		lk_tid_bank_comboBox.setBounds(168, 82, 198, 21);
		lk_tid_bank_comboBox.setSelectedIndex(4);
		lock_kill_panel.add(lk_tid_bank_comboBox);
		
		lk_user_bank_comboBox = new JComboBox();
		lk_user_bank_comboBox.setModel(new DefaultComboBoxModel(new String[] {"ACCESSIBLE", "ALWAYS ACCESSIBLE", "SECURED ACCESSIBLE", "ALWAYS NOT ACCESSIBLE", "NO CANGE"}));
		lk_user_bank_comboBox.setBounds(168, 105, 198, 21);
		lk_user_bank_comboBox.setSelectedIndex(4);
		lock_kill_panel.add(lk_user_bank_comboBox);
		
		JLabel lblPassword_1 = new JLabel("Password");
		lblPassword_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword_1.setBounds(83, 132, 63, 25);
		lock_kill_panel.add(lblPassword_1);
		
		lk_pw_editorPane = new JEditorPane();
		lk_pw_editorPane.setBounds(168, 132, 112, 21);
		lock_kill_panel.add(lk_pw_editorPane);
		
		JLabel lblex_1 = new JLabel("(ex : 12345678)");
		lblex_1.setBounds(290, 132, 84, 25);
		lock_kill_panel.add(lblex_1);
		
		JButton lk_btnLock = new JButton("Lock");
		lk_btnLock.setBounds(168, 167, 97, 25);
		lk_btnLock.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
	            //String message = "��� ? ";

				
	            //if (MessageBox.Show(message, "ggg ", MessageBoxButtons.YesNo, MessageBoxIcon.Information, MessageBoxDefaultButton.Button2) == DialogResult.Yes)
	            {
	            	
	            	
	                String killpermissions = lk_kill_pw_comboBox.getSelectedItem().toString();
	                String accesspermissions = lk_access_pw_comboBox.getSelectedItem().toString();
	                String epcpermissions = lk_epc_bank_comboBox.getSelectedItem().toString();
	                String tidpermissions = lk_tid_bank_comboBox.getSelectedItem().toString();
	                String userpermissions = lk_user_bank_comboBox.getSelectedItem().toString();
	                String password = lk_pw_editorPane.getText().toUpperCase();
	                
	                System.out.println("killpermissions = " + killpermissions);
	                System.out.println("accesspermissions = " + accesspermissions);
	                System.out.println("epcpermissions = " + epcpermissions);
	                System.out.println("tidpermissions = " + tidpermissions);
	                System.out.println("userpermissions = " + userpermissions);
	                System.out.println("password = " + password);
	   
	                LockTag(killpermissions, accesspermissions, epcpermissions, tidpermissions, userpermissions, password);
	            }
			}
		});
		lock_kill_panel.add(lk_btnLock);
		
		JButton lk_btnKill = new JButton("Kill");
		lk_btnKill.setBounds(269, 167, 97, 25);
		lk_btnKill.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
	            String message = "dngkgkgkgkgkgg";

	           // if (MessageBox.Show(message, "dngkgkgkg", MessageBoxButtons.YesNo, MessageBoxIcon.Information, MessageBoxDefaultButton.Button2) == DialogResult.Yes)
	            {
	                String password = lk_pw_editorPane.getText().toUpperCase();
	                System.out.println("password = " + password);
	                KillTag(password);
	            }
			}
		});
		lock_kill_panel.add(lk_btnKill);
		
		JLabel lblResponse_1 = new JLabel("Response :");
		lblResponse_1.setBounds(12, 229, 72, 25);
		lock_kill_panel.add(lblResponse_1);
		
		JButton lk_btnClear = new JButton("Clear");
		lk_btnClear.setBounds(12, 302, 72, 25);
		lk_btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				lk_txtarea.setText(null);
			}
		});
		lock_kill_panel.add(lk_btnClear);
		

		
		JLabel lblreadWrite = new JLabel("(Read / Write)");
		lblreadWrite.setBounds(390, 10, 84, 25);
		lock_kill_panel.add(lblreadWrite);
		
		JLabel label_1 = new JLabel("(Read / Write)");
		label_1.setBounds(390, 34, 84, 25);
		lock_kill_panel.add(label_1);
		
		JLabel lblwrite = new JLabel("(Write)");
		lblwrite.setBounds(390, 57, 84, 25);
		lock_kill_panel.add(lblwrite);
		
		JLabel lblwrite_1 = new JLabel("(Write)");
		lblwrite_1.setBounds(390, 80, 84, 25);
		lock_kill_panel.add(lblwrite_1);
		
		JLabel lblwrite_2 = new JLabel("(Write)");
		lblwrite_2.setBounds(390, 103, 84, 25);
		lock_kill_panel.add(lblwrite_2);
		
		
		lk_txtarea = new JTextArea();
		read_write_panel.add(lk_txtarea);
		lk_txtarea.setRows(5);
		lk_txtarea.setLineWrap(true);
		lk_txtarea.setBounds(96, 229, 423, 98);
		lk_scrollPane = new JScrollPane(lk_txtarea);
		lk_scrollPane.setSize(391, 104);
		lk_scrollPane.setLocation(101, 223);
		//read_write_panel.add(txtrLll);
		lock_kill_panel.add(lk_scrollPane);
		
		
		
		
		//JTextArea lk_textArea = new JTextArea();
		//lk_textArea.setBounds(96, 229, 423, 98);
		//lock_kill_panel.add(lk_textArea);
		
		
		// select
		select_panel = new JPanel();
		tabbedPane.addTab("Select", null, select_panel, null);
		select_panel.setLayout(null);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBounds(12, 10, 507, 114);
		select_panel.add(panel_7);
		panel_7.setLayout(null);
		
		JLabel lblMemoryBank = new JLabel("Memory bank");
		lblMemoryBank.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMemoryBank.setBounds(12, 10, 99, 15);
		panel_7.add(lblMemoryBank);
		
		JLabel lblOffset = new JLabel("Offset");
		lblOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOffset.setBounds(12, 35, 99, 15);
		panel_7.add(lblOffset);
		
		JLabel lblCount = new JLabel("Count");
		lblCount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCount.setBounds(12, 60, 99, 15);
		panel_7.add(lblCount);
		
		JLabel lblMask = new JLabel("Mask   0x");
		lblMask.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMask.setBounds(37, 88, 99, 15);
		panel_7.add(lblMask);
		
		JComboBox comboBox_6 = new JComboBox();
		comboBox_6.setModel(new DefaultComboBoxModel(new String[] {"RESERVED", "EPC", "TID", "USER"}));
		comboBox_6.setBounds(138, 10, 140, 21);
		panel_7.add(comboBox_6);
		
		JSpinner spinner_2 = new JSpinner();
		spinner_2.setBounds(138, 35, 76, 22);
		panel_7.add(spinner_2);
		
		JSpinner spinner_3 = new JSpinner();
		spinner_3.setBounds(138, 60, 76, 22);
		panel_7.add(spinner_3);
		
		textField_3 = new JTextField();
		textField_3.setText("\r\n");
		textField_3.setBounds(170, 85, 35, 21);
		panel_7.add(textField_3);
		textField_3.setColumns(10);
		
		JLabel lblbyte = new JLabel("(byte)");
		lblbyte.setBounds(421, 88, 57, 15);
		panel_7.add(lblbyte);
		
		JLabel lblunitBit = new JLabel("(unit : bit)");
		lblunitBit.setBounds(221, 41, 57, 15);
		panel_7.add(lblunitBit);
		
		JLabel label_2 = new JLabel("(unit : bit)");
		label_2.setBounds(221, 63, 57, 15);
		panel_7.add(label_2);
		
		textField_4 = new JTextField();
		textField_4.setText("\r\n");
		textField_4.setColumns(10);
		textField_4.setBounds(138, 85, 35, 21);
		panel_7.add(textField_4);
		
		textField_5 = new JTextField();
		textField_5.setText("\r\n");
		textField_5.setColumns(10);
		textField_5.setBounds(238, 85, 35, 21);
		panel_7.add(textField_5);
		
		textField_6 = new JTextField();
		textField_6.setText("\r\n");
		textField_6.setColumns(10);
		textField_6.setBounds(204, 85, 35, 21);
		panel_7.add(textField_6);
		
		textField_7 = new JTextField();
		textField_7.setText("\r\n");
		textField_7.setColumns(10);
		textField_7.setBounds(272, 85, 35, 21);
		panel_7.add(textField_7);
		
		textField_8 = new JTextField();
		textField_8.setText("\r\n");
		textField_8.setColumns(10);
		textField_8.setBounds(306, 85, 35, 21);
		panel_7.add(textField_8);
		
		textField_9 = new JTextField();
		textField_9.setText("\r\n");
		textField_9.setColumns(10);
		textField_9.setBounds(340, 85, 35, 21);
		panel_7.add(textField_9);
		
		textField_10 = new JTextField();
		textField_10.setText("\r\n");
		textField_10.setColumns(10);
		textField_10.setBounds(374, 85, 35, 21);
		panel_7.add(textField_10);
		
		JPanel panel_8 = new JPanel();
		panel_8.setBounds(12, 134, 507, 92);
		select_panel.add(panel_8);
		panel_8.setLayout(null);
		
		JLabel lblTarget = new JLabel("Target");
		lblTarget.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTarget.setBounds(57, 13, 57, 15);
		panel_8.add(lblTarget);
		
		JLabel lblAction = new JLabel("Action");
		lblAction.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAction.setBounds(57, 40, 57, 15);
		panel_8.add(lblAction);
		
		JLabel lblTruncate = new JLabel("Truncate");
		lblTruncate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTruncate.setBounds(57, 65, 57, 15);
		panel_8.add(lblTruncate);
		
		JComboBox comboBox_7 = new JComboBox();
		comboBox_7.setModel(new DefaultComboBoxModel(new String[] {"INVENTORY_S0", "INVENTORY_S1", "INVENTORY_S2", "INVENTORY_S3", "SELECTED"}));
		comboBox_7.setBounds(126, 10, 146, 21);
		panel_8.add(comboBox_7);
		
		JComboBox comboBox_8 = new JComboBox();
		comboBox_8.setModel(new DefaultComboBoxModel(new String[] {"ASLINVA_DSLINVB", "ASLINVA_NOTHING", "NOTING_DSLINVB", "NSLINVS_NOTHING", "DSLINVB_ASLINVA", "DSLINVB_NOTHING", "NOTING_ASLINVA", "NOTING_NSLINVS"}));
		comboBox_8.setBounds(126, 37, 146, 21);
		panel_8.add(comboBox_8);
		
		JComboBox comboBox_9 = new JComboBox();
		comboBox_9.setModel(new DefaultComboBoxModel(new String[] {"DISABLE", "ENABLE"}));
		comboBox_9.setBounds(126, 62, 146, 21);
		panel_8.add(comboBox_9);
		
		JPanel panel_9 = new JPanel();
		panel_9.setBounds(12, 236, 360, 91);
		select_panel.add(panel_9);
		panel_9.setLayout(null);
		
		JLabel lblSelect = new JLabel("Select");
		lblSelect.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSelect.setBounds(55, 13, 57, 15);
		panel_9.add(lblSelect);
		
		JLabel lblSession = new JLabel("Session");
		lblSession.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSession.setBounds(55, 38, 57, 15);
		panel_9.add(lblSession);
		
		JLabel lblTarget_1 = new JLabel("Target");
		lblTarget_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTarget_1.setBounds(55, 63, 57, 15);
		panel_9.add(lblTarget_1);
		
		JComboBox comboBox_10 = new JComboBox();
		comboBox_10.setModel(new DefaultComboBoxModel(new String[] {"SELECTED_ALL", "SELECTED_OFF", "SELECTED_ON"}));
		comboBox_10.setBounds(124, 10, 198, 21);
		panel_9.add(comboBox_10);
		
		JComboBox comboBox_11 = new JComboBox();
		comboBox_11.setModel(new DefaultComboBoxModel(new String[] {"INVENTORY_SESSION_S0", "INVENTORY_SESSION_S1", "INVENTORY_SESSION_S2", "INVENTORY_SESSION_S3"}));
		comboBox_11.setBounds(124, 35, 198, 21);
		panel_9.add(comboBox_11);
		
		JComboBox comboBox_12 = new JComboBox();
		comboBox_12.setModel(new DefaultComboBoxModel(new String[] {"INVENTORY_SESSION_TARGET_A", "INVENTORY_SESSION_TARGET_B"}));
		comboBox_12.setBounds(124, 60, 232, 21);
		panel_9.add(comboBox_12);
		
		JButton btnApplySelectMask = new JButton("Apply Select Mask");
		btnApplySelectMask.setBounds(384, 260, 139, 50);
		select_panel.add(btnApplySelectMask);
		
		lblstatusLabel = new JLabel("None");
		lblstatusLabel.setBounds(22, 418, 92, 15);
		frmTest.getContentPane().add(lblstatusLabel);
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	
    public void Inventory(InventoryAction action)
    {
    	System.out.println("GUI_Inventory()");
        if (Reader != null)
        {
            //if (Reader.IsHandling)
            {
                if (action == InventoryAction.Start)      	
                {
                	Reader.ReadTagId(TagType.ISO18000_6C_GEN2, ReadType.MULTI);
                	lblstatusLabel.setText("Run Inventory");                    
                }
                else
                {

                	Reader.StopOperation();
                	lblstatusLabel.setText("Ready");
                	
                    //bool returnvalue = reader.StopOperationToSync();
                    //if (returnvalue)
                    //{
                    //    this.tslblStatus.Text = "Ready";

                    //    Inventory(Action.Start);
                    //}
                    //else
                    //{
                    //    this.tslblStatus.Text = "Stop Failed";
                    //}
                }
            }
        }
        
        /*
        if (Reader2 != null)
        {
            //if (Reader.IsHandling)
            {
                if (action == InventoryAction.Start)      	
                {
                	Reader2.ReadTagId(TagType.ISO18000_6C_GEN2, ReadType.MULTI);
                	lblstatusLabel.setText("Run Inventory");                    
                }
                else
                {

                	Reader2.StopOperation();
                	lblstatusLabel.setText("Ready");
                	
                    //bool returnvalue = reader.StopOperationToSync();
                    //if (returnvalue)
                    //{
                    //    this.tslblStatus.Text = "Ready";

                    //    Inventory(Action.Start);
                    //}
                    //else
                    //{
                    //    this.tslblStatus.Text = "Stop Failed";
                    //}
                }
            }
        } */
    }
    
    

    private void GetConfiguration()
    {
    	System.out.println("GUI_GetConfiguration()");
    	
        if (Reader != null)
        {
            //if (Reader.IsHandling && !Reader.IsInventorying)
            {
                //this.Cursor = Cursors.WaitCursor;
      				
                //// Version - reader
                //reader.GetVersion(0); Thread.Sleep(300);
  				Reader.GetVersion(0);
  				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                //// Version - H/W
                //reader.GetVersion(5); Thread.Sleep(300);
  				Reader.GetVersion(5);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                //// Version - Root
                //reader.GetVersion(2); Thread.Sleep(300);
  				Reader.GetVersion(2);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

                //// Version - reader
                //this.lblVersionReader.Text = reader.GetVersionToSync(0); Thread.Sleep(300);
                //// Version - H/W
                //this.lblVersionHardware.Text = reader.GetVersionToSync(5); Thread.Sleep(300);
                //// Version - Root
                //this.lblVersionRoot.Text = reader.GetVersionToSync(2); Thread.Sleep(300);

  				
            	lblHardware.setEnabled(true);
  				lblroot.setEnabled(true);
  				lblReader.setEnabled(true);
  				
 				 // Power
 				//if(true)
 				{
 					byte[] addpacket;
 					ByteBuffer writebuffer;
 					byte[] payload = {'y', ' ', 'p'};
 					int length = payload.length;
 					
 					//this.bResponseRequired = bResponseRequired;
 					//PACKET FORMAT : [0x3e],payload,[0x0d],[0x0a]
 					addpacket = new byte[length + 3]; //include start, end signal
 					addpacket[0] = 0x3e;
 					//Array.Copyof(payload, 0, packet, 1, length); // C#
 					System.arraycopy(payload, 0, addpacket, 1, length); //java
 					addpacket[length + 1] = 0x0d;
 					addpacket[length + 2] = 0x0a;
 					
 					//packets.add(packet);
 					
 					writebuffer = ByteBuffer.wrap(addpacket);			
 					Reader.Write(writebuffer);
 					
 				 //Reader.GetPower(); //thread.sleep(300);	                    
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
 				}
 				//else
 				{
				//Antenna state
                Reader.GetAntennaState(); //Thread.sleep(300);	                    
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
 				}

                //this.Cursor = Cursors.Arrow;
            }
        }
        
        /*
        if (Reader2 != null)
        {
            //if (Reader.IsHandling && !Reader.IsInventorying)
            {
                //this.Cursor = Cursors.WaitCursor;
      				
                //// Version - reader
                //reader.GetVersion(0); Thread.Sleep(300);
  				Reader2.GetVersion(0);
  				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                //// Version - H/W
                //reader.GetVersion(5); Thread.Sleep(300);
  				Reader2.GetVersion(5);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                //// Version - Root
                //reader.GetVersion(2); Thread.Sleep(300);
  				Reader2.GetVersion(2);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

                //// Version - reader
                //this.lblVersionReader.Text = reader.GetVersionToSync(0); Thread.Sleep(300);
                //// Version - H/W
                //this.lblVersionHardware.Text = reader.GetVersionToSync(5); Thread.Sleep(300);
                //// Version - Root
                //this.lblVersionRoot.Text = reader.GetVersionToSync(2); Thread.Sleep(300);

  				
            	lblHardware.setEnabled(true);
  				lblroot.setEnabled(true);
  				lblReader.setEnabled(true);
  				
 				 // Power
 				//if(true)
 				{
 				 Reader2.GetPower(); //thread.sleep(300);	                    
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
 				}
 				//else
 				{
				//Antenna state
                Reader2.GetAntennaState(); //Thread.sleep(300);	                    
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
 				}

                //this.Cursor = Cursors.Arrow;
            }
        }*/
    }

    private void SetConfiguration()
    {
    	System.out.println("GUI_SetConfiguration()");   
    	
        if (Reader != null)
        {
            //if (reader.IsHandling && !reader.IsInventorying)
            {
               // this.Cursor = Cursors.WaitCursor;
            	
			    int antstate = ( chckbxAntena.isSelected() ? 1 : 0) + 
                        ((chckbxAntena_1.isSelected() ? 1 : 0) << 1) +
                        ((chckbxAntena_2.isSelected() ? 1 : 0) << 2) +
                        ((chckbxAntena_3.isSelected() ? 1 : 0) << 3);
	              
                int powervalue = (Integer)power.getValue();
               
                Reader.SetAntennaState(antstate);  //Thread.Sleep(300);             

                Reader.SetPower(powervalue); //Thread.Sleep(300);
                
                //this.Cursor = Cursors.Arrow;
            }
        }
    }
    

    private void GetTagMemory(int bank, int location, int length, String password)
    {
    	System.out.println("GUI_GetTagMemory()");
    	
        if (Reader != null)
        {            
            //if (Reader.IsHandling)
            {           
            	Reader.ReadTagMemory(TagType.ISO18000_6C_GEN2, bank, location, length, password);
            }
        }
    }

    private void SetTagMemory(int bank, int location, int length, String data, String password)
    {
    	System.out.println("GUI_SetTagMemory()");
    	
        if (Reader != null)
        {         
           // if (Reader.IsHandling)
            {
               Reader.WriteTagMemory(TagType.ISO18000_6C_GEN2, bank, location, data, password);
            }
        }
    }


    private void LockTag(String killpermissions, String accesspermissions, String epcpermissions, String tidpermissions, String userpermissions, String password)
    {
    	System.out.println("GUI_LockTag()");
        if (Reader != null)
        {          
            //if (Reader.IsHandling)
            {
                String mask = "";
                String action = "";
             
              //  if (Reader.IsRf1000Series)
                {
                    mask =  killpermissions +
                            accesspermissions +
                            epcpermissions +
                            tidpermissions;

                    action = "000" + userpermissions;
                }               
                
                Reader.LockTag(mask, action, password); 
                
            }
        }
    }

   
    private void KillTag(String password)
    {
    	System.out.println("GUI_KillTag()");
    	
        if (Reader != null)
        {            
           // if (Reader.IsHandling)
            {                
                Reader.KillTag(password);                
            }
        }
    }
    
    
    
    
 
	  //�׽�Ʈ ����
	  public class ReaderEventHandler implements IEventHandler<ReaderEventArgs> {
 
		  ReaderApi reader;

		  public ReaderEventHandler(ReaderApi reader){
			  this.reader = reader;
			  reader.Visited.addEventHandler(this);
			  
		  }
	  	@Override
	  	public void OnReaderEvent(Object sender, ReaderEventArgs e) {
	  		/*
	  		if(e.getpayload() != null)
	  		{
	  			String s = new String(e.getpayload());
	  			//System.out.println(s);
	  			System.out.println( "OnReaderEvent [" + e.getkind() + "] [" + e.getmessage() + "] [" + s + "]");
	  		}
	  		else
	  		{
	  			System.out.println( "OnReaderEvent [" + e.getkind() + "] [" + e.getmessage() + "] [" + e.getpayload() + "]");	  			
	  		}
	  		*/
	  		System.out.println("REDERIP = " + reader.getIpAddr() );
	  		ReaderApi rd = (ReaderApi)sender;
	  		rd.getIpAddr();
	  		
	  		switch(e.getkind())
	  		{
	  			case Connected:	  				
	  				//GetConfiguration();
	  				
	  				reader.GetVersion(0);	
	  				
	  				reader.GetVersion(5);
					
	  				reader.GetVersion(2);
					
	  				reader.GetAntennaState();
					
	  				reader.GetPower();
	
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	  				tabbedPane.setEnabled(true);
	  				lblstatusLabel.setText(e.getmessage());
	  				btnConnetion.setText("Disconnect");
  				
	  				config_btnRead.setEnabled(true);
	  				config_btnWrite.setEnabled(true);
	  				
	  					  				  				
	  				break;
	  			case Disconnected:
	  				tabbedPane.setEnabled(false);
	  				lblstatusLabel.setText(e.getmessage());
	  				btnConnetion.setText("Connect");
	  								
	  				
	            	lblReader.setEnabled(false);
	            	lblHardware.setEnabled(false);
	            	lblroot.setEnabled(false);
	            	
                	chckbxAntena.setEnabled(false);
                	chckbxAntena_1.setEnabled(false);
                	chckbxAntena_2.setEnabled(false);
                	chckbxAntena_3.setEnabled(false);
                	
                	power.setEnabled(false); 
                	
	  				config_btnRead.setEnabled(false);
	  				config_btnWrite.setEnabled(false);
	  				
                    //this.splitContainer1.Panel2.Enabled = false;
	  				break;
	  			case timeout:
	  				break;
	  			case Version:
	  				 				
	  				String payload =  new String(e.getpayload());		  			
                    String items = payload.substring(1,3);

                    switch (items)
                    {
                        case "v0": // Reader Version
                        	Readerver = payload.substring(3,payload.length() - 2);
                            break;
                        case "v2": // Root Version
                        	Hardwarever = payload.substring(3,payload.length() - 2);
                            break;
                        case "v5": // H/W Version
                        	rootver = payload.substring(3,payload.length() - 2);
                            break;
                    }
                                       
	            	lblReader.setText("* Reader : " + Readerver);
	            	lblHardware.setText("* Hardware :" + Hardwarever);
	            	lblroot.setText("root :" + rootver);
	            	
	            	lblReader.setEnabled(true);
	            	lblHardware.setEnabled(true);
	            	lblroot.setEnabled(true);
	            	
	  				break;
	  			case AntennaState:	  				
	  			case Power:
                    //payload = Encoding.ASCII.GetString(e.Payload);
	  				payload = new String(e.getpayload());
	  				
                    switch (payload.substring(1, 2))
                    {
                        case "e": // Antenna State
                            //int number = Convert.ToInt32(payload.substring(1));
                        	//int antenastate = Integer.parseInt(payload.substring(2,3));
                        	int antenastate = Integer.parseInt(payload.substring(2,payload.length() - 2));
                        	
                        	System.out.println("antenastate = " + antenastate );    
                        	
                        	boolean checked1 = (antenastate & 0x0001) > 0 ? true:false;
                        	boolean checked2 = (antenastate & 0x0002) > 0 ? true:false;
                        	boolean checked3 = (antenastate & 0x0004) > 0 ? true:false;
                        	boolean checked4 = (antenastate & 0x0008) > 0 ? true:false;

                        	chckbxAntena.setSelected(checked1);
                        	chckbxAntena_1.setSelected(checked2);
                        	chckbxAntena_2.setSelected(checked3);
                        	chckbxAntena_3.setSelected(checked4);
                        	
                        	chckbxAntena.setEnabled(true);
                        	chckbxAntena_1.setEnabled(true);
                        	chckbxAntena_2.setEnabled(true);
                        	chckbxAntena_3.setEnabled(true);
                        	                       	
                            break;
                            
                        case "p": //Power Value
                            //this.nudPower.Value = Convert.ToInt32(payload.Substring(1));
                        	//int powervalue = Integer.parseInt(payload.substring(2,5));
                        	
                        	int powervalue = Integer.parseInt(payload.substring(2,payload.length() - 2));
                        	System.out.println("powervalue = " + powervalue );
                        	
                        	power.setValue(powervalue);
                        	power.setEnabled(true);                  
                            break;
                    }
                    
	  				break;
	  			case TagId:                    
                   // payload = Encoding.ASCII.GetString(e.Payload);
	  				payload = new String(e.getpayload());
                    //String[] tagIds = payload.Split(new String[] { "\r\n>" }, StringSplitOptions.RemoveEmptyEntries);

	  				testcount++;
	  				//System.out.println("testcount = " + testcount );
	  				TagIDtable = (DefaultTableModel)table.getModel();
	  				TagIDtable.insertRow(0, new Object[]{payload,testcount});  					

	  				//table.updateUI();



	  				
	  				
                    //foreach (String tagid in tagIds)
                    {
                        //this.lvwInventory.Items.Add(tagid);

                        //String epc = tagid.Substring(2);

                        //this.lvwInventory.BeginUpdate();

                        //ListViewItem lvi = this.lvwInventory.Items.Insert(0, epc);

                       // String txt = String.Empty;
                       // if (reader.TagType == TagType.ISO18000_6C_GEN2)
                        {
                            
                           // int p = 4;
                          //  if (tagid.length > p)
                            {
                            //    String hex = epc.Substring(p, epc.Length - p);
                             //   txt = reader.MakeTextFromHex(hex);
                            }
                        }
                        //lvi.SubItems.Add(txt);

                        //this.lvwInventory.EndUpdate();
                        //this.lvwInventory.Refresh();
                    }
	  				break;
	  			case GetTagMemory:
                    //payload = Encoding.ASCII.GetString(e.Payload);
	  				payload = new String(e.getpayload());
                    if (payload.substring(2, 3).equals("T") || payload.substring(2, 3).equals("B"))
                    {
                    	rw_tagid_textField.setText(payload.substring(3, payload.length() - 2));
                    	//rw_tagid_textField.setText(payload.substring(3,7) + "-" 
                    	//							+ payload.substring(7,11) + "-" 
                    	//							+ payload.substring(11,15) + "-"
                    	//							+ payload.substring(15,19) + "-"
                    	//							+ payload.substring(19,23) + "-"
                    	//							+ payload.substring(23,27));                	
                    	
                    }
                    break;

	  			case TagResponseCode:
                    //payload = Encoding.ASCII.GetString(e.Payload);	  				
	  				payload = new String(e.getpayload());
	  				
                    if (payload.substring(2, 3).equals("C"))
                    {                    	
                        String code = payload.substring(3,5);                        
                        
                        if(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals(tabbedPane.getTitleAt(2)))
                        {
                        	//rw_textPane.setText(code + " - " + Reader.Responses(code));
                        	rw_txtarea.append(code + " - " + Reader.Responses(code) + "\n");
                        	rw_txtarea.setCaretPosition(rw_txtarea.getDocument().getLength());                        	
                        }
                        else
                        {
                        	//lk_txtarea.setText(code + " - " + Reader.Responses(code));
                           	lk_txtarea.append(code + " - " + Reader.Responses(code) + "\n");  
                        	lk_txtarea.setCaretPosition(lk_txtarea.getDocument().getLength());
                        }                      

                    }
                    break;

	  			  			
	  		
	  		}
	  		
	  		
	  	}

	  } 
	  
	  
	  /*
	  //�׽�Ʈ ����
	  public class ReaderEventHandler2 implements IEventHandler<ReaderEventArgs> {
 
		  ReaderApi reader2;

		  public ReaderEventHandler2(ReaderApi reader2){
			  this.reader2 = reader2;
			  reader2.Visited.addEventHandler(this);
			  
		  }
		  
	  	@Override
	  	public void OnReaderEvent(Object sender, ReaderEventArgs e) {
	  		
	  		if(e.getpayload() != null)
	  		{
	  			String s = new String(e.getpayload());
	  			//System.out.println(s);
	  			System.out.println( "OnReaderEvent [" + e.getkind() + "] [" + e.getmessage() + "] [" + s + "]");
	  		}
	  		else
	  		{
	  			System.out.println( "OnReaderEvent [" + e.getkind() + "] [" + e.getmessage() + "] [" + e.getpayload() + "]");	  			
	  		}
	  		
	  		System.out.println("REDERIP = " + reader2.getIpAddr() );
	  		switch(e.getkind())
	  		{
	  			case Connected:	  				
	  				//GetConfiguration();
	  				
	  				reader2.GetVersion(0);	
	  				
	  				reader2.GetVersion(5);
					
	  				reader2.GetVersion(2);
					
	  				reader2.GetAntennaState();
					
	  				reader2.GetPower();
	
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	  				tabbedPane.setEnabled(true);
	  				lblstatusLabel.setText(e.getmessage());
	  				btnConnetion.setText("Disconnect");
  				
	  				config_btnRead.setEnabled(true);
	  				config_btnWrite.setEnabled(true);
	  				
	  					  				  				
	  				break;
	  			case Disconnected:
	  				tabbedPane.setEnabled(false);
	  				lblstatusLabel.setText(e.getmessage());
	  				btnConnetion.setText("Connect");
	  								
	  				
	            	lblReader.setEnabled(false);
	            	lblHardware.setEnabled(false);
	            	lblroot.setEnabled(false);
	            	
                	chckbxAntena.setEnabled(false);
                	chckbxAntena_1.setEnabled(false);
                	chckbxAntena_2.setEnabled(false);
                	chckbxAntena_3.setEnabled(false);
                	
                	power.setEnabled(false); 
                	
	  				config_btnRead.setEnabled(false);
	  				config_btnWrite.setEnabled(false);
	  				
                    //this.splitContainer1.Panel2.Enabled = false;
	  				break;
	  			case timeout:
	  				break;
	  			case Version:
	  				 				
	  				String payload =  new String(e.getpayload());		  			
                    String items = payload.substring(1,3);

                    switch (items)
                    {
                        case "v0": // Reader Version
                        	Readerver = payload.substring(3,payload.length() - 2);
                            break;
                        case "v2": // Root Version
                        	Hardwarever = payload.substring(3,payload.length() - 2);
                            break;
                        case "v5": // H/W Version
                        	rootver = payload.substring(3,payload.length() - 2);
                            break;
                    }
                                       
	            	lblReader.setText("* Reader : " + Readerver);
	            	lblHardware.setText("* Hardware :" + Hardwarever);
	            	lblroot.setText("root :" + rootver);
	            	
	            	lblReader.setEnabled(true);
	            	lblHardware.setEnabled(true);
	            	lblroot.setEnabled(true);
	            	
	  				break;
	  			case AntennaState:	  				
	  			case Power:
                    //payload = Encoding.ASCII.GetString(e.Payload);
	  				payload = new String(e.getpayload());
	  				
                    switch (payload.substring(1, 2))
                    {
                        case "e": // Antenna State
                            //int number = Convert.ToInt32(payload.substring(1));
                        	//int antenastate = Integer.parseInt(payload.substring(2,3));
                        	int antenastate = Integer.parseInt(payload.substring(2,payload.length() - 2));
                        	
                        	System.out.println("antenastate = " + antenastate );    
                        	
                        	boolean checked1 = (antenastate & 0x0001) > 0 ? true:false;
                        	boolean checked2 = (antenastate & 0x0002) > 0 ? true:false;
                        	boolean checked3 = (antenastate & 0x0004) > 0 ? true:false;
                        	boolean checked4 = (antenastate & 0x0008) > 0 ? true:false;

                        	chckbxAntena.setSelected(checked1);
                        	chckbxAntena_1.setSelected(checked2);
                        	chckbxAntena_2.setSelected(checked3);
                        	chckbxAntena_3.setSelected(checked4);
                        	
                        	chckbxAntena.setEnabled(true);
                        	chckbxAntena_1.setEnabled(true);
                        	chckbxAntena_2.setEnabled(true);
                        	chckbxAntena_3.setEnabled(true);
                        	                       	
                            break;
                            
                        case "p": //Power Value
                            //this.nudPower.Value = Convert.ToInt32(payload.Substring(1));
                        	//int powervalue = Integer.parseInt(payload.substring(2,5));
                        	
                        	int powervalue = Integer.parseInt(payload.substring(2,payload.length() - 2));
                        	System.out.println("powervalue = " + powervalue );
                        	
                        	power.setValue(powervalue);
                        	power.setEnabled(true);                  
                            break;
                    }
                    
	  				break;
	  			case TagId:                    
                   // payload = Encoding.ASCII.GetString(e.Payload);
	  				payload = new String(e.getpayload());
                    //String[] tagIds = payload.Split(new String[] { "\r\n>" }, StringSplitOptions.RemoveEmptyEntries);

	  				testcount++;
	  				//System.out.println("testcount = " + testcount );
	  				TagIDtable = (DefaultTableModel)table.getModel();
	  				TagIDtable.insertRow(0, new Object[]{payload,testcount +"  "+ reader2.getIpAddr()});  					

	  				//table.updateUI();



	  				
	  				
                    //foreach (String tagid in tagIds)
                    {
                        //this.lvwInventory.Items.Add(tagid);

                        //String epc = tagid.Substring(2);

                        //this.lvwInventory.BeginUpdate();

                        //ListViewItem lvi = this.lvwInventory.Items.Insert(0, epc);

                       // String txt = String.Empty;
                       // if (reader.TagType == TagType.ISO18000_6C_GEN2)
                        {
                            
                           // int p = 4;
                          //  if (tagid.length > p)
                            {
                            //    String hex = epc.Substring(p, epc.Length - p);
                             //   txt = reader.MakeTextFromHex(hex);
                            }
                        }
                        //lvi.SubItems.Add(txt);

                        //this.lvwInventory.EndUpdate();
                        //this.lvwInventory.Refresh();
                    }
	  				break;
	  			case GetTagMemory:
                    //payload = Encoding.ASCII.GetString(e.Payload);
	  				payload = new String(e.getpayload());
                    if (payload.substring(2, 3).equals("T") || payload.substring(2, 3).equals("B"))
                    {
                    	rw_tagid_textField.setText(payload.substring(3, payload.length() - 2));
                    	//rw_tagid_textField.setText(payload.substring(3,7) + "-" 
                    	//							+ payload.substring(7,11) + "-" 
                    	//							+ payload.substring(11,15) + "-"
                    	//							+ payload.substring(15,19) + "-"
                    	//							+ payload.substring(19,23) + "-"
                    	//							+ payload.substring(23,27));                	
                    	
                    }
                    break;

	  			case TagResponseCode:
                    //payload = Encoding.ASCII.GetString(e.Payload);	  				
	  				payload = new String(e.getpayload());
	  				
                    if (payload.substring(2, 3).equals("C"))
                    {                    	
                        String code = payload.substring(3,5);                        
                        
                        if(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals(tabbedPane.getTitleAt(2)))
                        {
                        	//rw_textPane.setText(code + " - " + Reader.Responses(code));
                        	rw_txtarea.append(code + " - " + Reader2.Responses(code) + "\n");
                        	rw_txtarea.setCaretPosition(rw_txtarea.getDocument().getLength());                        	
                        }
                        else
                        {
                        	//lk_txtarea.setText(code + " - " + Reader.Responses(code));
                           	lk_txtarea.append(code + " - " + Reader2.Responses(code) + "\n");  
                        	lk_txtarea.setCaretPosition(lk_txtarea.getDocument().getLength());
                        }                      

                    }
                    break;

	  			  			
	  		
	  		}
	  		
	  		
	  	}

	  } 
	  */
}
