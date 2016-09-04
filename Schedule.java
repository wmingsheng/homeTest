import java.util.*;
import java.lang.*;
public class Schedule {
	public static void main(String[] args) {
		long start = System.currentTimeMills();
		SimpleDataFormat dFormat = new SimpleDataFormat("yyyy年MM月dd日 hh:mm:ss");
		Data d = new Data();
		System.out.println(dFormat.format(d));
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
		System.out.println(System.currentTimeMills() - start);
	}
}
