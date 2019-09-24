package race.component;

import com.simsilica.es.EntityComponent;

import race.H;

/**
 * Represents a time-to-live for an entity.
 *
 * @author Paul Speed
 */
public class Decay implements EntityComponent {
	private long start;
	private long delta;

	public Decay(long deltaMillis) {
		this.start = System.nanoTime();
		this.delta = deltaMillis * 1000000;
	}

	public double getPercent() {
		long time = System.nanoTime();
		return (double) (time - start) / delta;
	}

	@Override
	public String toString() {
		return "Decay[" + H.roundDecimal(delta / 1000000.0, 3) + " ms]";
	}
}
