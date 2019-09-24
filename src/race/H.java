package race;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class H {

	public static Vector2f v3tov2fXY(Vector3f v) {
		return new Vector2f(v.x, v.y);
	}

	//https://ideone.com/PnPJgb
	//lines AB and CD
	public static class IntersectResult {
		public final boolean success;
		public final Vector2f pos;
		public final float t;
		
		public IntersectResult(boolean success, Vector2f pos, float t) {
			this.success = success;
			this.pos = pos;
			this.t = t;
		}
	}
	public static IntersectResult linesIntersectXY(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
		return linesIntersect(H.v3tov2fXY(a), H.v3tov2fXY(b), H.v3tov2fXY(c), H.v3tov2fXY(d));
	}
	public static IntersectResult linesIntersect(Vector2f a, Vector2f b, Vector2f c, Vector2f d) {
		Vector2f CmP = c.subtract(a);
		Vector2f r = b.subtract(a);
		Vector2f s = d.subtract(c);
		
		float CmPxr = CmP.x * r.y - CmP.y * r.x;
		if (CmPxr == 0f) {
			// Lines are collinear, and so intersect if they have any overlap
 			if (((c.x - a.x < 0f) != (c.x - b.x < 0f)))
 				return new IntersectResult(true, c, 0);
 			else if ((c.y - a.y < 0f) != (c.y - b.y < 0f))
 				return new IntersectResult(true, c, 0);
 			
 			return new IntersectResult(false, null, 0);//co-linear but not overlapping 
		}
		
		float CmPxs = CmP.x * s.y - CmP.y * s.x;
		float rxs = r.x * s.y - r.y * s.x;
		if (rxs == 0)
			return new IntersectResult(false, null, 0); //lines are parallel so no collision
		
		float rxsr = 1f / rxs;
		float t = CmPxs * rxsr;
		float u = CmPxr * rxsr;
		
		Vector2f out_ = a.add(r.mult(t));
		if ((t >= 0f) && (t <= 1f) && (u >= 0f) && (u <= 1f))
			return new IntersectResult(true, out_, t);
		
		return new IntersectResult(false, null, 0);
	}



	public static String roundDecimal(float num, int places) {
		if (places == 0) {
			return Integer.toString(Math.round(num));
		}
		return String.format("%." + places + "f", num);
	}

	public static String roundDecimal(double num, int places) {
		return roundDecimal((float) num, places);
	}
	
	public static String round3f(Vector3f vec, int places) {
		if (vec == null)
			return "x:?, y:?, z:?";
		return "x:" + H.roundDecimal(vec.x, places) + ", y:" + H.roundDecimal(vec.y, places) + ", z:" + H.roundDecimal(vec.z, places);
	}


	public static Quaternion addScaledVector(Quaternion orientation, Vector3f v, double scale) {

		double x = orientation.getX();
		double y = orientation.getY();
		double z = orientation.getZ();
		double w = orientation.getW();

		Quaternion q = new Quaternion((float) (v.x * scale), (float) (v.y * scale), (float) (v.z * scale), 0);
		q.multLocal(orientation);

		x = x + q.getX() * 0.5;
		y = y + q.getY() * 0.5;
		z = z + q.getZ() * 0.5;
		w = w + q.getW() * 0.5;

		return new Quaternion((float) x, (float) y, (float) z, (float) w);
	}
}
