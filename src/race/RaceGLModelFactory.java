package race;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;

import component.CollisionShape;
import component.Colour;
import component.ModelType;


/**
 * Implements non sprite based spatials, for the alternative:
 * @see RaceModelFactory
 */
public class RaceGLModelFactory implements ModelFactory {

    public static final String MODEL_WALL = "wall";
    public static final String MODEL_LINE = "line";

    private AssetManager assets;
    private EntityData ed;
    
    @Override
    public void setState(ModelState state) {
        this.assets = state.getApplication().getAssetManager();
        this.ed = state.getApplication().getStateManager().getState(EntityDataState.class).getEntityData();
    }

    @Override
    public Spatial createModel(Entity e) {
        ModelType type = e.get(ModelType.class);
        Colour c = ed.getComponent(e.getId(), Colour.class);
        
        if (MODEL_WALL.equals(type.getType())) {
            return createLine("Wall", ed.getComponent(e.getId(), CollisionShape.class).getDir(),
                    (c != null ? c.getColour() : ColorRGBA.White));
        } else if (MODEL_LINE.equals(type.getType())) {
            return createLine("Line", ed.getComponent(e.getId(), CollisionShape.class).getDir(),
                    (c != null ? c.getColour() : ColorRGBA.Gray));
        } else {
            try {
                throw new Exception("Unknown type: " + type.getType());
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public boolean accepts(Entity e) {
        String type = e.get(ModelType.class).getType();
        return type.equals(RaceGLModelFactory.MODEL_WALL) || type.equals(RaceGLModelFactory.MODEL_LINE);
    }
 
    protected Geometry createLine(String name, Vector3f end, ColorRGBA color) {
        Line l = new Line(new Vector3f(0, 0, 0), end);
        Geometry g = new Geometry(name, l);
        Material mat = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.setQueueBucket(Bucket.Opaque);
        return g;
    }
}