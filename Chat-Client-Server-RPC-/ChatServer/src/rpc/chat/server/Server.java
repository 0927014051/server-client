package rpc.chat.server;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NameAlreadyBoundException;

import rpc.chat.interfaces.IClient;
import rpc.chat.interfaces.IServer;

public class Server implements IServer {

	private List<IClient> clients = new ArrayList<>();
	private String eventLog = "";
	private boolean isNewLog = false;

	/* Phát tin */
	@Override
	public void broadcast(String msg, IClient client, String receiver) {
		eventLog = (client.getName() + " to " + receiver + ":\n" + msg);
		isNewLog = true;
		if (receiver.equals("ALL")) {
			for (IClient iClient : clients) {
				try {
					if (!iClient.getName().equals(client.getName())) {
						iClient.receive("ALL" + ":" + client.getName() + ":" + msg);
					}
				} catch (Exception e) {
					this.logout(iClient);
				}
			}
		} else {
			for (IClient iClient : clients) {
				try {
					if (iClient.getName().equals(receiver))
						iClient.receive(client.getName() + ": " + msg);
				} catch (Exception e) {
					this.logout(iClient);
				}
			}
		}
	}

	/* Đăng nhập */
	@Override
	public void login(IClient client) throws NameAlreadyBoundException {
		for (IClient iClient : clients)
			if (iClient.getName().equals(client.getName())) {
				throw new NameAlreadyBoundException();
			}
		clients.add(client);
		eventLog = client.getName() + " - " + ((ServerSideClientProxy) client).socket.getInetAddress().getHostAddress()+ " đã đăng nhập";
		isNewLog = true;
	}

	/* Cập nhật thành viên */
	public void updateMember() {
		String listMember = "ALL";
		for (IClient iClient : clients) {
			listMember += "+" + iClient.getName();
		}
		for (IClient iClient : clients) {
			iClient.updateMember(listMember);
		}
	}

	/* Đăng xuất */
	@Override
	public void logout(IClient client) {
		clients.remove(client);
		eventLog = client.getName() + " - " + ((ServerSideClientProxy) client).socket.getInetAddress().getHostAddress()+ " đã đăng xuất";
		isNewLog = true;

	}

	public int getTotalMember() {
		return clients.size();
	}

	public String getNewLog() {
		return eventLog;
	}

	public void setNewLog(String newMessage) {
		this.eventLog = newMessage;
	}

	public boolean isNew() {
		return isNewLog;
	}

	public void setNew(boolean isNew) {
		this.isNewLog = isNew;
	}

}
