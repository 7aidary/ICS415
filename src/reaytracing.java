import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class raytracing  {
    static final int WIDTH = 800;  // Canvas width
    static final int HEIGHT = 800; // Canvas height
    static final double VIEWPORT_SIZE = 1.0;
    static final double PROJECTION_PLANE_Z = 1.0;
    static final int BACKGROUND_COLOR = 0xffffff; // white

    List<Sphere> spheres = new ArrayList<>();

    public raytracing() {
        // Add spheres to the scene
        spheres.add(new Sphere(new Vector3D(0, -1, 3), 1, 0xFF0000)); // Red sphere
        spheres.add(new Sphere(new Vector3D(2, 0, 4), 1, 0x00FF00)); // Green sphere
        spheres.add(new Sphere(new Vector3D(-2, 0, 4), 1, 0x0000FF));// Blue sphere
    }

    public Vector3D canvasToViewport(int x, int y) {
        return new Vector3D(
                x * VIEWPORT_SIZE / WIDTH,
                y * VIEWPORT_SIZE / HEIGHT,
                PROJECTION_PLANE_Z
        );
    }

    public int traceRay(Vector3D origin, Vector3D direction, double tMin, double tMax) {
        double closestT = Double.POSITIVE_INFINITY;
        Sphere closestSphere = null;

        for (Sphere sphere : spheres) {
            double[] tValues = intersectRaySphere(origin, direction, sphere);
            if (tValues != null) {
                if (tValues[0] >= tMin && tValues[0] <= tMax && tValues[0] < closestT) {
                    closestT = tValues[0];
                    closestSphere = sphere;
                }
                if (tValues[1] >= tMin && tValues[1] <= tMax && tValues[1] < closestT) {
                    closestT = tValues[1];
                    closestSphere = sphere;
                }
            }
        }

        return (closestSphere != null) ? closestSphere.color : BACKGROUND_COLOR;
    }

    public double[] intersectRaySphere(Vector3D origin, Vector3D direction, Sphere sphere) {
        Vector3D CO = origin.subtract(sphere.center);
        double a = direction.dot(direction);
        double b = 2 * CO.dot(direction);
        double c = CO.dot(CO) - sphere.radius * sphere.radius;

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return null; // No intersection

        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
        return new double[]{t1, t2};
    }

    public void render(String filename) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int x = -WIDTH / 2; x < WIDTH / 2; x++) {
            for (int y = -HEIGHT / 2; y < HEIGHT / 2; y++) {
                Vector3D direction = canvasToViewport(x, y).normalize();
                int color = traceRay(new Vector3D(0, 0, 0), direction, 1, Double.POSITIVE_INFINITY);
                image.setRGB(x + WIDTH / 2, HEIGHT / 2 - y - 1, color);
            }
        }

        ImageIO.write(image, "png", new File(filename));
    }

    public static void main(String[] args) throws IOException {
        raytracing rayTracer = new raytracing();
        rayTracer.render("output.png");
    }
}