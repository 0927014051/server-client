package rpc.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.NameAlreadyBoundException;

import rpc.chat.interfaces.IProxy;

public class ServerProxy implements Runnable, IProxy {
	BufferedReader in;
	PrintWriter out;
	Server server;
	boolean running = true;
	ServerSideClientProxy lc;

	public ServerProxy(BufferedReader in, PrintWriter out, Object server) {
		this.server = (Server) server;
		this.in = in;
		this.out = out;

	}

	private void broadcast() {
		try {
			out.println("Nhap tin nhan: ");
			out.flush();
			String msg = in.readLine();
			String receiver = in.readLine();
			server.broadcast(msg, lc, receiver);
			out.println("200 - Success");
		} catch (Exception e) {

			out.println("500 - Internal Server Error");
			out.println(e.getMessage());

		}

		out.flush();
	}

	private void login() throws IOException {
		out.println("Nhap IP:");
		out.flush();
		String IP = in.readLine();
		System.out.println("IP: " + IP);

		out.println("Nhap Port:");
		out.flush();
		int port = Integer.parseInt(in.readLine());
		System.out.println("Port: " + port);

		try {
			lc = new ServerSideClientProxy(IP, port);
			server.login(lc);
			out.println("200 - Success");

		} catch (NameAlreadyBoundException e) {
			out.println("201 - Name Already");
		} catch (Exception e) {

			out.println("500 - Internal Server Error");
			out.println(e.getClass().getName());

		}
		out.flush();
		server.updateMember();
	}

	private void logout() throws IOException {
		try {

			server.logout(lc);
			out.println("200 - Success");

		} catch (Exception e) {

			out.println("500 - Internal Server Error");
			out.println(e.getMessage());

		}
		out.flush();
		server.updateMember();
		running = false;
		in.close();
		out.close();
	}

	public void run() {
		while (running) {
			try {
				out.println("Methods: (1)broadcast (2)login (3)logout");
				out.flush();

				String line = in.readLine();

				switch (line) {
				case "1":
					this.broadcast();
					break;

				case "2":
					this.login();
					break;

				case "3":
					this.logout();
					break;

				default:
					out.println("Khong ho tro!");
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
