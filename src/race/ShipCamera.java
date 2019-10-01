package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

import component.Position;
import component.Velocity;


public class ShipCamera extends BaseAppState {

	private EntityData ed;
	private EntityId ship;
	
	private Camera cam;

	//TODO add smoothing
	private boolean lookAhead = false;
	
	public ShipCamera(EntityId ship) {
		this.ship = ship;
	}
	
	@Override
	protected void initialize(Application app) {
		ed = getState(EntityDataState.class).getEntityData();
		cam = app.getCamera();
	}

	@Override
	protected void onEnable() {
	}
	@Override
	protected void onDisable() {
	}
	@Override
	protected void cleanup(Application app) {
	}
	
	@Override
	public void update(float tpf) {
		Position p = ed.getComponent(ship, Position.class);
		Velocity v = ed.getComponent(ship, Velocity.class);
		Vector3f vel = v.getLinear();
		if (!lookAhead)
			vel = new Vector3f();
		cam.setLocation(p.getLocation().add(vel.x, vel.y, 10));
	}
}
