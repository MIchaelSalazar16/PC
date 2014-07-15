// ************* Pecusa class. Author: Simon Pickin **************

import java.util.concurrent.Semaphore;

// PECUSA: = el Primero que Entra Cierra, el Último que Sale Abre
public class Pecusa {

	int contadorPecusa = 0;
	Semaphore protectorContador = new Semaphore(1);
	Semaphore recursoCompartido;

	// Suponemos que recurso es un semÃ¡foro inicializado a 1,
	public Pecusa(Semaphore recurso){
		recursoCompartido = recurso;
	}

	public void cerrar_al_otro_genero_si_primero() {
	    try {protectorContador.acquire();} catch(Exception e) {};
		contadorPecusa++;
		if (contadorPecusa == 1)
			try{recursoCompartido.acquire();} catch(Exception e) {}
		protectorContador.release();
	}

	public void abrir_al_otro_genero_si_ultimo() {
	    try {protectorContador.acquire();} catch(Exception e) {};
		contadorPecusa--;
		if (contadorPecusa == 0) recursoCompartido.release();
		protectorContador.release();
	}
}
