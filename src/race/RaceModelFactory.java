package race;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;

import component.CollisionShape;
import component.Colour;
import component.ModelType;

/**
 * Implements spatials as quads with sprites selected from a sprite sheet.
 */
public class RaceModelFactory implements ModelFactory {

	public static final String MODEL_SHIP = "ship";
	public static final String MODEL_THRUST = "thrust";

	private AssetManager assets;
	private EntityData ed;

	private Texture sprites;

	private static final float cellSize = 128f / 1024f;

	@Override
	public void setState(ModelState state) {
		this.assets = state.getApplication().getAssetManager();
		this.ed = state.getApplication().getStateManager().getState(EntityDataState.class).getEntityData();

		sprites = assets.loadTexture("assets/Textures/sprites.png");
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
		
		geom.setQueueBucket(Bucket.Transparent);
		geom.setMaterial(mat);

		return geom;
	}

	@Override
	public boolean accepts(Entity e) {
		String type = e.get(ModelType.class).getType();
		return type.equals(RaceModelFactory.MODEL_SHIP) || type.equals(RaceModelFactory.MODEL_THRUST);
	}

	@Override
	public Spatial createModel(Entity e) {
		ModelType type = e.get(ModelType.class);
		Colour c = ed.getComponent(e.getId(), Colour.class);
		
		if (MODEL_SHIP.equals(type.getType())) {
			CollisionShape cs = ed.getComponent(e.getId(), CollisionShape.class);
			float radius = cs == null ? 0.1f : cs.getRadius();
			return createSprite("Ship", radius * 4, (c!=null?c.getColour():ColorRGBA.Cyan), 0, 7);
		} else if (MODEL_THRUST.equals(type.getType())) {
			return createSprite("Thrust", 0.5f, (c!=null?c.getColour():ColorRGBA.Red), 0, 5);
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
