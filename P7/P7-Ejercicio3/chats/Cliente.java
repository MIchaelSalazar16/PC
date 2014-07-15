package chats;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Random;

public class Cliente implements ChatBasicoCliente {
	
	private final static int maxMensajes = 10;	
	private final static int maxVueltas = 10;
	
	private Cliente() {}

    public static void main(String[] args) {

    	String nick = "usuario_" + args[0];

    	Cliente cliente = new Cliente();

        try {
        	
        	// PREPARACION PARTE REMOTA CLIENTE
        	// **************************************************************
        	
        	// "Conexion" con metodos remotos del servidor
        	Registry registry = LocateRegistry.getRegistry();//(host);
            ChatBasicoServidor stubServidor = (ChatBasicoServidor) registry.lookup("ChatBasicoServidor");
            
            // PREPARACION PARTE REMOTA SERVIDOR
	        // **************************************************************
        	
            // Preparamos el cliente para ejecutar metodos remotos desde el servidor

            ChatBasicoCliente stubCliente = (ChatBasicoCliente) UnicastRemoteObject.exportObject(cliente,0);
            
            for (int rep = 0; rep < maxVueltas; rep++){
            	
	            // Le pedimos el nombre de usuario
	            System.out.println("*****************************************************************************");
	            System.out.println("Bienvenido al servicio de chat!");
	            System.out.print("\n> Para comenzar, elige un nombre de usuario: " + nick);
	   		 	
				registry.bind(nick, stubCliente);
	            stubServidor.darseDeAlta(nick);    

	            // Imprimimos la cabecera
	            System.out.println("> Ya estas dentro de la sala!");
	            System.out.println("> Para salir, escribe el mensaje '#EXIT'");
	            System.out.println("*****************************************************************************");
	            
	            // Procesamos los mensajes enviados a la sala
	            
	            int maxMensajes = 10;
	            
	            for (int numMensajes = 1; numMensajes <= maxMensajes; numMensajes++){
	            	
	            	Random r = new Random();
	            	Thread.sleep(r.nextInt(100));
	            	
	            	String mensaje = "Mensaje " + numMensajes + "/" + maxMensajes + " \t\t Vuelta: " + (rep+1) + "/10";
	            	stubServidor.difundir(nick, mensaje);
	            	
	            }
	            
	            // Si introdujo #EXIT le sacamos de la sala
	            System.out.println("*****************************************************************************");
	            stubServidor.darseDeBaja(nick);
	            registry.unbind(nick);
	            
	            // Esperamos antes de volver a entrar
	            Random r = new Random();
            	Thread.sleep(r.nextInt(100));
            }
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        
       System.exit(0);
        
    }


	@Override
	public void entregarMensaje(String mensaje) throws RemoteException {

		System.out.println(mensaje);
		
	}
	
}
