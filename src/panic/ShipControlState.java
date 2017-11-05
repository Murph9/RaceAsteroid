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

package panic;

import com.jme3.app.Application;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.event.BaseAppState;
import com.simsilica.lemur.input.AnalogFunctionListener;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;
import com.simsilica.lemur.input.StateFunctionListener;

/**
 * Maps player input into ship control. Note: this state takes over the job of
 * applying acceleration to the ship's velocity. This could easily be moved into
 * the physics system by adding an Acceleration component.
 *
 * @author Paul Speed
 */
public class ShipControlState extends BaseAppState implements AnalogFunctionListener, StateFunctionListener {

	private EntityData ed;
	private EntityId ship;

	private static final float rotateSpeed = 3;
	private static final float ACCEL_VALUE = 1;

	private double lastThrustTime = 0.1;
	private double thrustInterval = 0.1;

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
	protected void enable() {
		InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
		inputMapper.activateGroup(ShipFunctions.GROUP);
	}

	@Override
	protected void disable() {
		InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
		inputMapper.deactivateGroup(ShipFunctions.GROUP);
	}

	public void valueActive(FunctionId func, double value, double tpf) {
		if (func == ShipFunctions.F_TURN) {

			Velocity vel = ed.getComponent(ship, Velocity.class);
			float rotate = (float) (value * rotateSpeed);
			ed.setComponent(ship, new Velocity(vel.getLinear(), new Vector3f(0, 0, rotate)));
		} else if (func == ShipFunctions.F_THRUST) {

			Position pos = ed.getComponent(ship, Position.class);
			accel.set(0, (float) (ACCEL_VALUE * value), 0);
			accel.mult(behindShipScale());
			accel = pos.getFacing().multLocal(accel); // quaternion multlocal applies to the vector

			lastThrustTime += tpf;
			if (value != 0 && lastThrustTime >= thrustInterval) {

				lastThrustTime = 0;

				// Create a thrust entity (TODO push out making a bell curve shape)
				EntityId thrust = ed.createEntity();
				Vector3f thrustVel = accel.mult(-2);
				Vector3f thrustPos = pos.getLocation().add(thrustVel.normalize().multLocal(0.1f));
				ed.setComponents(thrust, 
						new Position(thrustPos, new Quaternion()), 
						new Velocity(thrustVel),
						new Acceleration(new Vector3f()), 
						new ModelType(PanicModelFactory.MODEL_THRUST),
						new Decay(1000));

			} else if (value == 0) {
				lastThrustTime = thrustInterval;
			}
		}
	}

	private float behindShipScale() {
		Vector3f rayStart = ed.getComponent(ship, Position.class).getLocation();
		Vector3f rayDir = ed.getComponent(ship, Position.class).getFacing().mult(new Vector3f(0,-1,0));
		
		EntitySet entities = ed.getEntities(CollisionShape.class);
		float minDist = 1;
		for (Entity entity: entities) {
			ModelType type = ed.getComponent(entity.getId(), ModelType.class);
			if (PanicModelFactory.MODEL_WALL.equals(type.getType())) {
				Vector3f p = ed.getComponent(entity.getId(), Position.class).getLocation();
				Vector3f r = ed.getComponent(entity.getId(), CollisionShape.class).getDir();
				
				H.IntersectResult result = H.linesIntersectV3(rayStart, rayStart.add(rayDir), p, p.add(r));
				if (result.success)
					minDist = Math.min(minDist, result.t);
			}
		}
		
		return 1/(minDist*minDist);
	}
	
	public void valueChanged(FunctionId func, InputState value, double tpf) {
	}

	@Override
	public void update(float tpf) {
		ed.setComponent(ship, new Acceleration(accel, ed.getComponent(ship, Acceleration.class).getAngular()));
	}

}
