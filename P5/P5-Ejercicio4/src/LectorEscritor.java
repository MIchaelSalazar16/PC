import java.util.concurrent.ThreadLocalRandom;


public class LectorEscritor extends Thread {

	private MonitorArbitraje monitorArb;
	private BaseDatos baseDatos;

	LectorEscritor(MonitorArbitraje monitor, BaseDatos BD, String nombre) {
		super(nombre);
		monitorArb = monitor;
		baseDatos = BD;
	}

	public void run() {
		try {
			while (true) {
				
				// Sorteamos el papel (LECTOR, ESCRITOR, LECTOR-ESCRITOR o ESCRITOR-LECTOR) de cada hilo en cada iteracion 
				int papel = ThreadLocalRandom.current().nextInt(0,4000);
				
				//    0 - 1000 : Lector				Permiso R
				// 1000 - 2000 : Escritor			Permiso W
				// 2000 - 3000 : Lector->Escritor	Permiso RW
				// 3000 - 4000 : Escritor->Lector	Permiso WR
				
				if (papel < 1000){
					
					monitorArb.entrarLeer();
					sleep(ThreadLocalRandom.current().nextInt(1000,2000));
					monitorArb.entrarLeer();
					
					System.out.println(getName() + " lee lo siguiente de la BD: " + baseDatos.leer() + "\t\tPermisos: R");
					
					monitorArb.salirLeer();
					sleep(ThreadLocalRandom.current().nextInt(500,5000));
					monitorArb.salirLeer();
					
				} else if (papel < 2000){
					
					monitorArb.entrarEscribir();
					sleep(ThreadLocalRandom.current().nextInt(1500,2500));
					monitorArb.entrarEscribir();
					
					baseDatos.escribir();
					System.out.println(getName() + " escribe su nombre en la BD." + "\t\tPermisos: W");
					
					monitorArb.salirEscribir();
					monitorArb.salirEscribir();
					sleep(ThreadLocalRandom.current().nextInt(500,5000));
					
				} else if (papel < 3000) {
					
					monitorArb.entrarLeer();
					sleep(ThreadLocalRandom.current().nextInt(1000,2000));
					System.out.println(getName() + " lee lo siguiente de la BD: " + baseDatos.leer() + "\t\tPermisos: RW");
					
					monitorArb.entrarEscribir();
					
					baseDatos.escribir();
					System.out.println(getName() + " escribe su nombre en la BD.");
					
					monitorArb.salirEscribir();
					
					sleep(ThreadLocalRandom.current().nextInt(500,5000));
					monitorArb.salirLeer();
					
				} else {
					
					monitorArb.entrarEscribir();
					sleep(ThreadLocalRandom.current().nextInt(1500,2500));
					
					baseDatos.escribir();
					System.out.println(getName() + " escribe su nombre en la BD." + "\t\tPermisos: WR");
					
					monitorArb.entrarLeer();
					sleep(ThreadLocalRandom.current().nextInt(1000,2000));
					
					System.out.println(getName() + " lee lo siguiente de la BD: " + baseDatos.leer() + "\t\tPermisos: WR");
					monitorArb.salirLeer();
					sleep(ThreadLocalRandom.current().nextInt(500,5000));
					
					monitorArb.salirEscribir();
					sleep(ThreadLocalRandom.current().nextInt(500,5000));
					
				}
			}
		} catch (InterruptedException e){}
	}
	
}