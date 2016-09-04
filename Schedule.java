import java.util.*;
import java.lang.*;
public class Schedule {
	public static void main(String[] args) {

		new Timer().schedule(new TimerTask() {
				public void run() {
					try {
						Runtime.getRuntime().exec("cale.exe");
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			,2*10000);
	}
}
