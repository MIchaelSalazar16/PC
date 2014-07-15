import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
public class EjemploCompletionService {
 
    // Defina un método estático sin argumentos llamado crearListaTareas
    // que cree una lista de, por ejemplo, diez TareasLargas.
	static Collection<TareaLarga> tareas = new ArrayList<TareaLarga>();
	
	static void crearListaTareas(){
		
		for (int i = 0; i < 10 ; i++)
			tareas.add(new TareaLarga());
		
	}

    // El main:
	
    public static void main(String[] argv) {
    	
      // Utilice un método factoría para crear un ThreadPool (implementación
      // de la interfaz ExecutorService) con el numero de hilos requerido:
      // por ejemplo, diez. 
    	ExecutorService pool = Executors.newFixedThreadPool(10);
    	
      // Cree un nuevo CompletionService para tareas que devuelvan un String,
      // pasándole el ExecutorService creado en la instrucción anterior
        CompletionService<String> cs = new ExecutorCompletionService<String>(pool);
        
      // Cree una lista de TareaLarga con el método crearListaTareas
        crearListaTareas();

      // COMENZAR BUCLE (DE TIPO FOR-EACH). Para cada tarea de la lista
        for (TareaLarga tl : tareas) {
          
        	// Presente esta tarea al CompletionService para su ejecución
        	cs.submit(tl);
       
      // TERMINAR BUCLE.
        }
        
        
      // COMENZAR BUCLE. Para un número de veces = el tamaño de la lista de tareas
        for (int i = 0; i < tareas.size(); i++){
        	
          // Pida una tarea completada al CompletionService
          // (espere si no ha terminado ninguna tarea todavía)
	        String resultado = "¿?";
	        
			try {
				resultado = cs.take().get();
			} catch (InterruptedException | ExecutionException e) {
				System.err.println(" ! : Error durante la espera y recolección de resultado");
			}

          // Imprima el resultado de la tarea en la salida estándar.
	         System.out.println("[" + (i+1) + "] Tarea: " + resultado + " finalizó una tarea!");
	         
      // TERMINAR BUCLE.
        }

      // Cierre el ExecutionService
        pool.shutdownNow();
        
    }
}