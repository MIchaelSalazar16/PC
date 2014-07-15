package chats;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;


public class Cliente implements ChatBasicoCliente {

	private final static int maxMensajes = 10;	
	private final static int maxVueltas = 10;
	
	// Colas para procesar los mensajes y ordenarlos
	private PriorityBlockingQueue<Mensaje> colaLlegada;
	private PriorityQueue<Mensaje> colaRetenidos;
	
	private static int numMsgActual;
	
	public Cliente() {
    	
    	colaLlegada = new PriorityBlockingQueue<Mensaje>();
    	colaRetenidos = new PriorityQueue<Mensaje>();

    }
	
	// Implementamos Comparator y Comparable para que las colas de prioridad
	// sepan ordenar los mensajes 
	private class Mensaje implements Comparator<Mensaje>, Comparable<Mensaje> {

		private int numMsg;
		private String texto;
		
		public Mensaje(int numMsg, String texto){
			
			this.numMsg = numMsg;
			this.texto = texto;
			
		}
		
		@Override
		public int compareTo(Mensaje m1) {
			
			return this.numMsg - m1.numMsg;
			
		}

		@Override
		public int compare(Mensaje m1, Mensaje m2) {
			
			return m1.numMsg - m2.numMsg;
			
		}
		

	}

	private class PublicadorCliente extends Thread{
		
		public PublicadorCliente(String nick){
			
			// Limpiamos la colas
			colaLlegada.clear();
			colaRetenidos.clear();
			
            // Le pedimos el nombre de usuario
            System.out.println("*****************************************************************************");
            System.out.println("Bienvenido al servicio de chat!");
            System.out.println("\n> Para comenzar, elige un nombre de usuario: " + nick);
   		 	
		}
		
		@Override
		public void run(){
			
			try {
				
				while(true){
						
					Mensaje sigMensaje = null;
					Mensaje sigRetenido;
					
					// Esperamos a que haya algun mensaje en la cola de llegados	
					sigMensaje = colaLlegada.take();
				
					
					// Si su número es mayor que el esperado ==> Lo pone en una "cola de retenidos"
					if (sigMensaje.numMsg > numMsgActual)
						colaRetenidos.add(sigMensaje);
					
					// Si su número coincide con el esperado: == > Lo imprime por pantalla e incrementa el contador,
					else if (sigMensaje.numMsg == numMsgActual){
							System.out.println(sigMensaje.texto);
							numMsgActual++;
							
							// Obtenemos el primero de los retenidos a ver si se puede 
							sigRetenido = colaRetenidos.peek();
							
							// Si su número es mayor que el esperado ==> Lo pone en una "cola de retenidos"
							while (sigRetenido != null && sigRetenido.numMsg == numMsgActual){
								System.out.println(sigRetenido.texto);
								colaRetenidos.poll();
								numMsgActual++;
								
							} // while
								
					} // if
				
				} // while
				
			} catch (InterruptedException e) {
				// Si paramos el publicador, no hacemos nada
			}

		} // run()
		
		
	}
	
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
        		
	            // Nos registramos en el chat
	            registry.bind(nick, stubCliente);
	            numMsgActual = stubServidor.darseDeAlta(nick);    
	            
	            // Avanzamos el numero del mensaje actual porque del que devuelve
	            // el servidor (el ultimo recibido), este cliente no ha recibido la señal de "entregarMensaje()"
	            numMsgActual++;
	            
	            // Iniciar publicador
	            Thread hilo = cliente.new PublicadorCliente(nick);
	            hilo.start();
	            
	            // Imprimimos la cabecera
	            System.out.println("> Ya estas dentro de la sala!");
	            System.out.println("> Para salir, escribe el mensaje '#EXIT'");
	            System.out.println("*****************************************************************************");
	            
	            
	            // Creamos y enviamos mensajes a la sala   
	            for (int numMensajes = 1; numMensajes <= maxMensajes; numMensajes++){
	            	
	            	Random r = new Random();
	            	Thread.sleep(r.nextInt(100));
	            	
	            	String mensaje = "Mensaje " + numMensajes + "/" + maxMensajes + " \t\t Vuelta: " + (rep+1) + "/10";
	            	stubServidor.difundir(nick, mensaje);
	            	
	            }
	            
	            // Una vez ha enviado los mensajes, sale de la sala
	            stubServidor.darseDeBaja(nick);
	            
	            // Paramos el publicador
	            hilo.interrupt();

	            System.out.println("*****************************************************************************");
	         
	            // Esperamos antes de volver a entrar
	            Random r = new Random();
            	Thread.sleep(r.nextInt(100));
            }
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();

        }
        
       //System.exit(0);
        
    }


	@Override
	public void entregarMensaje(int numMensaje, String mensaje) throws RemoteException {

		// Cuando llega un mensaje se encola para que el publicador lo imprima
		colaLlegada.add(new Mensaje(numMensaje, mensaje));

	}
	
}
