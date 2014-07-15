public class MonitorFIFO implements MonitorArbitraje {

	private int numLectores = 0;
	private boolean escritor = false; 
	private long startTime;
	// Nueva variable para el apartado 2
	//private int escritoresEnEspera = 0;
	// Nueva variable del apartado 3
	//private boolean turnoLectores = false;
	// Nueva variable del apartado 4
	private Secuenciador controlTurno;
	
	MonitorFIFO() {
		startTime = System.currentTimeMillis();
		controlTurno = new Secuenciador();
	}

	public synchronized void entrarLeer() throws InterruptedException {
		
		// Cogemos un ticket de turno
		int turno = controlTurno.getTicket(Thread.currentThread().getName());
		
		// Lector puede entrar si NO hay escritor dentro y es su turno
		while (escritor || turno != controlTurno.currentTurn())
			this.wait();
		
		// Entra lector
		numLectores++;
		
		// Pasa el turno al siguiente (por si hay mas lectores, que el acceso siga siendo concurrente)
		controlTurno.advance();	

		System.out.println( (System.currentTimeMillis()-startTime) + ": " 
			+ Thread.currentThread().getName() + " va a empezar a leer");
		
		//FIXME hace falta un notifyAll para aceptar mas lectores en espera.
	} 

	public synchronized void salirLeer() {
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		// Sale lector
		numLectores--;
		
		// Hay que avisar a todos porque no sabemos de quien es el siguiente turno
		this.notifyAll();		//	this.notify();
		
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		
		// Cogemos un ticket de turno
		int turno = controlTurno.getTicket(Thread.currentThread().getName());
		
		// Un escritor puede entrar si NO quedan lectores dentro, NO hay otro escritor y es su turno
		while(numLectores != 0 || escritor || turno != controlTurno.currentTurn())
			this.wait();

		// Entra escritor
		escritor =  true;
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": " 
			+ Thread.currentThread().getName()  + " va a empezar a escribir");
	}

	public synchronized void salirEscribir() {
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de escribir");
		
		// Sale escritor
		escritor = false;
		
		// Pasa el turno al siguiente 
		controlTurno.advance();
		
		// Al salir el escritor, hay que avisar tanto a lectores como escritores en espera
		this.notifyAll();
		
	}
	
}
