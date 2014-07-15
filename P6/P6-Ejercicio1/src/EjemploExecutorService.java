import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EjemploExecutorService {

  // El main:
	
    public static void main(String[] argv) {
    	
        final int numTareas = 1000;
        final int numMaxHilos = 50;
        
        ExecutorService pool;
        
        int availableCores = Runtime.getRuntime().availableProcessors();
        
    	System.out.println("\nNumero de procesadores disponibles: " + availableCores);
	
    	// Fichero de salida de datos
    	File ficheroDatos = new File("miFichero" + availableCores + ".datos");
    	FileOutputStream ficheroOut = null;

    	try {
    		ficheroOut = new FileOutputStream(ficheroDatos);	
    	} catch (FileNotFoundException e1) {
    		//....
    	}

    	PrintWriter pw = new PrintWriter(ficheroOut);	
    	
	    System.out.println("Numero de hilos \tTiempo en ns");
	    
 
	    // COMENZAR BUCLE. Para un número de hilos entre 1 y numMaxHilos:
	    for (int hilos = 1; hilos < numMaxHilos; hilos++){	
	
	    	final long startTime = System.nanoTime();
	    	
	      // Utilice un método factoría para crear un ThreadPool (implementación
	      // de la interfaz ExecutorService) con el número de hilos requerido.
	        pool = Executors.newFixedThreadPool(hilos);

	      // COMENZAR BUCLE. Para un número de tareas entre 1 y numTareas:
	        for (int tareas = 1; tareas < numTareas; tareas++){
	        	
	         // Crear un nuevo objeto de la clase TareaDeCalculo (con p.e. maxCont == 1000000)
	        	TareaDeCalculo tdc = new TareaDeCalculo(1000000);

		     // Presente esta tarea al ExecutorService para su ejecución		
	            try {
		        	pool.execute(tdc);
	              	}
	            catch (Exception ex) {
	                pool.shutdown();
	              }
	        	
	      // TERMINAR BUCLE
	        }
	
	      // Intente cerrar el ExecutorService de manera ordenada, es decir, dejando las
	      // tareas activas terminar.	
	        
	        pool.shutdown(); 		// Disable new tasks from being submitted	      

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
	        
	        try {
				if (!pool.awaitTermination(20, TimeUnit.SECONDS))
				    System.err.println("Pool did not terminate");
			} catch (InterruptedException e) {
				// (Re-)Cancel if current thread also interrupted
		          pool.shutdownNow();
		          // Preserve interrupt status
		          Thread.currentThread().interrupt();
		        }

	      // Salida estandar 
	      System.out.println(hilos + "\t\t\t" + (System.nanoTime() - startTime));
	      
	      // Salida fichero
	      pw.println(hilos + "\t" + (System.nanoTime() - startTime));
	      
	    // TERMINAR BUCLE.
	    }
	    
	    // Cerramos el fichero
		pw.close();
		
		try {
			ficheroOut.close();
		} catch (Exception e){
			//...
		}
	}
}
 