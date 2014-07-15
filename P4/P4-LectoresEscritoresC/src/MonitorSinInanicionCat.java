public class MonitorSinInanicionCat implements MonitorArbitraje {

	private int numLectores = 0;
	private boolean escritor = false; 
	private long startTime;
	
	//Nueva variable para el apartado 2
	private int escritoresEnEspera = 0;
	
	//Nueva variable del apartado 3
	private boolean turnoLectores = false;

	MonitorSinInanicionCat() {
		startTime = System.currentTimeMillis();
	}

	public synchronized void entrarLeer() throws InterruptedException {

		// Lector puede entrar si NO hay escritor dentro y es el turno de los lectores	
		while (escritor || escritoresEnEspera!=0) {	
			
			// Activamos el turno de los lectores si hay alguno esperando
			turnoLectores = true;
			
			this.wait();	
		}
		
		// Lector ha entrado
		numLectores++;

		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer");
	} 

	public synchronized void salirLeer() {
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		// Sale lector
		numLectores--;

		// El ultimo lector tiene que avisar a un escritor que este esperando 	
		if (numLectores == 0){	
			// Pasa el turno a los escritores
			turnoLectores = false;
			this.notifyAll();
		}
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		
		// Un escritor puede entrar si NO quedan lectores dentro, 
		// NO hay escritores y NO es el turo de los lectores

		while (numLectores != 0 || escritor || turnoLectores){
			
			// Control del aforo de los escritores en espera
			escritoresEnEspera++;
			this.wait();
			escritoresEnEspera--;
			
			// Devolvemos turno a los escritores
			turnoLectores = false;
			
		}
		
		// Escritor ha entrado
		escritor =  true;
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName()  + " va a empezar a escribir");
	}

	public synchronized void salirEscribir() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de escribir");

		// Sale escritor
		escritor = false;
		
		// Pasa el turno a los lectores
		turnoLectores = true;
		
		// Al salir el escritor, hay que avisar tanto a lectores como escritores en espera
		this.notifyAll();
		
	}
	
}
