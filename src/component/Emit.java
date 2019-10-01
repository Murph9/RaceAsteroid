package component;

import com.simsilica.es.EntityComponent;

public class Emit implements EntityComponent {

    private String type;

    public Emit(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "Emit[]";
    }
}