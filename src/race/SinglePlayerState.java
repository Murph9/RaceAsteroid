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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.lemur.event.BaseAppState;


/**
 *  Keeps track of the single-player game state and
 *  transitions through the state machine as game conditions
 *  change.
 *
 *  @author    Paul Speed
 */
public class SinglePlayerState extends BaseAppState {

    protected enum GameState { LoadLevel, Joining, Playing, Death, EndLevel, GameOver };

    private EntityData ed;
    private EntityId ship;

    private GameState state = GameState.GameOver;

    public SinglePlayerState() {
    }

	protected void setState(GameState state) {
		if (state == this.state) {
            return;
        }
        this.state = state;
        initState();
    }

    protected void initState() {
		switch (state) {
		case LoadLevel:
			setupLevel();
			break;
		case Joining:
			resetShip(true);
			break;
		case Playing:
			break;
		case Death:
		case EndLevel:
		case GameOver:
			break;
		}
    }

	protected void updateState(float tpf) {
		switch (state) {
		case LoadLevel:
			setState(GameState.Joining);
			break;
		case Joining:
			setState(GameState.Playing);
			break;
		case Playing:
			break;
		case Death:
		case EndLevel:
		case GameOver:
			System.out.println("Unknown state: " + state);
			break;
		}
	}

    protected void resetShip(boolean mobile) {
        ed.setComponents(ship,
                         new ModelType(RetroPanicModelFactory.MODEL_SHIP),
                         new Position(new Vector3f(), new Quaternion()),
                         new Velocity(new Vector3f()),
                         new Acceleration(new Vector3f()),
                         new Mass(mobile ? 0.1 : 0.0));
        getState(ShipControlState.class).setEnabled(mobile);
        getState(ShipCamera.class).setEnabled(mobile);
        getState(World.class).setEnabled(mobile);
    }

    protected void setupLevel() {
    }

    @Override
    public void update( float tpf ) {
        updateState(tpf);
    }

    @Override
    protected void initialize(Application app) {

        ed = getState(EntityDataState.class).getEntityData();

        ship = ed.createEntity();
        ed.setComponents(ship,
                         new Position(new Vector3f(), new Quaternion()),
                         new Velocity(new Vector3f(), new Vector3f()),
                         new Acceleration(new Vector3f(), new Vector3f()),
                         new Drag(0.1f, 0.25f),
                         CollisionShape.Circle(0.1f),
                         new Mass(0.1),
                         new ModelType(RetroPanicModelFactory.MODEL_SHIP));
        
        getStateManager().attach(new ShipControlState(ship));
        getState(ShipControlState.class).setEnabled(false);
        getStateManager().attach(new ShipCamera(ship));
        getState(ShipCamera.class).setEnabled(false);
        getStateManager().attach(new World(ship));
        getState(World.class).setEnabled(false);
        
        setState(GameState.LoadLevel);
    }

    @Override
	protected void cleanup(Application app) {
		getStateManager().detach(getState(ShipControlState.class));
		getStateManager().detach(getState(ShipCamera.class));
		getStateManager().detach(getState(World.class));
	}

	@Override
	protected void enable() {
	}
	@Override
	protected void disable() {
	}
}
