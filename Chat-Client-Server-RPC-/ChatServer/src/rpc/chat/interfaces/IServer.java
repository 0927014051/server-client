package rpc.chat.interfaces;

import javax.naming.NameAlreadyBoundException;

public interface IServer {
	void broadcast(String msg, IClient client, String receiver);
	void login(IClient client)throws NameAlreadyBoundException;
	void logout(IClient client);
}
