package rpc.chat.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import rpc.chat.interfaces.IClient;

public class ServerSideClientProxy implements IClient {
	Socket socket;
	BufferedReader input;
	PrintWriter output;

	public ServerSideClientProxy(String host, int port) throws Exception {
		socket = new Socket(host, port);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
		output = new PrintWriter(socket.getOutputStream(),false,StandardCharsets.UTF_8);
		int size = Integer.parseInt(input.readLine());

		for (int i = 0; i < size; i++) {
			System.out.println(input.readLine());
		}
		output.println("ChatClient");
		output.flush();
		
		String msg = input.readLine();
		System.out.println(msg);
		if (!msg.equals("Methods: (1)Receive (2)GetName (3)Update Member")) {
			throw new Exception("Falscher-Proxy-Alarm!");
		}
	}

	private Object evaluateErrorCode() throws IOException {
		switch (input.readLine().substring(0, 3)) {
		case "500":
			String line = input.readLine();
			input.readLine();
			throw new RuntimeException(line);

		case "200":
			input.readLine();
			return null;
		case "400":
			String s = input.readLine();
			input.readLine();
			return s;

		default:
			input.readLine();
			throw new RuntimeException("Error");
		}
	}

	@Override
	public void receive(String msg) {
		output.println("1");
		output.flush();
		try {

			input.readLine();

			output.println(msg);
			output.flush();

			evaluateErrorCode();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public String getName() {
		output.println("2");
		output.flush();

		try {
			
			return (String) evaluateErrorCode();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateMember(String member) {
		output.println("3");
		output.flush();

		try {
			input.readLine();
			output.println(member);
			output.flush();
			evaluateErrorCode();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
}
