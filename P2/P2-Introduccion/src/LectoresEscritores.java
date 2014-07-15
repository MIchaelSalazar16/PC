// ************* LectoresEscritores. Author: Simon Pickin **************

import java.util.*;
import java.util.concurrent.Semaphore;

class Lector extends Thread {

  	// Random Number Generator
	static Random rnd = new Random();

	// Número que sirve de indentificador del objeto
	final int mi_numero;

	// PECUSA que controla el acceso múltiple a la base de datos
	static Pecusa disponibilidad;

	Lector(int num, Semaphore sem){
		mi_numero = num;
		if (disponibilidad == null) {
			disponibilidad = new Pecusa(sem);
		}
	}

	private void pasarUnRato(){
		try {
			sleep(rnd.nextInt(10));
		} catch (InterruptedException e) {
			System.err.println("Interrupted while sleeping");
		}
	}

	// Aquí se modela la lectura con un sleep aleatorio
	private void leer(){
		pasarUnRato();
	}

	public void run(){
		while(true){
			pasarUnRato();
			disponibilidad.cerrar_al_otro_genero_si_primero();
			  System.out.println("Lector número " + mi_numero + " leyendo.");
			  leer();
 			  System.out.println("Lector número " + mi_numero + " terminado.");
 			disponibilidad.abrir_al_otro_genero_si_ultimo();
		}
	}
}

class Escritor extends Thread {

  	// Random Number Generator
	static Random rnd = new Random();

	// Número que sirve de indentificador del objeto
	final int mi_numero;

	// Semáforo que controla el acceso a la base de datos
	final Semaphore disponible;

	Escritor(int num, Semaphore sem){
		mi_numero = num;
		disponible = sem;
	}

	private void pasarUnRato(){
		try {
			sleep(rnd.nextInt(1000));
		} catch (Exception e) {
			System.err.println("Interrupted while sleeping");
		}
	}

	// Aquí se modela la escritura con un sleep aleatorio
	private void escribir(){
		pasarUnRato();
	}

	public void run(){
		while(true){
			pasarUnRato();
			try{disponible.acquire();} catch (Exception e) {}
			  System.out.println("Escritor numero " + mi_numero + " escribiendo.");
			  escribir();
			  System.out.println("Escritor numero " + mi_numero + " terminado.");
			disponible.release();
		}
	}
}


public class LectoresEscritores{

	// Semáforo que se utiliza para controlar el acceso a la base de datos
	static final Semaphore libre = new Semaphore(1);

	public static void main(String[] args){
		for(int i=0; i<5; i++){
			new Lector(i, libre).start();	
			new Escritor(i, libre).start();
		}
	}
}
