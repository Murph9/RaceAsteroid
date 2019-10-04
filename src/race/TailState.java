package race;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

import component.CollisionShape;
import component.Colour;
import component.ModelType;
import component.Position;
import component.Tail;

public class TailState extends BaseAppState {

    private final Map<EntityId, TailObject> tails;

    private SimpleApplication app;
    private EntityData ed;
	private EntitySet entities;

    private final float TAIL_STEP = 0.1f;
    private final float TAIL_SIZE = 10;
    private float curStep;

    public TailState() {
        this.tails = new HashMap<>();
        this.curStep = TAIL_STEP;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        this.ed = getState(EntityDataState.class).getEntityData();
        this.entities = this.ed.getEntities(Tail.class, Position.class);
    }

    @Override
    protected void cleanup(Application app) {
        entities.release();
        entities = null;
    }

    @Override
    protected void onEnable() {
        
    }

    @Override
    protected void onDisable() {
        
    }
    
    @Override
    public void update(float tpf) {
        if (entities.applyChanges()) {
            removeModels(entities.getRemovedEntities());
            addModels(entities.getAddedEntities());
        }

        curStep -= tpf;
        if (curStep > 0) {
            return; //don't add if its not ready
        }
        curStep = TAIL_STEP;
        for (Entity e: entities) {
            Vector3f pos = e.get(Position.class).getLocation();
            TailObject tail = tails.get(e.getId());

            if (tail.lastPos == null) {
                tail.lastPos = pos;
                continue; //if no last pos set it for next round
            }

            //remove old tail pieces
            if (tail.lines.size() > TAIL_SIZE) { //IDEA maybe move count to tail object
                EntityId delE = tail.lines.remove();
                ed.removeEntity(delE);
            }
            
            EntityId e2 = ed.createEntity();
            ed.setComponents(e2,
                new Position(tail.lastPos),
                CollisionShape.Line(pos.subtract(tail.lastPos), true),
                new Colour(ColorRGBA.Cyan),
                new ModelType(RaceGLModelFactory.MODEL_LINE));
            
            tail.lines.add(e2);
            tail.lastPos = pos.clone();
        }
    }

    private void removeModels(Set<Entity> entities) {
        for (Entity e : entities) {
            TailObject tail = tails.remove(e.getId());
            tail.rootNode.removeFromParent();
            for (EntityId subE: tail.lines)
                ed.removeEntity(subE);
        }
    }

    private void addModels(Set<Entity> entities) {

        for (Entity e : entities) {
            TailObject tail = new TailObject();
            tails.put(e.getId(), tail);
            this.app.getRootNode().attachChild(tail.rootNode);
        }
    }

    class TailObject {
        Vector3f lastPos;
        Node rootNode = new Node();
        Queue<EntityId> lines = new LinkedList<EntityId>();
    }
}