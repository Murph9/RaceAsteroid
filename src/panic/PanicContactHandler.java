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

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;

/**
 *  Asteroid Panic-specific contact handler.  Performs simple
 *  contact resolution and checks for game state conditions such
 *  as ship-asteroid collisions and missile-asteroid collisions.
 *  It updates the PanicPlayer object accordingly with either a
 *  score or a death.
 *  The resolveCollision() method is general for any frictionless
 *  contact resolution scheme.
 *
 *  @author    Paul Speed
 */
public class PanicContactHandler implements ContactHandler {

    private EntityData ed;
    private PanicPlayer player;

    public PanicContactHandler() {
    }

    public void setPlayer( PanicPlayer player ) {
        this.player = player;
    }

    public void setCollisionState( CollisionState state )
    {
        if( state == null ) {
            this.ed = null;
            return;
        }

        this.ed = state.getApplication().getStateManager().getState(EntityDataState.class).getEntityData();
    }

    protected float getInvMass( Entity e ) {
        Mass m = ed.getComponent(e.getId(), Mass.class);
        if( m != null ) {
            return (float)m.getInvMass();
        }
        CollisionShape shape = e.get(CollisionShape.class);
        if( shape != null ) {
            return 1.0f/shape.getRadius();
        }
        return 0;
    }

    protected void resolveCollision( Entity e1, Entity e2, Vector3f cp, Vector3f cn, float penetration )
    {
        Position p1 = e1.get(Position.class);
        Position p2 = e2.get(Position.class);
        float invMass1 = getInvMass(e1);
        float invMass2 = getInvMass(e2);

        if( penetration > 0 ) {
            // Resolve the penetration
            Vector3f np1 = p1.getLocation().subtract(cn.mult(penetration));
            Vector3f np2 = p2.getLocation().add(cn.mult(penetration));
            e1.set(new Position(np1,p1.getFacing()));
            e2.set(new Position(np2,p2.getFacing()));
        }

        Velocity v1 = ed.getComponent(e1.getId(), Velocity.class);
        Vector3f vl1 = v1.getLinear();
        Velocity v2 = ed.getComponent(e2.getId(), Velocity.class);
        Vector3f vl2 = v2.getLinear();

        Vector3f vRel = vl2.subtract(vl1);

        float relNormalVel = vRel.dot(cn);
        if( relNormalVel > 0 ) {
            // Already separating
            return;
        }

        // Calculate the change in velocity and we'll ignore
        // penetration for the moment.
        float restitution = 0.99f;

        float impulse = (-(1+restitution) * relNormalVel)
                        / (invMass1 + invMass2);

        // Apply the impulse to the velocities
        vl1.subtractLocal(cn.mult(impulse * invMass1));
        vl2.addLocal(cn.mult(impulse * invMass2));

        e1.set(new Velocity(vl1, v1.getAngular()));
        e2.set(new Velocity(vl2, v2.getAngular()));
    }

    protected void shipCollision( Entity ship, Entity other, ModelType type, Vector3f cp, Vector3f cn, float penetration )
    {
    	//ignore for now
    	
    	/*
        Velocity v1 = ed.getComponent(ship.getId(), Velocity.class);
        Vector3f vl1 = v1.getLinear();
        Velocity v2 = ed.getComponent(other.getId(), Velocity.class);
        Vector3f vl2 = v2.getLinear();

        Vector3f vRel = vl1.subtract(vl2);

        float relNormalVel = vRel.dot(cn);

        // If the player is invincible right now then no explosion...
        if( player.isInvincible() )
            return;

        // Could calculate damage based on relNormalVel
        System.out.println( "relNormalVel:" + relNormalVel );

        // Kill the ship
        player.setDead(true);
        ed.removeComponent(ship.getId(), ModelType.class);

        // Create some explosive debris from fake asteroids with no
        // collision shapes
        int debrisCount = (int)((Math.random() * 5) + 5);
        float angleOffset = (float)Math.random();
        for( int i = 0; i < debrisCount; i++ ) {
            EntityId debris = ed.createEntity();
            float angle = angleOffset + ((float)i / debrisCount) * FastMath.TWO_PI;
            float x = FastMath.cos(angle) * 2;
            float y = FastMath.sin(angle) * 2;
            float spin = (float)Math.random() * FastMath.PI * 4 - FastMath.PI * 2;
            ed.setComponents(debris,
                             new Position(cp, new Quaternion()),
                             new Velocity(new Vector3f(x,y,0), new Vector3f(0,0,spin)),
                             new ModelType(PanicModelFactory.MODEL_SHIP_DEBRIS),
                             new Decay(500));
        }

        // Make an explosion sound
        boom1.playInstance();
        */
    }

    public void handleContact( Entity e1, Entity e2, Vector3f cp, Vector3f cn, float penetration )
    {
    	return;
    	/* ignore for now
    	
        resolveCollision(e1, e2, cp, cn, penetration);

        // Now, if it's a specific kind of collision then we
        // will do more specific things.
        ModelType t1 = ed.getComponent(e1.getId(), ModelType.class);
        ModelType t2 = ed.getComponent(e2.getId(), ModelType.class);
        if( t1 == null || t2 == null )  {
            return;
        }

        if( PanicModelFactory.MODEL_SHIP.equals(t1.getType()) ) {
            shipCollision(e1, e2, t2, cp, cn, penetration);
        } else if( PanicModelFactory.MODEL_SHIP.equals(t2.getType()) ) {
            shipCollision(e2, e1, t1, cp, cn.mult(-1), penetration);
        } else {
            // Assume asteroid to asteroid

            // Check the sizes
            CollisionShape shape1 = e1.get(CollisionShape.class);
            float r1 = shape1 == null ? 0.01f : shape1.getRadius();
            CollisionShape shape2 = e2.get(CollisionShape.class);
            float r2 = shape2 == null ? 0.01f : shape2.getRadius();

            boolean smallImpact = false;
            if( r1 < 0.3 || r2 < 0.3 ) {
                smallImpact = true;
            }
            if( r1 < 0.6 && r2 < 0.6 ) {
                smallImpact = true;
            }

            if( smallImpact ) {
                bump2.playInstance();
            } else {
                bump1.playInstance();
            }
        }
        */
    }
}
