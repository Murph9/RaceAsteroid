package race;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;

import race.component.Mass;
import race.component.Stun;
import race.component.Velocity;

/**
 * Asteroid Panic-specific contact handler. Performs simple contact resolution
 * and checks for game state conditions such as ship-asteroid collisions and
 * missile-asteroid collisions. It updates the PanicPlayer object accordingly
 * with either a score or a death. The resolveCollision() method is general for
 * any frictionless contact resolution scheme.
 *
 * @author Paul Speed
 */
public class PanicContactHandler implements ContactHandler {

	private EntityData ed;

	public PanicContactHandler() {
	}

	public void setCollisionState(CollisionState state) {
		if (state == null) {
			this.ed = null;
			return;
		}

		this.ed = state.getApplication().getStateManager().getState(EntityDataState.class).getEntityData();
	}

	protected float getInvMass(Entity e) {
		Mass m = ed.getComponent(e.getId(), Mass.class);
		if (m != null) {
			return (float) m.getInvMass();
		}
		return 0;
	}

	protected void resolveCollision(Entity line, Entity ship, Vector3f cp, Vector3f cn, float penetration) {
		float invMass1 = getInvMass(line);
		float invMass2 = getInvMass(ship);

		Velocity v1 = ed.getComponent(line.getId(), Velocity.class);
		Vector3f vl1 = v1.getLinear();
		Velocity v2 = ed.getComponent(ship.getId(), Velocity.class);
		Vector3f vl2 = v2.getLinear();

		Vector3f vRel = vl2.subtract(vl1);

		float relNormalVel = vRel.dot(cn);
		if (relNormalVel > 0) {
			return;
		}

		// Calculate the change in velocity and we ignore penetration
		float restitution = 0.5f;//0.99f;

		float impulse = (-(1 + restitution) * relNormalVel) / (invMass1 + invMass2);

		// Apply the impulse to the velocities
		vl2.addLocal(cn.mult(impulse * invMass2));
		ship.set(new Velocity(vl2, v2.getAngular()));
		
		ship.set(new Stun(ShipControlState.COLLISION_STUN_TIME));
	}

	public void handleContact(Entity line, Entity circle, Vector3f cp, Vector3f cn, float penetration) {
		resolveCollision(line, circle, cp, cn, penetration);
	}
}
