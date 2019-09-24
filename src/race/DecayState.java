package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

import component.Decay;
import component.Die;
import component.Position;

/**
 * General app state that watches entities with a Decay component and deletes
 * them when their time is up.
 *
 * @author Paul Speed
 */
public class DecayState extends BaseAppState {

    private EntityData ed;
    private EntitySet entities;

    @Override
    protected void initialize(Application app) {
        ed = getState(EntityDataState.class).getEntityData();
        entities = ed.getEntities(Decay.class, Position.class);
    }

    @Override
    protected void cleanup(Application app) {
        // Release the entity set we grabbed previously
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
        entities.applyChanges();
        for (Entity e : entities) {

            Decay d = e.get(Decay.class);
            if (d.getPercent() >= 1.0) {
                ed.removeEntity(e.getId());

                // TODO this is self propagating
                // then cause explosion
                Vector3f pos = e.get(Position.class).getLocation();
                ed.setComponents(ed.createEntity(), new Die(), new Decay(250), new Position(pos));
            }
        }
    }
}
