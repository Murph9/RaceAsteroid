package race;

import com.jme3.math.Vector3f;

public class H {

	//https://ideone.com/PnPJgb
	//lines AB and CD
	public static class IntersectResult {
		boolean success;
		Vector3f pos;
		float t;
		
		public IntersectResult(boolean success, Vector3f pos, float t) {
			this.success = success;
			this.pos = pos;
			this.t = t;
		}
	}
	public static IntersectResult linesIntersectV3(Vector3f A, Vector3f B, Vector3f C, Vector3f D) {
		Vector3f CmP = C.subtract(A);
		Vector3f r = B.subtract(A);
		Vector3f s = D.subtract(C);
		
		float CmPxr = CmP.x * r.y - CmP.y * r.x;
		if (CmPxr == 0f) {
			// Lines are collinear, and so intersect if they have any overlap
 			if (((C.x - A.x < 0f) != (C.x - B.x < 0f)))
 				return new IntersectResult(true, C, 0); //TODO test
 			else if ((C.y - A.y < 0f) != (C.y - B.y < 0f))
 				return new IntersectResult(true, C, 0); //TODO test
 			
 			return new IntersectResult(false, null, 0);//co-linear but not overlapping 
		}
		
		float CmPxs = CmP.x * s.y - CmP.y * s.x;
		float rxs = r.x * s.y - r.y * s.x;
		if (rxs == 0)
			return new IntersectResult(false, null, 0); //lines are parallel so no collision
		
		float rxsr = 1f / rxs;
		float t = CmPxs * rxsr;
		float u = CmPxr * rxsr;
		
		Vector3f out_ = A.add(r.mult(t));
		if ((t >= 0f) && (t <= 1f) && (u >= 0f) && (u <= 1f))
			return new IntersectResult(true, out_, t);
		
		return new IntersectResult(false, null, 0);
	}
	
	//shorthand for new Vector3f
	public static Vector3f v3(float x, float y, float z) {
		return new Vector3f(x,y,z);
	}
}
