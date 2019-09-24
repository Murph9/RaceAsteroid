package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import component.Acceleration;
import component.Drag;
import component.Velocity;


public class ThrustPhysicsState extends BaseAppState {

	private EntityData ed;
	private long lastFrame;
	private EntitySet accelEntities;
	
	@Override
	protected void initialize(Application app) {

		ed = getState(EntityDataState.class).getEntityData();
		accelEntities = ed.getEntities(Velocity.class, Acceleration.class, Drag.class);
	}

	@Override
	protected void cleanup(Application app) {
		accelEntities.release();
		accelEntities = null;
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

		accelEntities.applyChanges();
		for (Entity e : accelEntities) {
			Velocity vel = e.get(Velocity.class);
			Acceleration acc = e.get(Acceleration.class);
			Drag drag = e.get(Drag.class);
			Vector3f forces = acc.getLinear().add(drag.getDrag(vel.getLinear()));
			//no angular acceleration yet

			Vector3f linear = vel.getLinear().add((float) (forces.x * tpf), (float) (forces.y * tpf), (float) (forces.z * tpf));
			e.set(new Velocity(linear, vel.getAngular()));
		}
	}
}
