package race;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;

public class BackgroundState extends BaseAppState {

    private final float Z_DEPTH = 0.9100009f;
    private final float Z_WORLD_DEPTH = -0.0001f;
    private final float xDist; //TODO use
    private final float yDist;

    private Node rootNode;

    public BackgroundState(float xDist, float yDist) {
        this.xDist = xDist;
        this.yDist = yDist;
    }

    @Override
    protected void initialize(Application app) {
        SimpleApplication sa = (SimpleApplication) app;
        
        rootNode = new Node("Background grid root");
        sa.getRootNode().attachChild(rootNode);

        redrawBackground(sa.getCamera());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        redrawBackground(getApplication().getCamera());
    }

    private void redrawBackground(Camera cam) {
        rootNode.detachAllChildren();

        //already offset by camera pos
        Vector3f min = cam.getWorldCoordinates(new Vector2f(0, 0), Z_DEPTH);
        Vector3f max = cam.getWorldCoordinates(new Vector2f(cam.getWidth(), cam.getHeight()), Z_DEPTH);

        int minWorldX = (int)Math.ceil((double)min.x);
        int minWorldY = (int)Math.ceil((double)min.y);
        int maxWorldX = (int)Math.ceil((double)max.x);
        int maxWorldY = (int)Math.ceil((double)max.y);

        for (int x = minWorldX; x < maxWorldX; x++) {
            Vector3f start = new Vector3f(x, minWorldY, Z_WORLD_DEPTH);
            Vector3f end = new Vector3f(x, maxWorldY, Z_WORLD_DEPTH);
            rootNode.attachChild(createLine("grid", start, end, ColorRGBA.Green.mult(0.3f)));
        }
        
        for (int y = minWorldY; y < maxWorldY; y++) {
            Vector3f start = new Vector3f(minWorldX, y, Z_WORLD_DEPTH);
            Vector3f end = new Vector3f(maxWorldX, y, Z_WORLD_DEPTH);
            rootNode.attachChild(createLine("grid", start, end, ColorRGBA.Green.mult(0.3f)));
        }
    }

    @Override
    protected void cleanup(Application app) {
        rootNode.removeFromParent();
        rootNode = null;
    }

    @Override
    protected void onDisable() {
    }

    @Override
    protected void onEnable() {
    }
    
    private Geometry createLine(String name, Vector3f start, Vector3f end, ColorRGBA color) {
        Line l = new Line(start, end);
        Geometry g = new Geometry(name, l);
        Material mat = new Material(getApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        g.setMaterial(mat);
        return g;
    }
}