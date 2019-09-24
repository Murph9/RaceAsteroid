package race.component;

import com.simsilica.es.EntityComponent;


/**
 *  Represents the mass of an entity.  A mass of 0 is
 *  for static objects... or infinite mass.
 *
 *  @author    Paul Speed
 */
public class Mass implements EntityComponent
{
    private double mass;
    private double invMass;

    public Mass( double mass ) {
        this.mass = mass;
        this.invMass = mass == 0 ? 0 : 1.0/mass;
    }

    public double getMass() {
        return mass;
    }

    public double getInvMass() {
        return invMass;
    }

    @Override
    public String toString() {
        return "Mass[" + mass + "]";
    }
}
