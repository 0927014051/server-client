package rpc.chat.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.naming.NameAlreadyBoundException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;
import rpc.chat.client.Client;
import rpc.chat.client.ClientProxy;
import rpc.chat.client.ClientSideServerProxy;
import rpc.chat.client.RPCRuntime;
import rpc.chat.component.AnimationScroll;
import rpc.chat.component.ChatBox;
import rpc.chat.component.ChatBox.BoxType;
import rpc.chat.component.ModelMessage;
import rpc.chat.component.RoundPanel;
import rpc.chat.interfaces.IProxy;
import rpc.chat.interfaces.IProxyFactory;

public class UIClient {
	public static String serverIP = "192.168.1.7";
	public static int serverPort = 9999;
	private static int clientPort=3535;
	
	private JPanel panelLogin;
	private JPanel panelChat;
	private ClientProxy clientP;
	Hashtable<String, JPanel> listPanel = new Hashtable<String, JPanel>();
	private JComboBox<String> comboBox;
	private Client myClient;
	String mode = "ALL";
	boolean isupdate = false;
	private JFrame frame;
	boolean isOpen = false;
	boolean isConnect = false;
	private JScrollPane scrollPane;
	private AnimationScroll animationScroll;
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy, hh:mmaa");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIClient window = new UIClient();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public UIClient() throws Exception {
		initialize();
		panellogin();
		//ScreenChat();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws Exception
	 */
	private void initialize() throws Exception {
		frame = new JFrame("Client");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void addPanel(String name) {
		RoundPanel panel = new RoundPanel();
		panel.setBackground(new Color(0, 0, 0, 0));
		panel.setLayout(new MigLayout("wrap,fillx"));
		listPanel.put(name, panel);
	}

	public void run(String name) throws Exception {

		try {
			myClient = new Client(name);
			if (!isOpen) {
				RPCRuntime rpc = new RPCRuntime(new ServerSocket(clientPort));
				rpc.register("ChatClient", new IProxyFactory() {
					@Override
					public IProxy createProxy(BufferedReader inputStream, PrintWriter outputStream) {
						return new ClientSideServerProxy(inputStream, outputStream, myClient);
					}
				});
				Thread ab = new Thread(rpc);
				ab.start();
				isOpen = true;
				clientP = new ClientProxy(serverIP, serverPort, rpc);
			}

			clientP.login(myClient);
			ScreenChat();
			listenForMessages();
			panelLogin.setVisible(false);
			panelChat.setVisible(true);
			isConnect = true;
		} catch (NameAlreadyBoundException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Lỗi Port đã được sử dụng: " + e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	public void listenForMessages() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(100);
						if (myClient.getNewMsg()) {
							String sender = myClient.getMsg().split(":")[0];
							if (!listPanel.containsKey(sender)) {
								addPanel(sender);
							}

							if (sender.equals("ALL")) {
								try {
									listPanel.get(sender).add(
											new ChatBox(BoxType.LEFT,
													new ModelMessage(myClient.getMsg().split(":")[1],
															df.format(new Date()), myClient.getMsg().split(":")[2])),
											"width ::80%");
								} catch (Exception e) {

								}

							} else {
								listPanel
										.get(sender).add(
												new ChatBox(BoxType.LEFT, new ModelMessage(sender,
														df.format(new Date()), myClient.getMsg().split(":")[1])),
												"width ::80%");

							}

							listPanel.get(sender).repaint();
							listPanel.get(sender).revalidate();
							if (mode.equals(sender))
								animationScroll.scrollVertical(scrollPane,
										scrollPane.getVerticalScrollBar().getMaximum());
							myClient.setNewMsg(false);
						}
						if (myClient.getNewMember()) {
							isupdate = true;
							comboBox.removeAllItems();
							for (String client : myClient.getMember()) {
								comboBox.addItem(client);
							}
							myClient.setNewMember(false);
							comboBox.setSelectedItem(mode);
							isupdate = false;
						}

					} catch (Exception e) {
						System.err.println("=>" + e.getMessage());
					}
				}
			}
		}).start();
	}

	public void ScreenChat() {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(UIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(UIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(UIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(UIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		frame.setBounds(100, 100, 667, 477);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		panelChat = new JPanel();
		panelChat.setBackground(new Color(192, 192, 192));
		panelChat.setBounds(113, 29, 448, 385);
		panelChat.setLayout(null);
		frame.getContentPane().add(panelChat);

		JTextPane textMsg = new JTextPane();
		textMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==10) {
					if (!textMsg.getText().trim().isEmpty() && !textMsg.getText().trim().isBlank()) {
						listPanel.get(mode).add(
								new ChatBox(BoxType.RIGHT, new ModelMessage("", df.format(new Date()), textMsg.getText())),
								"al right,width ::80%");
						listPanel.get(mode).repaint();
						listPanel.get(mode).revalidate();
						animationScroll.scrollVertical(scrollPane, scrollPane.getVerticalScrollBar().getMaximum());
						clientP.broadcast(textMsg.getText(), myClient, mode);
						textMsg.setText(null);
					}
					e.consume();
				}}
		});
		textMsg.setBounds(39, 344, 267, 33);
		panelChat.add(textMsg);

		JButton btnSend = new JButton("Send");

		btnSend.setBounds(316, 344, 96, 33);
		panelChat.add(btnSend);

		// Create a scrollPane model
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		// Configuration for scrollPane
		scrollPane.setBounds(0, 51, 448, 237);
		panelChat.add(scrollPane);

		animationScroll = new AnimationScroll(panelChat);
		RoundPanel panel = new RoundPanel();
		panel.setBackground(new Color(0, 0, 0, 0));
		panel.setLayout(new MigLayout("wrap,fillx"));

		JTextArea chatArea = new JTextArea();
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		scrollPane.setViewportView(panel);
		// Configuration for chatArea
		chatArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		listPanel.put("ALL", panel);

		JLabel lblNewLabel_1 = new JLabel(myClient.getName());
		lblNewLabel_1.setBounds(99, 0, 231, 34);
		panelChat.add(lblNewLabel_1);
		comboBox = new JComboBox<String>();

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isupdate == false) {
					listPanel.get(mode).setVisible(false);
					mode = (String) comboBox.getSelectedItem();
					if (!listPanel.containsKey(mode)) {
						addPanel(mode);
						listPanel.get(mode).setVisible(true);
						scrollPane.setViewportView(listPanel.get(mode));
						listPanel.get(mode).repaint();
					} else {
						listPanel.get(mode).setVisible(true);
						scrollPane.setViewportView(listPanel.get(mode)); //
						listPanel.get(mode).repaint();
					}
				}

			}
		});

		comboBox.setBounds(37, 29, 77, 44);
		frame.getContentPane().add(comboBox);

		btnSend.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!textMsg.getText().trim().isEmpty() && !textMsg.getText().trim().isBlank()) {
					listPanel.get(mode).add(
							new ChatBox(BoxType.RIGHT, new ModelMessage("Me", df.format(new Date()), textMsg.getText())),
							"al right,width ::80%");
					listPanel.get(mode).repaint();
					listPanel.get(mode).revalidate();
					animationScroll.scrollVertical(scrollPane, scrollPane.getVerticalScrollBar().getMaximum());
					clientP.broadcast(textMsg.getText(), myClient, mode);
					textMsg.setText(null);
				}
			}
		});
		
		

	}

	public void panellogin() {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(UIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(UIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(UIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(UIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		panelLogin = new JPanel();
		panelLogin.setBounds(0, 0, 434, 261);
		panelLogin.setLayout(null);
		JTextField txtName = new JTextField();

		txtName.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		txtName.setBounds(161, 89, 155, 32);
		panelLogin.add(txtName);

		JLabel lblNewLabel = new JLabel("USER NAME");
		lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		lblNewLabel.setBounds(74, 94, 77, 23);
		panelLogin.add(lblNewLabel);

		JButton btnLogin = new JButton("LOGIN");
		btnLogin.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnLogin.setBounds(191, 147, 89, 32);
		panelLogin.add(btnLogin);
		frame.getContentPane().add(panelLogin);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				if (name.trim().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Nhập đầy đủ các thông số");
				} else {
					try {
						run(name);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (isOpen && isConnect) {
					clientP.logout(myClient);
				}

				System.exit(0);
			}
		});

	}
}
