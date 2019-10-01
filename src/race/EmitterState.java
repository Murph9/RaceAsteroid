package race;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

import component.Emit;
import component.Position;

public class EmitterState extends BaseAppState {

    private final Map<EntityId, ParticleEmitter> models;

    private SimpleApplication app;
    private EntityData ed;
    private EntitySet entities;
    private EmitterFactory factory;

    public EmitterState(EmitterFactory emitter) {
        this.models = new HashMap<>();
        this.factory = emitter;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        this.ed = getState(EntityDataState.class).getEntityData();
        this.entities = this.ed.getEntities(Emit.class, Position.class);
        this.factory.setState(this);
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
            updateModels(entities.getChangedEntities());
        }
    }

    private void removeModels(Set<Entity> entities) {
        for (Entity e : entities) {
            Spatial s = models.remove(e.getId());
            s.removeFromParent();
        }
    }

    private void addModels(Set<Entity> entities) {

        for (Entity e : entities) {
            ParticleEmitter emitter = this.factory.createEmitter(e);
            models.put(e.getId(), emitter);
            updateEffects(e, emitter);
            this.app.getRootNode().attachChild(emitter);
        }
    }

    private void updateModels(Set<Entity> entities) {
        for (Entity e : entities) {
            Spatial s = models.get(e.getId());
            updateEffects(e, s);
        }
    }

    private void updateEffects(Entity e, Spatial s) {
        Position p = e.get(Position.class);
        s.setLocalTranslation(p.getLocation());
    }
}