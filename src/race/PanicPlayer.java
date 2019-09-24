package race;

public class PanicPlayer {

	public static final float COLLISION_TIME = 1;
	
    private float collision = 0;

    public PanicPlayer( int ships ) {
    }

    public void setCollision(float set) {
    	collision = set;
    }
    public float getCollision() {
    	return collision;
    }
}

