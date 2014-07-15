package chats;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;


public class Logger implements ChatBasicoCliente {

	private File log;
	private static PrintWriter pw;
	
	// Colas para procesar los mensajes y ordenarlos
	private PriorityBlockingQueue<Mensaje> colaLlegada;
	private PriorityQueue<Mensaje> colaRetenidos;
	
	private static int numMsgActual;
	
	public Logger() {
    	
    	colaLlegada = new PriorityBlockingQueue<Mensaje>();
    	colaRetenidos = new PriorityQueue<Mensaje>();

    	try {
    		
        	log = new File("logChat.txt");
        	
        	if (log.exists())
        		log.delete();
        	
			log.createNewFile();
			
	    	pw = new PrintWriter(log);
	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
    	
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

	private class PublicadorLogger extends Thread{
		
		public PublicadorLogger(String nick){
			
			// Limpiamos la colas
			colaLlegada.clear();
			colaRetenidos.clear();
			

            // Le pedimos el nombre de usuario
            pw.println("*****************************************************************************");
            pw.println("Bienvenido al servicio de chat!");
            pw.println("\n> Para comenzar, elige un nombre de usuario: " + nick);
   		 	
		}
		
		@Override
		public void run(){
			
			try {
				
				boolean parar = false;
				
				while(!parar){
						
					Mensaje sigMensaje = null;
					Mensaje sigRetenido;
					

					// Esperamos a que haya algun mensaje en la cola de llegados	
					sigMensaje = colaLlegada.take();
				
					if (sigMensaje.texto.equals("FIN_LOGGER")){
						pw.close();
						parar = true;
					}
					else {
						
						// Si su número es mayor que el esperado ==> Lo pone en una "cola de retenidos"
						if (sigMensaje.numMsg > numMsgActual)
							colaRetenidos.add(sigMensaje);
						
						// Si su número coincide con el esperado: == > Lo imprime por pantalla e incrementa el contador,
						else if (sigMensaje.numMsg == numMsgActual){
								pw.println(sigMensaje.texto);
								numMsgActual++;
								
								// Obtenemos el primero de los retenidos a ver si se puede 
								sigRetenido = colaRetenidos.peek();
								
								// Si su número es mayor que el esperado ==> Lo pone en una "cola de retenidos"
								while (sigRetenido != null && sigRetenido.numMsg == numMsgActual){
									pw.println(sigRetenido.texto);
									colaRetenidos.poll();
									numMsgActual++;
									
								} // while
									
						} // if
						
					}

				
				} // while
				
			} catch (InterruptedException e) {
				// Si paramos el publicador, no hacemos nada
				pw.close();
			}

		} // run()
		
		
	}
	
    public static void main(String[] args) {

    	String nick = "logger";

    	Logger logger = new Logger();
    	
        try {
        	
        	// PREPARACION PARTE REMOTA CLIENTE
        	// **************************************************************
        	
        	// "Conexion" con metodos remotos del servidor
        	Registry registry = LocateRegistry.getRegistry();//(host);
            ChatBasicoServidor stubServidor = (ChatBasicoServidor) registry.lookup("ChatBasicoServidor");
            
            // PREPARACION PARTE REMOTA SERVIDOR
	        // **************************************************************
        	
            // Preparamos el cliente para ejecutar metodos remotos desde el servidor

            ChatBasicoCliente stubCliente = (ChatBasicoCliente) UnicastRemoteObject.exportObject(logger,0);
        		
            // Nos registramos en el chat
            registry.bind(nick, stubCliente);
            numMsgActual = stubServidor.darseDeAlta(nick);    
            
            // Avanzamos el numero del mensaje actual porque del que devuelve
            // el servidor (el ultimo recibido), este cliente no ha recibido la señal de "entregarMensaje()"
            numMsgActual++;
            
            System.out.println("> Iniciando el publicador de fichero...");
            // Iniciar publicador
            Thread hilo = logger.new PublicadorLogger(nick);
            hilo.start();
            
            // Imprimimos la cabecera
            pw.println("> Ya estas dentro de la sala!");
            pw.println("> Para salir, escribe el mensaje '#EXIT'");
            pw.println("*****************************************************************************");
            
            System.out.println("> Grabando registro en fichero...");
            
            //Thread.sleep(30000);
            System.out.println("\n> Para finalizar, pulsa 'Ctrl+C'");

        
        
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            pw.close();
            try {
				System.in.read();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        
        
    }


	@Override
	public void entregarMensaje(int numMensaje, String mensaje) throws RemoteException {

		// Cuando llega un mensaje se encola para que el publicador lo imprima
		colaLlegada.add(new Mensaje(numMensaje, mensaje));

	}
	
}
