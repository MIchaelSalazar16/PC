
package chats;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;

public class Servidor implements ChatBasicoServidor {

	// Necesario añadir para el correcto funcionamiento
	public static Registry registry;
	
	// HashMap que almacena los usuarios activos por su nick.
	private HashSet<String> usuariosActivos;
	

    public Servidor() {
    	
    	usuariosActivos = new HashSet<String>();
    }

	@Override
	public boolean darseDeAlta(String nick) throws RemoteException {
		
		if (!usuariosActivos.contains(nick))
			usuariosActivos.add(nick);
		else
			return false;
		
        System.out.println("# '"+ nick + "' se registra en la sala");
        
		return true;
		
	}

	@Override
	public boolean darseDeBaja(String nick) throws RemoteException {
		
		boolean exito;
		
		exito = usuariosActivos.remove(nick);
		
		if (exito)
			System.out.println("# '"+ nick + "' abandona la sala");
		else
			System.out.println("! Error dando de baja al usuario: " + nick);
		
		return exito;
	}

	@Override
	public boolean difundir(String nick, String mensaje) throws RemoteException {
		
		// Lo mostramos en el log
		if (usuariosActivos.contains(nick)){
			System.out.println("@ " + nick + " escribe: " + mensaje);
			
			try { 
				
				for (String s : usuariosActivos) {
				
					ChatBasicoCliente stubCliente = (ChatBasicoCliente) registry.lookup(s);
					stubCliente.entregarMensaje(nick, mensaje);
				}
				
			} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			
			return true;
		}
		else
			return false;
	}

		
	

    public static void main(String args[]) {

        //String host = (args.length < 1) ? null : args[0];
        try {
        	
        	// PREPARACION PARTE REMOTA SERVIDOR 
        	// **************************************************************
        	
            Servidor obj = new Servidor();
            ChatBasicoServidor stubServidor = (ChatBasicoServidor) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            registry = LocateRegistry.getRegistry();
            registry.bind("ChatBasicoServidor", stubServidor);

            
            // COMPORTAMIENTO SERVIDOR
        	// **************************************************************
            
            System.out.println("> Servidor de chat iniciado!");
            System.out.println(" LOG de eventos:");
            System.out.println("*****************************************************************************");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        
    }


}
