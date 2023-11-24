package rpc.chat.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;
import rpc.chat.component.AnimationScroll;
import rpc.chat.component.ChatBox;
import rpc.chat.component.ChatBox.BoxType;
import rpc.chat.component.ModelMessage;
import rpc.chat.component.RoundPanel;
import rpc.chat.interfaces.IProxy;
import rpc.chat.interfaces.IProxyFactory;
import rpc.chat.server.RPCRuntime;
import rpc.chat.server.Server;
import rpc.chat.server.ServerProxy;

public class UIServer {
	private int portServer = 8080;
	private JPanel panelChat;
	private RoundPanel panelServer;
	private Server myServer;
	private int totalMember = 0;
	private JFrame frame;
	private JLabel lblTotal;
	private JScrollPane scrollPane;
	private AnimationScroll animationScroll;
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy, hh:mmaa");
	private JLabel lblNewLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIServer window = new UIServer();
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
	public UIServer() throws Exception {
		initialize();
		RunServer();
		ScreenChat();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws Exception
	 */
	private void initialize() throws Exception {
		frame = new JFrame("Server");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void RunServer() throws IOException {
		System.out.println("=== SERVER ===");
		myServer = new Server();
		RPCRuntime rpc = new RPCRuntime(new ServerSocket(portServer));
		totalMember = myServer.getTotalMember();
		rpc.register("ChatServer", new IProxyFactory() {
			@Override
			public IProxy createProxy(BufferedReader inputStream, PrintWriter outputStream) {
				return new ServerProxy(inputStream, outputStream, myServer);
			}
		});

		(new Thread(rpc)).start();

	}

	public void listenEventForServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10);
						if (myServer.isNew()) {
							panelServer.add(
									new ChatBox(BoxType.LEFT,
											new ModelMessage("|", df.format(new Date()), myServer.getNewLog())),
									"width ::100%");
							panelServer.repaint();
							panelServer.revalidate();
							animationScroll.scrollVertical(scrollPane, scrollPane.getVerticalScrollBar().getMaximum());
							myServer.setNew(false);
						}
						if (myServer.getTotalMember() != totalMember) {
							totalMember = myServer.getTotalMember();
							lblTotal.setText(String.valueOf(totalMember));

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
			java.util.logging.Logger.getLogger(UIServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(UIServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(UIServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(UIServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		frame.setBounds(100, 100, 667, 477);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		panelChat = new JPanel();
		panelChat.setBackground(new Color(192, 192, 192));
		panelChat.setBounds(113, 29, 448, 376);
		panelChat.setLayout(null);
		frame.getContentPane().add(panelChat);

		// Create a scrollPane model
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		// Configuration for scrollPane
		scrollPane.setBounds(0, 44, 448, 309);
		panelChat.add(scrollPane);

		animationScroll = new AnimationScroll(panelChat);
		panelServer = new RoundPanel();
		panelServer.setBackground(new Color(0, 0, 0, 0));
		panelServer.setLayout(new MigLayout("wrap,fillx"));

		JTextArea chatArea = new JTextArea();
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		scrollPane.setViewportView(panelServer);
		// Configuration for chatArea
		chatArea.setFont(new Font("Tahoma", Font.PLAIN, 13));

		panelServer.add(new ChatBox(BoxType.LEFT, new ModelMessage("", df.format(new Date()), "Server đã sẵn sàng!")),
				"width ::100%");

		JLabel lblNewLabel_1 = new JLabel("Server");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		lblNewLabel_1.setBounds(27, 0, 231, 45);
		panelChat.add(lblNewLabel_1);

		lblTotal = new JLabel(String.valueOf(totalMember));
		lblTotal.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblTotal.setBounds(381, 17, 29, 17);
		panelChat.add(lblTotal);

		lblNewLabel = new JLabel("Client Connected:");
		lblNewLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
		lblNewLabel.setBounds(257, 15, 114, 19);
		panelChat.add(lblNewLabel);

		listenEventForServer();
	}
}
