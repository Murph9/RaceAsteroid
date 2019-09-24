package component;

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
		double percent = getPercent();
		if (percent > 1) {
			return "Stun[v=0%, max=" + (delta / 1000000.0) + "ms]";	
		}

		return "Stun[v=" + (int)(getPercent()*100) + "%, max="
				+ (delta / 1000000.0) + "ms]";
	}
}
