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

import com.jme3.app.Application;
import com.jme3.math.FastMath;
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
 * Maps player input into ship control.
 *
 * @author Paul Speed (modified by murph)
 */
public class ShipControlState extends BaseAppState implements AnalogFunctionListener, StateFunctionListener {

	private EntityData ed;
	private EntityId ship;

	private static final float ROTATE_SPEED = 4;
	
	private static final float RAY_CAST_LENGTH = 1.3f;
	private static final float ACCEL_VALUE = 3;
	
	private static final float WALL_SCALE = 5;
	private static final float WALL_SCALE_2 = 3.3f;

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
			
			lastThrustTime += tpf;
			if (value != 0 && lastThrustTime >= THRUST_INTERVAL) {

				lastThrustTime = 0;

				// Create a thrust entity
				EntityId thrust = ed.createEntity();
				Vector3f thrustVel = vel.getLinear().add(accel.mult(-1));
				Vector3f thrustPos = pos.getLocation().add(accel.normalize().multLocal(-0.1f));
				ed.setComponents(thrust, 
						new Position(thrustPos, new Quaternion()), 
						new Velocity(thrustVel),
						new Acceleration(new Vector3f()), 
						new Drag(0,0),
						new ModelType(RetroPanicModelFactory.MODEL_THRUST),
						new Decay(100));

				/*
				allow color/scale settings
				if (scale > 1) {
					thrust = ed.createEntity();
					ed.setComponents(thrust, 
							new Position(thrustPos, new Quaternion()), 
							new Velocity(thrustVel.mult(0.9f)),
							new Acceleration(new Vector3f()), 
							new Drag(0,0),
							new ModelType(RetroPanicModelFactory.MODEL_THRUST),
							new Decay(200));
				}
				*/
				
			} else if (value == 0) {
				lastThrustTime = THRUST_INTERVAL;
			}
		}
	}

	private float behindShipScale() {
		Vector3f rayStart = ed.getComponent(ship, Position.class).getLocation();
		Vector3f rayDir = ed.getComponent(ship, Position.class).getFacing().mult(new Vector3f(0,-RAY_CAST_LENGTH,0));
		
		EntitySet entities = ed.getEntities(CollisionShape.class);
		float minDist = 1;
		for (Entity entity: entities) {
			ModelType type = ed.getComponent(entity.getId(), ModelType.class);
			if (RetroPanicModelFactory.MODEL_WALL.equals(type.getType())) {
				Vector3f p = ed.getComponent(entity.getId(), Position.class).getLocation();
				Vector3f r = ed.getComponent(entity.getId(), CollisionShape.class).getDir();
				
				H.IntersectResult result = H.linesIntersectV3(rayStart, rayStart.add(rayDir), p, p.add(r));
				if (result.success)
					minDist = Math.min(minDist, result.t);
			}
		}
		float result = Math.max(1, (WALL_SCALE*FastMath.exp(-minDist*WALL_SCALE_2))); //never slower 
		return result;
	}
	
	public void valueChanged(FunctionId func, InputState value, double tpf) {
	}

	@Override
	public void update(float tpf) {
		ed.setComponent(ship, new Acceleration(accel, ed.getComponent(ship, Acceleration.class).getAngular()));
		
		//add to the position trail
		
	}

}
