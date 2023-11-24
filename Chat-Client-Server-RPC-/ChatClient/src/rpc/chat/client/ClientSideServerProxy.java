package rpc.chat.client;
import java.io.BufferedReader;
import java.io.PrintWriter;

import rpc.chat.interfaces.IProxy;

public class ClientSideServerProxy implements Runnable, IProxy {
	BufferedReader in;
	PrintWriter out;

	Client client;

	boolean running = true;

	public ClientSideServerProxy(BufferedReader in, PrintWriter out, Object client) {
		this.client = (Client) client;
		this.in = in;
		this.out = out;
	}

	private void getName() {

		try {

			out.println("400 - Success");
			out.println(client.getName());

		} catch (Exception e) {

			out.println("500 - Internal Server Error");
			out.println(e.getMessage());

		}
		out.flush();

	}

	private void receive() {
		try {
			out.println("Tin nhan la gi");
			out.flush();
			client.receive(in.readLine());
			out.println("200 - Success");

		} catch (Exception e) {

			out.println("500 - Internal Server Error");
			out.println(e.getMessage());

		}
		out.flush();

	}
	
	private void updateMember() {
		try {
			out.println("Update Memeber");
			out.flush();
			client.updateMember(in.readLine());
			out.println("200 - Success");

		} catch (Exception e) {

			out.println("500 - Internal Server Error");
			out.println(e.getMessage());

		}
		out.flush();

	}

	public void run() {
		while (running) {
			try {
				out.println("Methods: (1)Receive (2)GetName (3)Update Member");
				out.flush();

				String line = in.readLine();

				switch (line) {
				case "1":
					this.receive();
					break;

				case "2":
					this.getName();
					break;
				case "3":
					this.updateMember();
					break;

				default:
					out.println("log error!");
					out.flush();
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
				running = false;
			}
		}
	}
}
