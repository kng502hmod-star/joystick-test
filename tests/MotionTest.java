import com.example.joystickvisual.Motion;

public final class MotionTest {
    public static void main(String[] args) {
        for (int mode=0;mode<3;mode++) for(float speed:new float[]{0.25f,1f,2.5f}) {
            float[] initial=Motion.sample(0,mode,speed);
            boolean moves=false;
            for(int i=0;i<2000;i++) {
                float[] p=Motion.sample(i/30.0,mode,speed);
                if(!Float.isFinite(p[0])||!Float.isFinite(p[1])||Math.hypot(p[0],p[1])>0.781)throw new AssertionError("Out of range");
                if(Math.hypot(p[0]-initial[0],p[1]-initial[1])>0.1)moves=true;
            }
            if(!moves)throw new AssertionError("No movement");
        }
        System.out.println("PASS: all modes remain bounded and animate at all tested speeds.");
    }
}
