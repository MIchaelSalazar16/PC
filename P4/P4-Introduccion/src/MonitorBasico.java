public class MonitorBasico implements MonitorArbitraje {

	private int numLectores = 0;
	private boolean escritor = false; 
	private long startTime;

	MonitorBasico() {
		startTime = System.currentTimeMillis();
	}

	public synchronized void entrarLeer() throws InterruptedException {
		// <su codigo>
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer");
	}

	public synchronized void salirLeer() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		// <su codigo>
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		// <su codigo>
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName()  + " va a empezar a escribir");
	}

	public synchronized void salirEscribir() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de escribir");
		// <su codigo>
	}
	
}
