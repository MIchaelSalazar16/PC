public class BaseDatos {

	private String contenido = "estado inicial";
	
	public void escribir() {
		contenido = Thread.currentThread().getName() + " ha estado aqu�";
	}

	public String leer() { return contenido; }

}
