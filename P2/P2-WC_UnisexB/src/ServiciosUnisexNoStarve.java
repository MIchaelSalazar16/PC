import java.util.Random;
import java.util.concurrent.Semaphore;

// Enumerado para indicar el sexo de las personas
enum Sexo {
		MAS {
			@Override
			public String toString(){ 
				return "HOMBRE";
			}
		},
		FEM {
			@Override
			public String toString(){ 
				return "MUJER";
				}
			}
		};


class Persona extends Thread {
	
	// ATRIBUTOS
	// ****************************************************************************************
  	
	// Random Number Generator
	static Random rnd = new Random();
	
	// Número que sirve de indentificador de la persona
	final int id_persona;
	
	// Sexo de la persona (No puede haber hombres a la vez que mujeres en el WC)
	final Sexo sexo;

	// Apunta al Pecusa de Hombres o de Mujeres segun el sexo
	Pecusa miPecusa;
	
	// Semaforo para controlar que el aforo del WC no se sobrepasa en 4 personas
	static Semaphore aforo;
	
	// Contador para mostar el aforo del baño y semaforo protector del mismo
	static Semaphore protector;
	static int numPersonas;

	// CONSTRUCTORA
	// ****************************************************************************************
	Persona(int num,
			Sexo s, 
			Pecusa miPecusa,
			Semaphore aforo,
			int numPersonas,
			Semaphore protector){
		
		id_persona = num;
		sexo = s;
		
		this.miPecusa = miPecusa;
		
		Persona.aforo = aforo;
		Persona.numPersonas = numPersonas;
		Persona.protector = protector;
		
	}
	
	// METODOS
	// ****************************************************************************************
	
	// Modela la accion del uso de los servicios
	protected void utilizarServicios(){
		try {
			sleep(rnd.nextInt(150));
		} catch (InterruptedException e) {
			System.err.println("Interrupted while sleeping");
		}
	}
	
	// Modela la accion de trabajar en la oficina
	protected void trabajar(){
		try {
			sleep(rnd.nextInt(1000));
		} catch (InterruptedException e) {
			System.err.println("Interrupted while sleeping");
		}
	}

	public void run(){
		while(true){
			
			trabajar();
			
			// Cortar el paso al otro sexo si es la primera persona que entra
			miPecusa.cerrar_al_otro_genero_si_primero();
			
		  		// Comprobamos que el WC no esta completo / coger turno para entrar
				try {aforo.acquire();} catch(Exception e) {};	
			  
					// Acceso atomico a la variable contador del aforo
					try {protector.acquire();} catch(Exception e) {};	
					
						// Persona entra en el baño
						numPersonas++;
			
						// Notificar acceso de un hombre al WC por pantalla
			  		  	System.out.println("P @ WC: " +  numPersonas + "\t " + 
			  		  						sexo.toString() + " " + id_persona + " entra al servicio...");
			  		  	
		  		  	// Fin seccion critica
		  		  	try {protector.release();} catch(Exception e) {};	
			  		  	
		  		  	// Usar el WC
		  		  	utilizarServicios();
		  		  	
		  		  	// Acceso atomico a la variable contador del aforo
					try {protector.acquire();} catch(Exception e) {};	
					  
						// Persona sale del baño
						numPersonas--;
				  
						// Notificar salida del WC por pantalla
				  		System.out.println("P @ WC: " +  numPersonas + "\t " + 
				  				  			sexo.toString() + " " + id_persona + " tiró de la cadena y salió del servicio!");
				  	
				  	// Fin seccion critica contado aforo
					try {protector.release();} catch(Exception e) {};	

		  		// Actualiza el aforo para avisar si habia otro hombre esperando
		  		try {aforo.release();} catch(Exception e) {};	
	  		  
	  		// Si es el ultimo hombre/mujer en salir, avisar al otro sexo para entrar
	  		miPecusa.abrir_al_otro_genero_si_ultimo();
  		  	
		} // while
	}
}

class PersonaNoStarve extends Persona{

	// ATRIBUTOS
	// ****************************************************************************************
	
	// Es el encargado de controlar que el turno cambia cuando llega alguien del otro genero
	static Semaphore turno;
	
	// CONSTRUCTORA
	// ****************************************************************************************
	
	PersonaNoStarve(int num,
			Sexo s, 
			Pecusa miPecusa,
			Semaphore aforo,
			int numPersonas,
			Semaphore protector,
			Semaphore torniquete){
		
		super(num,s,miPecusa,aforo,numPersonas,protector);
		turno = torniquete;
		
	}
	
	// METODOS
	// ****************************************************************************************
	
	// Sobrecargamos el metodo run() para que funcione el torniquete de turno
	@Override
	public void run(){
		while(true){
			
			trabajar();
			
	  		// Reclamar el turno (torniquete) por si hay gente del otro sexo que no acapare el baño
			try {turno.acquire();} catch(Exception e) {};
			
				// Cortar el paso al otro sexo si es la primera persona que entra
				miPecusa.cerrar_al_otro_genero_si_primero();
	  		
			// Devolver torniquete
			try {turno.release();} catch(Exception e) {};
			
		  		// Comprobamos que el WC no esta completo / coger turno para entrar
				try {aforo.acquire();} catch(Exception e) {};	
			  
					// Acceso atomico a la variable contador del aforo
					try {protector.acquire();} catch(Exception e) {};	
					
						// Persona entra en el baño
						numPersonas++;
			
						// Notificar acceso de un hombre al WC por pantalla
			  		  	System.out.println("P @ WC: " +  numPersonas + "\t " + 
			  		  						sexo.toString() + " " + id_persona + " entra al servicio...");
			  		  	
		  		  	// Fin seccion critica
		  		  	try {protector.release();} catch(Exception e) {};	
			  		  	
		  		  	// Usar el WC
		  		  	utilizarServicios();
		  		  	
		  		  	// Acceso atomico a la variable contador del aforo
					try {protector.acquire();} catch(Exception e) {};	
					  
						// Persona sale del baño
						numPersonas--;
				  
						// Notificar salida del WC por pantalla
				  		System.out.println("P @ WC: " +  numPersonas + "\t " + 
				  				  			sexo.toString() + " " + id_persona + " tiró de la cadena y salió del servicio!");
				  	
				  	// Fin seccion critica contado aforo
					try {protector.release();} catch(Exception e) {};	

		  		// Actualiza el aforo para avisar si habia otro hombre esperando
		  		try {aforo.release();} catch(Exception e) {};	
	  		  
	  		// Si es el ultimo hombre/mujer en salir, avisar al otro sexo para entrar
	  		miPecusa.abrir_al_otro_genero_si_ultimo();
  		  	
		} // while
	}
}
	



class ServiciosUnisex {
	
	// Es el control del recurso compartido: El WC (sin fairness)
	static final Semaphore accesoWC = new Semaphore(1,false);
	
	// PECUSA que controla el acceso múltiple al WC de hombres y mujeres
	static final Pecusa accesoMujeres = new Pecusa(accesoWC);;
	static final Pecusa accesoHombres = new Pecusa(accesoWC);;
	
	// Es el control de aforo: No puede haber mas de 4 personas (sin fairness)
	static final Semaphore controlAforo = new Semaphore(4,false);
	
	// Es la variable que muesta el aforo actual y su semaforo protector
	static int aforoActual = 0;
	static final Semaphore protector = new Semaphore(1,false);
	
	ServiciosUnisex(){
		
		for(int i=0; i<7; i++){
			new Persona(i, Sexo.FEM, accesoMujeres,controlAforo,aforoActual,protector).start();	
			new Persona(i, Sexo.MAS, accesoHombres,controlAforo,aforoActual,protector).start();
		}
		
	}
}

public class ServiciosUnisexNoStarve {
	
	// Es el control del recurso compartido: El WC (sin fairness)
	static final Semaphore accesoWC = new Semaphore(1,false);
	
	// PECUSA que controla el acceso múltiple al WC de hombres y mujeres
	static final Pecusa accesoMujeres = new Pecusa(accesoWC);;
	static final Pecusa accesoHombres = new Pecusa(accesoWC);;
	
	// Es el control de aforo: No puede haber mas de 4 personas (sin fairness)
	static final Semaphore controlAforo = new Semaphore(4,false);
	
	// Es la variable que muesta el aforo actual y su semaforo protector
	static int aforoActual = 0;
	static final Semaphore protector = new Semaphore(1,false);
	
	// Es el encargado de garantizar que el turno cambia y ningun sexo muere de inanicion
	static final Semaphore torniqueteTurno = new Semaphore(1,false);
	
	ServiciosUnisexNoStarve(){
		
		for(int i=0; i<7; i++){
			new PersonaNoStarve(i, Sexo.FEM, accesoMujeres,controlAforo,aforoActual,protector,torniqueteTurno).start();	
			new PersonaNoStarve(i, Sexo.MAS, accesoHombres,controlAforo,aforoActual,protector,torniqueteTurno).start();
		}
		
	}
	
	public static void main(String[] args){
		
		System.out.println("*******************************************");
		System.out.println(" PC - P2: Semáforos");
		System.out.println("*******************************************");
		System.out.println("  > Opción elegida: "+ args[0]);
		
		if (args[0].equals("starve")){
			System.out.println("  > Iniciando ejecución en modo 'starve'...\n");
			new ServiciosUnisex();
		}
		else if (args[0].equals("nostarve")){
			System.out.println("  > Iniciando ejecución en modo 'no-starve'...\n");
			new ServiciosUnisexNoStarve();
		}
		else
			System.out.println("  > El parámetro '"+ args[0]+ "' no es una opción válida. Se aceptan las opciones 'starve' y 'nostarve'");
			
	}

}

