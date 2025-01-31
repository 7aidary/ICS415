import org.joml.Matrix3f;
import org.joml.Vector3f;
public class Camera {
    public Vector3D position;
    public Matrix3f rotation;

    private static final double VIEWPORT_SIZE = 1.0;
    private static final double PROJECTION_PLANE_Z = 1.0;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public Camera(Vector3D position, Matrix3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3D getRayDirection(int x, int y) {
        Vector3D viewportDir = canvasToViewport(x, y); // Convert screen coordinates to viewport coordinates

        // Convert to Vector3f
        Vector3f temp = new Vector3f((float) viewportDir.x, (float) viewportDir.y, (float) viewportDir.z);

        // Apply rotation
        temp = rotation.transform(new Vector3f(temp)); // Ensure it returns a new transformed vector

        // Normalize the ray direction
        temp.normalize();

        // Convert back to Vector3D
        return new Vector3D(temp.x, temp.y, temp.z);
    }
    public Vector3D canvasToViewport(int x, int y) {

        final double aspectRatio = (double) HEIGHT / WIDTH;
        final double viewportWidth = 1.0 / aspectRatio;
        return new Vector3D(
                x * VIEWPORT_SIZE / WIDTH,
               - y * VIEWPORT_SIZE / HEIGHT *aspectRatio,
                PROJECTION_PLANE_Z
        ); }
}
