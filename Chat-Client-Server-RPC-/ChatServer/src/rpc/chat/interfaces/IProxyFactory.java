package rpc.chat.interfaces;
import java.io.BufferedReader;
import java.io.PrintWriter;

public interface IProxyFactory {

	IProxy createProxy(BufferedReader inputStream, PrintWriter outputStream);
}
