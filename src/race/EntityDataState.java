package race;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;


/**
 *  AppState providing convenient access to the global
 *  ES entity data.  It also properly cleans up the ES
 *  upon termination.
 *
 *  @author    Paul Speed
 */
public class EntityDataState extends BaseAppState {
    private EntityData entityData;

    public EntityDataState() {
        this(new DefaultEntityData());
    }

    public EntityDataState( EntityData ed ) {
        this.entityData = ed;
    }

    public EntityData getEntityData() {
        return entityData;
    }

    @Override
    protected void initialize( Application app ) {
    }

    @Override
    protected void cleanup( Application app ) {
        entityData.close();
        entityData = null; // cannot be reused
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
