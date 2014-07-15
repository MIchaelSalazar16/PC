import java.util.HashMap;

class Value{
	protected Thread hiloLector;
	protected int numAccesos;
	
	Value(int n, Thread h){
		hiloLector = h;
		numAccesos = n;
	}
}

public class ReentranteEscritura implements MonitorArbitraje {

	//private int numLectores = 0;

	private boolean escritor = false; 
	private long startTime;
	private int escritoresEnEspera = 0;

	// Variable para el ejercicio 1
	private HashMap<String,Value> numLectores;
	
	// Variables para el ejercicio 2
	private Thread escritorActual = null;
	private int accesosEscritura = 0;

	ReentranteEscritura() {
		startTime = System.currentTimeMillis();
		numLectores = new HashMap<String,Value>();
	}

	public synchronized void entrarLeer() throws InterruptedException {
		
		Value valorEntrada = null;
		String nombreLector = Thread.currentThread().getName();
		
		// Si el lector ya tiene acceso a escritura, vuelve a entrar (independiente de que haya escritores en espera)
		if (!numLectores.containsKey(nombreLector)){
			
			// Lector puede entrar si NO hay escritor dentro
			while (escritor || escritoresEnEspera > 0) 
				this.wait();
			
			// Entra por primera vez
			valorEntrada = new Value(0,Thread.currentThread());
			
		} else {
			// Es una reentrada: Actualizamos contador de entradas
			Value v = numLectores.get(nombreLector);
			valorEntrada = new Value(v.numAccesos + 1, Thread.currentThread());
			
			System.out.println("\t\t\t\tReentrada num. "+ (v.numAccesos + 1) + " de "+ nombreLector );
		}
			
		// Lector ha entrado
		numLectores.put(nombreLector, valorEntrada );	// numLectores++;
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer");
		
	} 

	public synchronized void salirLeer() {
		
		String nombreLector = Thread.currentThread().getName();
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		// Lector sale
		Value v = numLectores.get(nombreLector);
		
		// Si es la ultima salida de sus reentradas => Eliminar del HashMap, sino, actualizar contador
		if (v.numAccesos == 0)
			numLectores.remove(nombreLector);			// numLectores--;
		else {
			numLectores.put(nombreLector, new Value(v.numAccesos - 1,Thread.currentThread()));
		}
		
		// El ultimo lector tiene que avisar a un escritor que este esperando 
		if (numLectores.isEmpty())			// if (numLectores == 0)
			this.notifyAll();
		
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		
		// Si el escritor ya tiene acceso a escritura, entonces puede reentrar
		if (escritorActual != Thread.currentThread()) {
			
			// Un escritor puede entrar si NO quedan lectores dentro y NO esta el otro escritor
			while (!numLectores.isEmpty() || escritor){				// while (numLectores != 0 || escritor){
				//Control del aforo de los escritores en espera
				escritoresEnEspera++;
				this.wait();
				escritoresEnEspera--;
			}
			
			// Escritor ha entrado
			escritorActual = Thread.currentThread();
			escritor = true;
			
		} else 
			System.out.println("\t\t\t\tReentrada num. "+ (accesosEscritura + 1) + " de "+ escritorActual.getName() );
		


		accesosEscritura++;
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName()  + " va a empezar a escribir");
		
	}

	public synchronized void salirEscribir() {
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de escribir");
		
		accesosEscritura--;
		
		// Al salir el escritor, hay que avisar tanto a lectores como escritores en espera
		if (accesosEscritura == 0){
			// Sale escritor
			escritor = false;
			escritorActual = null;
			this.notifyAll();
		}
			
		
	}
}

