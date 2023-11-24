package rpc.chat.client;
import rpc.chat.interfaces.IClient;

public class Client implements IClient {
	
	private String name;
	private boolean isNewMember;
	private String[] listMember;
	private boolean isNewMsg;
	private String msg;	

	public Client(String name) {
		this.name = name;
		this.msg=null;
		this.isNewMsg=false;
		this.isNewMember=false;
		this.listMember=null;
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public void receive(String msg) {
		this.msg=msg;
		setNewMsg(true);
	}
	
	@Override
	public void updateMember(String clients) {
		listMember=clients.split("\\+");
		isNewMember=true;
	}

	public boolean getNewMsg() {
		return isNewMsg;
	}
	
	public void setNewMsg(boolean st) {
		this.isNewMsg=st;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public String[] getMember() {
		return listMember;
	}
	
	
	public void setNewMember(boolean st) {
		this.isNewMember=st;
	}
	
	public boolean getNewMember() {
		return isNewMember;
	}

	


}
