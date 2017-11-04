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
import com.simsilica.es.EntityComponent;


/**
 *  Direction and magnitude of line.
 *  Start is the position of the object
 *
 *  @author    murph
 */
public class CollisionShape implements EntityComponent
{
	private final static String TYPE_CIRCLE = "circle";
	private final static String TYPE_LINE = "line";
	
	private String type;
	private Vector3f dir;
	private float radius;
	
	public static CollisionShape Line(Vector3f dir) {
		return new CollisionShape(TYPE_LINE, dir, 0);
	}
	public static CollisionShape Circle(float radius) {
		return new CollisionShape(TYPE_CIRCLE, null, radius);
	}
	
	private CollisionShape(String type, Vector3f dir, float radius) {
		this.type = type;
		this.dir = dir;
		this.radius = radius;
	}

    public Vector3f getDir() {
    	if (!type.equals(TYPE_LINE)) {
			try {
				throw new Exception("Not valid for type: " + type);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
    	}
        return dir;
    }
    public float getRadius() {
    	if (!type.equals(TYPE_CIRCLE)) {
			try {
				throw new Exception("Not valid for type: " + type);
			} catch (Exception e) {
				e.printStackTrace();
				return Float.NaN;
			}
    	}
        return radius;
    }
}
