
public class TareaDeCalculo implements Runnable{

	private int contador;
	private int maxCont;
	
	public TareaDeCalculo(int maxCont){
		this.maxCont = maxCont;
	}

	@Override
	public void run() {
		while(contador < maxCont)
			contador++;	
	}

}
