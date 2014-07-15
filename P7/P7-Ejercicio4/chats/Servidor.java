
package chats;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Tarea que representa la entrega de un mensaje a un usuario en concreto
class EntregarMensajeAction implements Runnable {

	private String nick;
	private int numMsg;
	private String mensaje;
	private Registry registry;
	
	public EntregarMensajeAction(String nick, int numMsg, String mensaje, Registry registry){
		
		this.nick = nick;
		this.numMsg = numMsg;
		this.mensaje = mensaje;
		this.registry = registry;
	}

	@Override
	public void run() {
		
		// Entregar el mensaje
		try {
			ChatBasicoCliente stubCliente = (ChatBasicoCliente) registry.lookup(nick);
			stubCliente.entregarMensaje(numMsg, mensaje);
		} catch (RemoteException | NotBoundException e) {
			// NO HACEMOS NADA SI SE INTENTA ENTREGAR UN MENSAJE A UN USUARIO QUE SE FUE
		}
		
	}
	
}

public class Servidor implements ChatBasicoServidor {

	// Necesario añaadir para el correcto funcionamiento
	public static Registry registry;
	
	// HashMap que almacena los usuarios activos por su nick.
	//private static HashSet<String> usuariosActivos;
	private static CopyOnWriteArraySet<String> usuariosActivos;
	
	// Indica el orden de los mensajes recibidos
	private AtomicInteger numMensaje;
	
    public Servidor() {
    	
    	usuariosActivos = new CopyOnWriteArraySet<>();
    	numMensaje = new AtomicInteger(0);

    }

	@Override
	public int darseDeAlta(String nick) throws RemoteException {
		
		// Si el nick no se encuentra ya registrado
		if (!usuariosActivos.contains(nick)){
	        
			// Lo añadimos a los usuario activos (registrar en la sala)
			usuariosActivos.add(nick);
			
			// Devolveremos el numero del ultimo mensaje recibido por el servidor
			// para inicializar el publicador del cliente
			int numMsg = numMensaje.get();
			
			// Imprimir en log del servidor
	        System.out.println("# '"+ nick + "' se registra en la sala (en el mensaje: " + numMsg + ")");
	        
			return numMsg;
		}

		else
			return -1;
		
	}

	@Override
	public boolean darseDeBaja(String nick) throws RemoteException {
		
		boolean exito;
		
		// Le quitamos de la lista de usuarios activos
		exito = usuariosActivos.remove(nick);
		
		// Le quitamos del registro RMI
		try {
			registry.unbind(nick);
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
		// Imprimimos por el log del servidor
		if (exito)
			System.out.println("# '"+ nick + "' abandona la sala");
		else
			System.out.println("! Error dando de baja al usuario: " + nick);
		
		return exito;
	}

	
	public boolean difundir(String nick, String mensaje) throws RemoteException {
		
		// Lo mostramos en el log
		if (usuariosActivos.contains(nick)){
			
			// Formateamos el mensaje para su publicacion
			int nMsg = numMensaje.incrementAndGet();
			String msgFormateado = "@ " + nick + " escribe: [" + nMsg + "]\t\t" + mensaje;
			
			// Lo imprimimos en la consola del servidor
			System.out.println(msgFormateado);
			
			// Creamos un pool de hilos con tantos como usuarios haya que entregar mensajes
			ExecutorService pool = Executors.newFixedThreadPool(usuariosActivos.size());

			// Creamos las tareas que se pasaran al pool (Una por cada usuario activo al que entregar el mensaje)
			for (String s : usuariosActivos) {
				
				EntregarMensajeAction ema = new EntregarMensajeAction(s, nMsg,  msgFormateado, registry);
				
				// Presentamos el envio al pool
	            try {
		        	pool.execute(ema);
	              	}
	            catch (Exception ex) {
	                pool.shutdown();
	              }
	            
			}
			
			// Impedimos que lleguen ya nuevas tareas
	        pool.shutdown(); 		      

	        // Espere que se termine el ExecutorService (o bien que pasen unos segundos)
	        try {
				pool.awaitTermination(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// (Re-)Cancel if current thread also interrupted
		          pool.shutdownNow();
		          // Preserve interrupt status
		          Thread.currentThread().interrupt();
		        }
	        
	      // Aunque no es necesario en este ejemplo ya que no habrá tareas que tarden demasiado
	      // en terminarse, ahora intente cerrar el ExecutorService de manera brusca,
	      // es decir, interrumpiendo tareas activas.
	
	        pool.shutdownNow();
			
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
        	
        	// Creamos el objeto servidor y lo exportamos a un stub
            Servidor obj = new Servidor();
            ChatBasicoServidor stubServidor = (ChatBasicoServidor) UnicastRemoteObject.exportObject(obj, 0);

            // Registramos el stub del servidor en el registro RMI
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
