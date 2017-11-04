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
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;


/**
 *  Keeps track of the single-player game state and
 *  transitions through the state machine as game conditions
 *  change.
 *
 *  @author    Paul Speed
 */
public class SinglePlayerState extends BaseAppState {

    protected enum GameState { LoadLevel, Starting, Joining, Playing, Death, EndLevel, GameOver };

    /*
        States break down as follows
        1) LoadLevel
            init: Setup asteroids for current player level
            update: show messages until done
                -go to Starting
        2) Starting
            init: reset ship, invincible and unmoveable
            update: show messages until done
                -go to Joining
        3) Joining
            init: reset ship, invincible, moveable
            update: when delay is done
                -go to Playing
        4) Playing
            init: ship invincible = false
            update: check for asteroids = 0
                        -go to EndLevel
                    check for death
                        -go to Death
        5) Death
            init: check for ships remaining
                    -go to Starting or GameOver
        6) EndLevel
            init:
            update: show messages until done
                -increment level
                -go to LoadLevel
        7) GameOver
            init: message
    */

    private EntityData ed;
    private EntitySet asteroids;

    private PanicPlayer player;
    private EntityId    ship;

    private GameState state = GameState.GameOver;

    public SinglePlayerState() {
    }

    protected void setState( GameState state, String... titles ) {
        if( state == this.state ) {
            return;
        }
        this.state = state;
        initState();
    }

    protected void initState() {
        switch( state ) {
            case LoadLevel:
                setupLevel();
                break;
            case Starting:
                resetShip(false);
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
			setState(GameState.Starting, "Ready...", "Set...", "Go!");
			break;
		case Starting:
			setState(GameState.Joining, "");
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
                         new ModelType(PanicModelFactory.MODEL_SHIP),
                         new Position(new Vector3f(), new Quaternion()),
                         new Velocity(new Vector3f()),
                         new Acceleration(new Vector3f()),
                         new Mass(mobile ? 0.1 : 0.0));
        getState(ShipControlState.class).setEnabled(mobile);
    }

    protected void setupLevel() {
    }

    @Override
    public void update( float tpf ) {
        updateState(tpf);
    }

    @Override
    protected void initialize( Application app ) {

        // The player has the ship they are playing and 2
        // extra in reserve to start with.
        this.player = new PanicPlayer(2);

        PanicContactHandler contactHandler
                = (PanicContactHandler)getState(CollisionState.class).getContactHandler();
        contactHandler.setPlayer( player );

        ed = getState(EntityDataState.class).getEntityData();

        ship = ed.createEntity();
        ed.setComponents(ship,
                         new Position(new Vector3f(), new Quaternion()),
                         new Velocity(new Vector3f(), new Vector3f()),
                         new Acceleration(new Vector3f(), new Vector3f()),
                         CollisionShape.Circle(0.1f),
                         new Mass(0.1),
                         new ModelType(PanicModelFactory.MODEL_SHIP));
        
        EntityId line = ed.createEntity();
        ed.setComponents(line, 
        		 new Position(new Vector3f(-0.5f, 1f, 0), new Quaternion()),
        		 new Velocity(new Vector3f(), new Vector3f()),
                 CollisionShape.Line(new Vector3f(0.5f, -2f, 0)),
                 new Mass(10000),
                 new ModelType(PanicModelFactory.MODEL_WALL));
        
        line = ed.createEntity();
        ed.setComponents(line, 
        		 new Position(new Vector3f(-3f, -3f, 0), new Quaternion()),
        		 new Velocity(new Vector3f(), new Vector3f()),
                 CollisionShape.Line(new Vector3f(0, 6f, 0)),
                 new Mass(10000),
                 new ModelType(PanicModelFactory.MODEL_WALL));
        line = ed.createEntity();
        ed.setComponents(line, 
        		 new Position(new Vector3f(-3f, -3f, 0), new Quaternion()),
        		 new Velocity(new Vector3f(), new Vector3f()),
                 CollisionShape.Line(new Vector3f(6f, 0f, 0)),
                 new Mass(10000),
                 new ModelType(PanicModelFactory.MODEL_WALL));
        line = ed.createEntity();
        ed.setComponents(line, 
        		 new Position(new Vector3f(3f, 3f, 0), new Quaternion()),
        		 new Velocity(new Vector3f(), new Vector3f()),
                 CollisionShape.Line(new Vector3f(-6f, 0f, 0)),
                 new Mass(10000),
                 new ModelType(PanicModelFactory.MODEL_WALL));
        line = ed.createEntity();
        ed.setComponents(line, 
        		 new Position(new Vector3f(3f, 3f, 0), new Quaternion()),
        		 new Velocity(new Vector3f(), new Vector3f()),
                 CollisionShape.Line(new Vector3f(0f, -6f, 0)),
                 new Mass(10000),
                 new ModelType(PanicModelFactory.MODEL_WALL));

        getStateManager().attach(new ShipControlState(ship));
        getState(ShipControlState.class).setEnabled(false);

        setState(GameState.LoadLevel, "PLAYER 1 UP");
    }

    @Override
    protected void cleanup( Application app ) {
        if (asteroids != null)
        	asteroids.release();
        asteroids = null;
        getStateManager().detach(getState(ShipControlState.class));
    }

    @Override
    protected void enable() {
    }

    @Override
    protected void disable() {
    }
}
