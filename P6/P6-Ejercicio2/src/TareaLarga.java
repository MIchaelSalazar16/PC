import java.util.Random;
import java.util.concurrent.Callable;


public class TareaLarga implements Callable<String> {

	public TareaLarga(){
		// ...
	}

	@Override
	public String call() throws Exception {
		
		try {
			Thread.sleep(Math.abs(new Random().nextLong() % 5000));
		} catch (InterruptedException e) {
			System.err.println("Interrupted while sleeping!");
		}
		
		return Thread.currentThread().getName();
	}

}
