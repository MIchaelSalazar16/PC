package chats;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Scanner;

public class Cliente implements ChatBasicoCliente {

	public LinkedList<String> colaMensajes;
	public final int maxMensajes = 15;	
	
	public String nickname;
	
	private Cliente() {
    	
    	colaMensajes = new LinkedList<String>();
    	nickname = "";
    }

    public static void main(String[] args) {

    	Cliente cliente = new Cliente();
    	
       // String host = (args.length < 1) ? null : args[0];
        
        try {
        	
        	// PREPARACION PARTE REMOTA CLIENTE
        	// **************************************************************
        	
        	// "Conexion" con metodos remotos del servidor
        	Registry registry = LocateRegistry.getRegistry();//(host);
            ChatBasicoServidor stubServidor = (ChatBasicoServidor) registry.lookup("ChatBasicoServidor");
            
        	// CODIGO DEL CLIENTE
        	// **************************************************************
        	
            // Le pedimos el nombre de usuario
            System.out.println("*****************************************************************************");
            System.out.println("Bienvenido al servicio de chat!");
            System.out.print("\n> Para comenzar, elige un nombre de usuario: ");
            
            String nick = null;
        	Scanner scanner = new Scanner(System.in);
        	
        	boolean registrado = false;
            while (!registrado){

            	nick = scanner.next("([0-9]|[A-Z]|[a-z])*");
            	
            	if (nick == null){
            		System.out.print("> Error!\n  Nombre de usuario no valido. Solo se admiten caracteres alfanumericos sin espacios");
            		System.out.print(" > Elige un nombre de usuario: ");
            	} 
            	else {
            		
           		 	registrado = stubServidor.darseDeAlta(nick);
           		 	
           		 	if (!registrado){
                		System.out.print("> Error!\n  El nombre de usuario ya esta en uso. Por favor, seleccione otro...");
                		System.out.print(" > Elige un nombre de usuario distinto: ");
           		 	}
            	}
            		 
            }
                     
	        // PREPARACION PARTE REMOTA SERVIDOR
	        // **************************************************************
	            
            // Preparamos el cliente para ejecutar metodos remotos desde el servidor
            ChatBasicoCliente stubCliente = (ChatBasicoCliente) UnicastRemoteObject.exportObject(cliente,0);
            registry.bind(nick, stubCliente);
            
            // Imprimimos la cabecera
            System.out.println("> Ya estas dentro de la sala!");
            System.out.println("> Para salir, escribe el mensaje '#EXIT'");
            System.out.println("*****************************************************************************");
            
            // Procesamos los mensajes enviados a la sala
            String mensaje = "";
            scanner = new Scanner(System.in);
            System.out.print("\n MSG > ");
            while (!mensaje.equals("#EXIT")){
            	//System.out.print("\n MSG > ");
            	mensaje = scanner.nextLine();
            	
            	if (!mensaje.equals("#EXIT"))
            		stubServidor.difundir(nick, mensaje);
            	
            }
            
            // Si introdujo #EXIT le sacamos de la sala
            System.out.println("*****************************************************************************");
            stubServidor.darseDeBaja(nick);
            registry.unbind(nick);
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        System.exit(0);
        
    }


	@Override
	public void entregarMensaje(String nick, String mensaje) throws RemoteException {

		//System.out.println("\n\n# " + nick + " escribe: " + mensaje);
		
		String msgFormateado = " # " + nick + " escribe: " + mensaje;
		
		colaMensajes.addLast(msgFormateado);
		
		if (colaMensajes.size() > 15)
			colaMensajes.removeFirst();
		
		System.out.println();
		
		for (int i = 0; i < colaMensajes.size(); i++)
			System.out.println(colaMensajes.get(i));

		//if (nick.equals(nickname))
			System.out.print("\n MSG > ");
		
	}
	
}
