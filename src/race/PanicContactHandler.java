/*
 * $Id$
 *
 * Copyright (c) 2013 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package race;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;

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
	private PanicPlayer player;

	public PanicContactHandler() {
	}

	public void setPlayer(PanicPlayer player) {
		this.player = player;
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
	}

	public void handleContact(Entity line, Entity circle, Vector3f cp, Vector3f cn, float penetration) {
		resolveCollision(line, circle, cp, cn, penetration);
	}
}
