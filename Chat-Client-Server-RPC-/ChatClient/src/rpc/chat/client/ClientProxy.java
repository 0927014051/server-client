package rpc.chat.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.naming.NameAlreadyBoundException;

import rpc.chat.interfaces.IClient;
import rpc.chat.interfaces.IServer;

public class ClientProxy implements IServer {
	Socket socket;
	BufferedReader input;
	PrintWriter output;
	RPCRuntime rpc;

	public ClientProxy(String host, int port, RPCRuntime rpc) throws Exception {
		this.rpc = rpc;
		socket = new Socket(host, port);

		input = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
		output = new PrintWriter(socket.getOutputStream(),false,StandardCharsets.UTF_8);
		int size = Integer.parseInt(input.readLine());

		for (int i = 0; i < size; i++) {
			System.out.println(input.readLine());
		}

		output.println("ChatServer");
		output.flush();
		String msg=input.readLine();
		System.out.println(msg);
		if (!msg.equals("Methods: (1)broadcast (2)login (3)logout")) {
			throw new Exception("Falscher-Proxy-Alarm!");
		}
	}

	@Override
	public void broadcast(String msg, IClient client, String receiver) {
		output.println("1");
		output.flush();

		try {

			input.readLine();
			output.println(msg);
			output.println(receiver);
			output.flush();
			
			evaluateErrorCode();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void login(IClient client) throws NameAlreadyBoundException  {
	     
		output.println("2");
		output.flush();
		try {
			input.readLine();
			output.println( InetAddress.getLocalHost().getHostAddress());
			output.flush();
			
			input.readLine();
			output.println(rpc.socket.getLocalPort());
			output.flush();
			
			evaluateErrorCode();
		   
		
		} catch (NameAlreadyBoundException e) {
			 throw new NameAlreadyBoundException(e.getMessage());
		}catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void logout(IClient client) {
		output.println("3");
		output.flush();

		try {
			
			evaluateErrorCode();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void evaluateErrorCode() throws IOException, NameAlreadyBoundException {
		switch (input.readLine().substring(0, 3)) {
		case "500":
			String line = input.readLine();
			System.out.println(input.readLine());
			throw new RuntimeException(line);

		case "200":
			input.readLine();
			return;
		case "201":
			input.readLine();
			throw new NameAlreadyBoundException("Tên đăng nhập đã tồn tại");
		default:
			System.out.println(input.readLine());
			throw new RuntimeException("Error");
		}
	}
}
