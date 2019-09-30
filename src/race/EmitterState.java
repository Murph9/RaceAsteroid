package race;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

import component.Emit;
import component.Position;

public class EmitterState extends BaseAppState {

    private SimpleApplication app;
    private EntityData ed;
    private EntitySet entities;
    private final Map<EntityId, ParticleEmitter> models;

    public EmitterState() {
        this.models = new HashMap<>();
    }

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        this.ed = getState(EntityDataState.class).getEntityData();
        this.entities = this.ed.getEntities(Emit.class, Position.class);
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
            ParticleEmitter emitter = createExplosion();
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


    //TODO add other types like the image app state
    private ParticleEmitter createExplosion() {
        ParticleEmitter fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat_red = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", app.getAssetManager().loadTexture("assets/Textures/blob.png"));
        fire.setMaterial(mat_red);
        fire.setEndColor(ColorRGBA.Red);
        fire.setStartColor(ColorRGBA.Yellow);
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 1, 0));
        fire.setStartSize(.1f);
        fire.setEndSize(.6f);
        fire.setGravity(0, 0, 0);
        fire.setLowLife(0.4f);
        fire.setHighLife(1f);
        fire.getParticleInfluencer().setVelocityVariation(0.4f);

        return fire;
    }
}