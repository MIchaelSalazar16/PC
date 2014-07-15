package chats;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ChatBasicoCliente extends Remote {

	void entregarMensaje(String nick, String id) throws RemoteException;

}
