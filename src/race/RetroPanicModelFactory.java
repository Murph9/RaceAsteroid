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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;

/**
 * Implements the Asteroid Panic spatials as quads with sprites selected from a
 * sprite sheet.
 *
 * @author Paul Speed
 */
public class RetroPanicModelFactory implements ModelFactory {

	public static final String MODEL_SHIP = "ship";
	public static final String MODEL_THRUST = "thrust";
	public static final String MODEL_WALL = "wall"; //has collision
	public static final String MODEL_LINE = "line"; //does not have collision

	private ModelState state;
	private AssetManager assets;
	private EntityData ed;

	private Texture sprites;

	private static final float cellSize = 128f / 1024f;

	public void setState(ModelState state) {
		this.state = state;
		this.assets = state.getApplication().getAssetManager();
		this.ed = state.getApplication().getStateManager().getState(EntityDataState.class).getEntityData();

		sprites = assets.loadTexture("assets/Textures/panic-sprites.png");
	}

	private float[] spriteCoords(int x, int y) {
		float s = x * cellSize;
		float t = y * cellSize;
		return new float[] { s, t, s + cellSize, t, s + cellSize, t + cellSize, s, t + cellSize };
	}

	protected Geometry createSprite(String name, float size, ColorRGBA color, int x, int y) {
		Quad quad = new Quad(size, size);
		quad.setBuffer(Type.TexCoord, 2, spriteCoords(x, y));

		float halfSize = size * 0.5f;
		quad.setBuffer(Type.Position, 3, new float[] { -halfSize, -halfSize, 0, halfSize, -halfSize, 0, halfSize,
				halfSize, 0, -halfSize, halfSize, 0 });
		quad.updateBound();

		Geometry geom = new Geometry(name, quad);

		Material mat = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", sprites);
		mat.setColor("Color", color);
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Additive);
		geom.setMaterial(mat);

		return geom;
	}

	protected Geometry createLine(String name, Vector3f end, ColorRGBA color) {
		Line l = new Line(new Vector3f(0, 0, 0), end);
		Geometry g = new Geometry(name, l);
		Material mat = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		g.setMaterial(mat);
		return g;
	}

	public Spatial createModel(Entity e) {

		ModelType type = e.get(ModelType.class);
		Colour c = ed.getComponent(e.getId(), Colour.class);
		
		if (MODEL_SHIP.equals(type.getType())) {
			CollisionShape cs = ed.getComponent(e.getId(), CollisionShape.class);
			float radius = cs == null ? 0.1f : cs.getRadius();
			return createSprite("Ship", radius * 4, (c!=null?c.getColour():ColorRGBA.Cyan), 0, 7);
		} else if (MODEL_THRUST.equals(type.getType())) {
			return createSprite("Thrust", 0.5f, (c!=null?c.getColour():ColorRGBA.Red), 0, 5);
		} else if (MODEL_WALL.equals(type.getType())) {
			return createLine("Wall", ed.getComponent(e.getId(), CollisionShape.class).getDir(), (c!=null?c.getColour():ColorRGBA.White));
		} else if (MODEL_LINE.equals(type.getType())) {
			return createLine("Line", ed.getComponent(e.getId(), CollisionShape.class).getDir(), (c!=null?c.getColour():ColorRGBA.Gray));
		} else {
			try {
				throw new Exception("Unknown type: " + type.getType());
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}
}
