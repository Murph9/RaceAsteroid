package race;

import com.jme3.math.ColorRGBA;
import com.simsilica.es.EntityComponent;

public class Colour implements EntityComponent {

	private ColorRGBA c;
	
	public Colour(ColorRGBA c) {
		this.c = c;
	}
	
	public ColorRGBA getColour() {
		return c;
	}
	
	@Override
	public String toString() {
		return "Colour[" + c + "]";
	}
}
