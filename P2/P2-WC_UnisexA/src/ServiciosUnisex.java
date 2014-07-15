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
	
	// N�mero que sirve de indentificador de la persona
	final int id_persona;
	
	// Sexo de la persona (No puede haber hombres a la vez que mujeres en el WC)
	final Sexo sexo;

	// Apunta al Pecusa de Hombres o de Mujeres segun el sexo
	Pecusa miPecusa;
	
	// Semaforo para controlar que el aforo del WC no se sobrepasa en 4 personas
	static Semaphore aforo;
	
	// Contador para mostar el aforo del ba�o y semaforo protector del mismo
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
	private void utilizarServicios(){
		try {
			sleep(rnd.nextInt(150));
		} catch (InterruptedException e) {
			System.err.println("Interrupted while sleeping");
		}
	}
	
	// Modela la accion de trabajar en la oficina
	private void trabajar(){
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
					
						// Persona entra en el ba�o
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
					  
						// Persona sale del ba�o
						numPersonas--;
				  
						// Notificar salida del WC por pantalla
				  		System.out.println("P @ WC: " +  numPersonas + "\t " + 
				  				  			sexo.toString() + " " + id_persona + " tir� de la cadena y sali� del servicio!");
				  	
				  	// Fin seccion critica contado aforo
					try {protector.release();} catch(Exception e) {};	

		  		// Actualiza el aforo para avisar si habia otro hombre esperando
		  		try {aforo.release();} catch(Exception e) {};	
	  		  
	  		// Si es el ultimo hombre/mujer en salir, avisar al otro sexo para entrar
	  		miPecusa.abrir_al_otro_genero_si_ultimo();
  		  	
		} // while
	}
}


public class ServiciosUnisex {
	
	// Es el control del recurso compartido: El WC (sin fairness)
	static final Semaphore accesoWC = new Semaphore(1,false);
	
	// PECUSA que controla el acceso m�ltiple al WC de hombres y mujeres
	static final Pecusa accesoMujeres = new Pecusa(accesoWC);;
	static final Pecusa accesoHombres = new Pecusa(accesoWC);;
	
	// Es el control de aforo: No puede haber mas de 4 personas (sin fairness)
	static final Semaphore controlAforo = new Semaphore(4,false);
	
	// Es la variable que muesta el aforo actual y su semaforo protector
	static int aforoActual = 0;
	static final Semaphore protector = new Semaphore(1,false);
	
	public static void main(String[] args){
		for(int i=0; i<7; i++){
			new Persona(i, Sexo.FEM, accesoMujeres,controlAforo,aforoActual,protector).start();	
			new Persona(i, Sexo.MAS, accesoHombres,controlAforo,aforoActual,protector).start();
		}
	}
}
