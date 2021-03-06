package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import component.Position;
import component.Velocity;


public class PhysicsState extends BaseAppState {

	private EntityData ed;
	private EntitySet entities;
	private long lastFrame;
	
	@Override
	protected void initialize(Application app) {

		ed = getState(EntityDataState.class).getEntityData();
		entities = ed.getEntities(Position.class, Velocity.class);
	}

	@Override
	protected void cleanup(Application app) {
		// Release the entity set we grabbed previously
		entities.release();
		entities = null;
	}

	@Override
	protected void onEnable() {
		lastFrame = System.nanoTime();
	}

	@Override
	protected void onDisable() {
	}

	@Override
	public void update(float tpf) {

		// Use our own tpf calculation in case frame rate is
		// running away making this tpf unstable
		long time = System.nanoTime();
		long delta = time - lastFrame;
		lastFrame = time;
		if (delta == 0) {
			return; // no update to perform
		}

		double seconds = delta / 1000000000.0;

		// Clamp frame time to no bigger than a certain amount
		// to prevent physics errors. A little jitter for slow frames
		// is better than tunneling/ghost objects
		if (seconds > 0.1) {
			seconds = 0.1;
		}

		integrate(seconds);
	}

	protected void integrate(double tpf) {
		entities.applyChanges();
		for (Entity e : entities) {
			Position pos = e.get(Position.class);
			Velocity vel = e.get(Velocity.class);
			
			Vector3f linear = vel.getLinear();
			Vector3f loc = pos.getLocation();
			loc.addLocal((float) (linear.x * tpf), (float) (linear.y * tpf), (float) (linear.z * tpf));

			// A little quaternion magic for adding rotational velocity to orientation
			Quaternion orientation = pos.getFacing();
			orientation = H.addScaledVector(orientation, vel.getAngular(), tpf);
			orientation.normalizeLocal();

			e.set(new component.Position(loc, orientation));
		}
	}
}
