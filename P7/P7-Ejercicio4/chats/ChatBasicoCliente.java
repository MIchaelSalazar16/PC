package chats;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ChatBasicoCliente extends Remote {

	void entregarMensaje(int numMensaje, String mensaje) throws RemoteException;

}
