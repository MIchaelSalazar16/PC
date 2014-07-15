public interface MonitorArbitraje {

	public void entrarLeer() throws InterruptedException;
	public void salirLeer();
	public void entrarEscribir() throws InterruptedException;
	public void salirEscribir();
  
}
