package component;

import com.simsilica.es.EntityComponent;

public class AccelModifier implements EntityComponent {

    private float value;

    public AccelModifier(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AccelModifier["+value+"]";
    }
}