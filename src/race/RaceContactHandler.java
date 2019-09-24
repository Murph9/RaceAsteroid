package race;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;

import component.Mass;
import component.Stun;
import component.Stunable;
import component.Velocity;

/**
 * Performs simple contact resolution.
 * The resolveCollision() method is general for any frictionless contact resolution scheme.
 */
public class RaceContactHandler implements ContactHandler {

	private EntityData ed;

	public RaceContactHandler() {
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

	public void handleContact(Entity e1, Entity e2, Vector3f cp, Vector3f cn, float penetration) {
		resolveCollision(e1, e2, cp, cn, penetration);
	}

	protected void resolveCollision(Entity e1, Entity e2, Vector3f cp, Vector3f cn, float penetration) {
		float invMass1 = getInvMass(e1);
		float invMass2 = getInvMass(e2);

		Velocity v1 = ed.getComponent(e1.getId(), Velocity.class);
		Vector3f vl1 = v1.getLinear();
		Velocity v2 = ed.getComponent(e2.getId(), Velocity.class);
		Vector3f vl2 = v2.getLinear();

		Vector3f vRel = vl2.subtract(vl1);

		float relNormalVel = vRel.dot(cn);
		if (relNormalVel > 0) {
			return;
		}

		// Calculate the change in velocity and we ignore penetration
		float restitution = 0.99f;

		float impulse = (-(1 + restitution) * relNormalVel) / (invMass1 + invMass2);

		// Apply the impulse to the velocities (0 mass will mean no motion)
		vl1.addLocal(cn.mult(-impulse * invMass1));
		e1.set(new Velocity(vl1, v1.getAngular()));

		vl2.addLocal(cn.mult(impulse * invMass2));
		e2.set(new Velocity(vl2, v2.getAngular()));

		stun(e1);
		stun(e2);
	}

	private void stun(Entity e) {
		if (ed.getComponent(e.getId(), Stunable.class) != null) {
			e.set(new Stun(ShipControlState.COLLISION_STUN_TIME));
		}
	}
}
