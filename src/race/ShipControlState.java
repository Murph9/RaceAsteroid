package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Line;
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

import component.Acceleration;
import component.CollisionShape;
import component.Decay;
import component.Mass;
import component.ModelType;
import component.CollisionShape.Type;
import component.Position;
import component.Velocity;

/**
 * Maps player input into ship control
 * 
 * @author murph
 */
public class ShipControlState extends BaseAppState implements AnalogFunctionListener, StateFunctionListener {

	private EntityData ed;
	private EntityId ship;
	
	private static final float ROTATE_SPEED = 8;
	public static final long COLLISION_STUN_TIME = 350;
	
	private static final float RAY_CAST_LENGTH = 1.6f;
	private static final float BASE_ACCEL_VALUE = 2.5f;
	private static final float WALL_FORCE = 3;
	
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
			this.accel.set(0, BASE_ACCEL_VALUE * (float) value, 0);
			pos.getFacing().multLocal(this.accel); // quaternion multlocal applies to the vector

			//TODO move to somewhere else
			float dist = closestWall(pos.getLocation());
			if (dist < 1) {
				this.accel.multLocal(WALL_FORCE);
			}

			lastThrustTime += tpf;
			if (value != 0 && lastThrustTime >= THRUST_INTERVAL && this.accel.length() > 0) {

				lastThrustTime = 0;

				// Create a thrust particle
				EntityId thrust = ed.createEntity();
				Vector3f thrustVel = vel.getLinear().add(accel.mult(-1));
				Vector3f thrustPos = pos.getLocation().add(accel.normalize().multLocal(-0.1f));
				ed.setComponents(thrust, 
						new Position(thrustPos, new Quaternion()), 
						new Velocity(thrustVel),
						new ModelType(RaceModelFactory.MODEL_THRUST),
						new Mass(0.01),
						CollisionShape.Circle(0.1f, true),
						new Decay(250));
				
			} else if (value == 0) {
				lastThrustTime = THRUST_INTERVAL;
			}
		}
	}
	
	private float closestWall(Vector3f pos) {
		EntitySet entities = ed.getEntities(CollisionShape.class);
		float minDist = Float.MAX_VALUE;
		for (Entity entity : entities) {
			CollisionShape shape = ed.getComponent(entity.getId(), CollisionShape.class);
			if (shape != null && shape.getType() == Type.Line && !shape.getGhost()) {
				Line l = new Line(ed.getComponent(entity.getId(), Position.class).getLocation(), shape.getDir());
				minDist = Math.min(minDist, l.distance(pos));
			}
		}
		return minDist;
	}

	@SuppressWarnings("unused")
	private float thrustRayDist() {
		Vector3f rayStart = ed.getComponent(ship, Position.class).getLocation();
		Vector3f rayDir = ed.getComponent(ship, Position.class).getFacing().mult(new Vector3f(0, -RAY_CAST_LENGTH, 0));

		Vector3f rayDirOff1 = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * 30, Vector3f.UNIT_Z).mult(rayDir);
		Vector3f rayDirOff2 = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * -30, Vector3f.UNIT_Z).mult(rayDir);

		EntitySet entities = ed.getEntities(CollisionShape.class);
		float minDist = 1;
		for (Entity entity : entities) {
			CollisionShape shape = ed.getComponent(entity.getId(), CollisionShape.class);
			if (shape != null && shape.getType() == Type.Line && !shape.getGhost()) {
				Vector3f p = ed.getComponent(entity.getId(), Position.class).getLocation();
				Vector3f r = shape.getDir();

				H.IntersectResult result = H.linesIntersectXY(rayStart, rayStart.add(rayDirOff1), p, p.add(r));
				if (result.success)
					minDist = Math.min(minDist, result.t);

				result = H.linesIntersectXY(rayStart, rayStart.add(rayDirOff2), p, p.add(r));
				if (result.success)
					minDist = Math.min(minDist, result.t);
			}
		}

		return minDist * RAY_CAST_LENGTH;
	}

	
	public void valueChanged(FunctionId func, InputState value, double tpf) {
	}

	@Override
	public void update(float tpf) {
		ed.setComponent(ship, new Acceleration(accel, ed.getComponent(ship, Acceleration.class).getAngular()));
	}		
}
