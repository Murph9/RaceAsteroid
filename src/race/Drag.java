package race;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public class Drag implements EntityComponent {

	private float drag;

	public Drag(float drag) {
		this.drag = drag;
	}

	public Vector3f getDrag(Vector3f vel) {
		return vel.mult(vel.length()*-drag);
	}

	@Override
	public String toString() {
		return "Drag[d=" + drag + "]";
	}
}
