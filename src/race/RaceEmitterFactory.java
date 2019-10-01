package race;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;

import component.Emit;

public class RaceEmitterFactory implements EmitterFactory {

    public static String EXPLOSION = "EXPLOSION";

    private AssetManager am;
    private EntityData ed;

    public void setState(EmitterState state) {
        this.am = state.getApplication().getAssetManager();
        this.ed = state.getApplication().getStateManager().getState(EntityDataState.class).getEntityData();
    }

    @Override
    public ParticleEmitter createEmitter(Entity e) {
        Emit emit = ed.getComponent(e.getId(), Emit.class);
        if (emit.getType().equals(EXPLOSION)) {
            return createExplosion();
        }
        return null;
    }

    private ParticleEmitter createExplosion() {
        ParticleEmitter fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat_red = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", am.loadTexture("assets/Textures/blob.png"));
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