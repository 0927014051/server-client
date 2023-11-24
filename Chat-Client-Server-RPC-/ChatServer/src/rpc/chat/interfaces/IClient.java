package rpc.chat.interfaces;

public interface IClient {
	void receive(String msg);
	String getName();
	void updateMember(String members);
}
