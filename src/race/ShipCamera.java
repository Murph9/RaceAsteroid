package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

import race.component.Position;


public class ShipCamera extends BaseAppState {

	private EntityData ed;
	private EntityId ship;
	
	private Camera cam;
	
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
		cam.setLocation(p.getLocation().add(0, 0, 10));
	}
}
