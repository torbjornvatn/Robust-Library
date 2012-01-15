import lejos.nxt.LCD;


public class Test {
    public static void main(String[] args) {
        LCD.refresh();
        System.out.println("t1");
        try { 
        	Thread.sleep(2000);
        } catch (Exception e) {}
    }
}
