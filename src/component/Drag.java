package component;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

import race.H;

public class Drag implements EntityComponent {

	private float linear;
	private float drag;

	public Drag(float linear, float drag) {
		this.linear = linear;
		this.drag = drag;
	}

	public Vector3f getDrag(Vector3f vel) {
		return vel.mult(vel.length() * -drag).add(vel.mult(-linear));
	}

	@Override
	public String toString() {
		return "Drag[l=" + H.roundDecimal(linear, 3) + ", d=" + H.roundDecimal(drag, 3) + "]";
	}
}
