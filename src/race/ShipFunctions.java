package race;

import com.jme3.input.KeyInput;

import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;


/**
 *  Defines a set of standard input function IDs and their default
 *  control mappings.  The ShipControlState uses these function IDs
 *  as triggers to the ship control.
 *
 *  @author    Paul Speed
 */
public class ShipFunctions {

    /**
     *  The group to which these functions are assigned for
     *  easy grouping and also for easy activation and deactivation
     *  of the entire group.
     */
    public static final String GROUP = "Ship Controls";

    /**
     *  Turns the ship left or right.  Default controls are setup
     *  as 'a' and 'd'.
     */
    public static final FunctionId F_TURN = new FunctionId(GROUP, "Turn");

    /**
     *  Thrusts the ship forward in its current direction.  Default control
     *  mapping is 'w'.
     */
    public static final FunctionId F_THRUST = new FunctionId(GROUP, "Thrust");

    /**
     *  Initializes a default set of input mappings for the ship functions.
     *  These can be changed later without impact... or multiple input
     *  controls can be mapped to the same function.
     */
    public static void initializeDefaultMappings( InputMapper inputMapper ) {
        // Default key mappings
        inputMapper.map(F_TURN, KeyInput.KEY_A);
        inputMapper.map(F_TURN, KeyInput.KEY_LEFT);
        
        inputMapper.map(F_TURN, InputState.Negative, KeyInput.KEY_D);
        inputMapper.map(F_TURN, InputState.Negative, KeyInput.KEY_RIGHT);
        
        inputMapper.map(F_THRUST, KeyInput.KEY_W);
        inputMapper.map(F_THRUST, KeyInput.KEY_UP);
    }
}
