package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.GuiGlobals;

import com.simsilica.lemur.input.AnalogFunctionListener;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;
import com.simsilica.lemur.input.StateFunctionListener;

import race.CollisionShape.Type;

/**
 * Maps player input into ship control.
 *
 * @author Paul Speed (modified a lot by murph)
 */
public class ShipControlState extends BaseAppState implements AnalogFunctionListener, StateFunctionListener {

	private EntityData ed;
	private EntityId ship;
	
	private static final float ROTATE_SPEED = 4;
	public static final long COLLISION_STUN_TIME = 400;
	
	private static final float RAY_CAST_LENGTH = 0.8f;
	private static final float ACCEL_VALUE = 3f;
	
	private static final float WALL_SCALE_A = 5.66f;
	private static final float WALL_SCALE_B = 3.75f;
	private static final float WALL_SCALE_C = 0.87f;

	private static final float THRUST_INTERVAL = 0.05f;
	private float lastThrustTime = 0.1f;

	private Vector3f accel = new Vector3f();

	public ShipControlState(EntityId ship) {
		this.ship = ship;
	}

	@Override
	protected void initialize(Application app) {
		ed = getState(EntityDataState.class).getEntityData();
		
		InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
		inputMapper.addAnalogListener(this, ShipFunctions.F_TURN, ShipFunctions.F_THRUST);
		inputMapper.addStateListener(this, ShipFunctions.F_THRUST);
	}

	@Override
	protected void cleanup(Application app) {
		InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
		inputMapper.removeAnalogListener(this, ShipFunctions.F_TURN, ShipFunctions.F_THRUST);
		inputMapper.removeStateListener(this, ShipFunctions.F_THRUST);
	}

	@Override
	protected void onEnable() {
		InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
		inputMapper.activateGroup(ShipFunctions.GROUP);
	}

	@Override
	protected void onDisable() {
		InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
		inputMapper.deactivateGroup(ShipFunctions.GROUP);
	}

	public void valueActive(FunctionId func, double value, double tpf) {
		Velocity vel = ed.getComponent(ship, Velocity.class);
		if (func == ShipFunctions.F_TURN) {

			float rotate = (float) (value * ROTATE_SPEED);
			ed.setComponent(ship, new Velocity(vel.getLinear(), new Vector3f(0, 0, rotate)));
		} else if (func == ShipFunctions.F_THRUST) {
			
			Position pos = ed.getComponent(ship, Position.class);
			accel.set(0, (float) (ACCEL_VALUE * value), 0);
			float scale = behindShipScale();
			accel.multLocal(scale);
			accel = pos.getFacing().multLocal(accel); // quaternion multlocal applies to the vector
			
			//wall collision creates stun effect
			Stun s = ed.getComponent(ship, Stun.class);
            if (s.getPercent() <= 1.0)
                accel.set(0, 0, 0);
			
			lastThrustTime += tpf;
			if (value != 0 && lastThrustTime >= THRUST_INTERVAL && accel.length() > 0) {

				lastThrustTime = 0;

				// Create a thrust particle TODO EmitterState
				EntityId thrust = ed.createEntity();
				Vector3f thrustVel = vel.getLinear().add(accel.mult(-5));
				Vector3f thrustPos = pos.getLocation().add(accel.normalize().multLocal(-0.1f));
				ed.setComponents(thrust, 
						new Position(thrustPos, new Quaternion()), 
						new Velocity(thrustVel),
						new Acceleration(new Vector3f()), 
						new Drag(0,0),
						new ModelType(RetroPanicModelFactory.MODEL_THRUST),
						new Decay(100));
				
			} else if (value == 0) {
				lastThrustTime = THRUST_INTERVAL;
			}
		}
	}

	private float behindShipScale() {
		Vector3f rayStart = ed.getComponent(ship, Position.class).getLocation();
		Vector3f rayDir = ed.getComponent(ship, Position.class).getFacing().mult(new Vector3f(0,-RAY_CAST_LENGTH,0));
		
		//TODO throw out 2 rays about 30 degrees apart (because its not just one)
		//TODO is also seems that boosting off the wall spins the craft a little, so these 2 rays are important
		
		EntitySet entities = ed.getEntities(CollisionShape.class);
		float minDist = 1;
		for (Entity entity: entities) {
			CollisionShape shape = ed.getComponent(entity.getId(), CollisionShape.class);
			if (shape != null && shape.getType() == Type.Line) {
				Vector3f p = ed.getComponent(entity.getId(), Position.class).getLocation();
				Vector3f r = shape.getDir();
				
				H.IntersectResult result = H.linesIntersectXY(rayStart, rayStart.add(rayDir), p, p.add(r));
				if (result.success)
					minDist = Math.min(minDist, result.t);
			}
		}
		float result = Math.max(1, (WALL_SCALE_A*FastMath.exp(-minDist*WALL_SCALE_B)) + (WALL_SCALE_C/minDist)); //never slower 
		return result;
	}
	
	public void valueChanged(FunctionId func, InputState value, double tpf) {
	}

	@Override
	public void update(float tpf) {
		ed.setComponent(ship, new Acceleration(accel, ed.getComponent(ship, Acceleration.class).getAngular()));
	}		
}
