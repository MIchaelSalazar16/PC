
public class Secuenciador {

	static int seq;
	static int currentEvent;
	
	Secuenciador(){
		seq = 0;
		currentEvent = 1;
	}
	
	public int getTicket(String obj){
		System.out.println("\t\t\t\t\t\tTurno actual: " + currentEvent + "\t\tUltimo ticket: " + (seq+1) + "\t Obj:" + obj);
		return ++seq;
	}
	
	public int currentTurn(){
		return currentEvent;
	}
	
	public void advance(){
		System.out.println("\t\t\t\t\t\tConsumido turno: " + currentEvent);
		currentEvent++;
	}
}
