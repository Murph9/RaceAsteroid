package race;

import com.simsilica.es.EntityComponent;

public class Stun implements EntityComponent {

	private long start;
	private long delta;
	
	public Stun(long deltaMillis) {
		this.start = System.nanoTime();
		this.delta = deltaMillis * 1000000;
	}
	
	public double getPercent() {
		long time = System.nanoTime();
		return (double) (time - start) / delta;
	}

	@Override
	public String toString() {
		return "Stun[" + (delta / 1000000.0) + " ms]";
	}
}
