package race;

import com.jme3.effect.ParticleEmitter;
import com.simsilica.es.Entity;

public interface EmitterFactory {
    public void setState(EmitterState state);

    public ParticleEmitter createEmitter(Entity e);
}