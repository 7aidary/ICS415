public class Camera {
    public Vector3D position;
   // public Matrix3x3 rotation;
    static final int WIDTH = 800;  // Canvas width
    static final int HEIGHT = 800; // Canvas height
    static final double VIEWPORT_SIZE = 1.0;
    static final double PROJECTION_PLANE_Z = 1.0;

   /* public Camera(Vector3D position, Matrix3x3 rotation) {
        this.position = position;
        this.rotation = rotation;
    }*/

    public Vector3D canvasToViewport(int x, int y) {
        return new Vector3D(
                x * VIEWPORT_SIZE / WIDTH,
                -y * VIEWPORT_SIZE / HEIGHT,
                PROJECTION_PLANE_Z
        );
    }

   /* public Vector3D getRayDirection(int x, int y) {
        Vector3D viewportDir = canvasToViewport(x, y); // The viewport direction
        return rotation.multiply(viewportDir); // Apply the rotation to the viewport direction
    }*/
}