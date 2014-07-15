import java.util.*;

class Filosofo extends Thread {

   // Generador de números aleatorios
   static Random rnd = new Random();

   private int id = 0;
   private MonitorFilosofos monitor = null;

   public Filosofo(int id, MonitorFilosofos monitor) {
      this.id = id;
      this.monitor = monitor;
      System.out.println("Filósofo " + this.id + " está vivo");
   }

   private void think() {
      System.out.println("Filósofo " + this.id + " está pensando");
	try {sleep(rnd.nextInt(5000));}
	catch (InterruptedException e) {System.err.println("Interrupted while sleeping");}
   }

   private void eat() {
      System.out.println("Filósofo " + this.id + " está comiendo");
	try {sleep(rnd.nextInt(2000));}
	catch (InterruptedException e) {System.err.println("Interrupted while sleeping");}
   }

   public void run() {
      while (true) {
         think();
         System.out.println("Filósofo " + this.id + " quiere comer");
         monitor.takeForks(id);
         eat();
         monitor.putForks(id);
      }
   }

}