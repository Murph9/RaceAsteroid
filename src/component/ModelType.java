package component;

import com.simsilica.es.EntityComponent;


/**
 *  A general "model type" used for entities with a visual display.
 */
public class ModelType implements EntityComponent {
    private String type;

    public ModelType( String type ) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return "ModelType[" + type + "]";
    }
}
