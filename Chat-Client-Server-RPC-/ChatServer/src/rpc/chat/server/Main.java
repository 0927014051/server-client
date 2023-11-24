package rpc.chat.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;

import rpc.chat.interfaces.IProxy;
import rpc.chat.interfaces.IProxyFactory;

public class Main {

	public static void main(String[] args) throws IOException {
		System.out.println("=== SERVER ===");
		Server s = new Server();
		RPCRuntime rpc = new RPCRuntime(new ServerSocket(8080));
	
		rpc.register("ChatServer", new IProxyFactory() {
			@Override
			public IProxy createProxy(BufferedReader inputStream, PrintWriter outputStream) {
				return new ServerProxy(inputStream, outputStream, s);
			}
		});
		
		(new Thread(rpc)).start();

	}
}
