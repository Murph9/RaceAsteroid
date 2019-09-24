package race;

import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;


/**
 *  Called by the ModelState to create new Spatials when required.
 *  @author Paul Speed
 */
public interface ModelFactory {

    public void setState( ModelState state );
    public Spatial createModel( Entity e );
}
