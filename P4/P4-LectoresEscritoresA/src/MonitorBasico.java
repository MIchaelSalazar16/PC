public class MonitorBasico implements MonitorArbitraje {

	private int numLectores = 0;
	private boolean escritor = false; 
	private long startTime;

	MonitorBasico() {
		startTime = System.currentTimeMillis();
	}

	public synchronized void entrarLeer() throws InterruptedException {
		
		// Lector puede entrar si NO hay escritor dentro
		while (escritor) 
			this.wait();
		
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
		if (numLectores == 0)
			this.notify();
		
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		
		// Un escritor puede entrar si NO quedan lectores dentro
		while (numLectores != 0 || escritor)
			this.wait();
		
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
		
		// Al salir el escritor, hay que avisar tanto a lectores como escritores en espera
		this.notifyAll();
		
	}
	
}
