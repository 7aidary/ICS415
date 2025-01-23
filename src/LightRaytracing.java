import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class LightRaytracing  {
    static final int WIDTH = 800;  // Canvas width
    static final int HEIGHT = 800; // Canvas height
    static final double VIEWPORT_SIZE = 1.0;
    static final double PROJECTION_PLANE_Z = 1.0;
    static final int BACKGROUND_COLOR =0xFFFFFF ; // white

    private List<Light> sceneLights;

    List<Sphere> spheres = new ArrayList<>();



    public LightRaytracing(List<Light> sceneLights){
        // Add spheres to the scene
        spheres.add(new Sphere(new Vector3D(0, -1, 3), 1, 0xFF0000,100)); // Red sphere
        spheres.add(new Sphere(new Vector3D(2, 0, 4), 1, 0x00FF00,20)); // Green sphere
        spheres.add(new Sphere(new Vector3D(-2, 0, 4), 1, 0x0000FF,10)); // Blue sphere
        spheres.add(new Sphere(new Vector3D(0, -5001, 0), 5000,0xFFFF00,1000));
        this.sceneLights=sceneLights;
    }

    public Vector3D canvasToViewport(int x, int y) {
        return new Vector3D(
                x * VIEWPORT_SIZE / WIDTH,
                y * VIEWPORT_SIZE / HEIGHT,
                PROJECTION_PLANE_Z
        );
    }

    public double traceRay(Vector3D origin, Vector3D direction, double tMin, double tMax) {
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
        // If no sphere was hit, return the background color
        if (closestSphere == null) {
            return BACKGROUND_COLOR;
        }


        // Compute intersection point and normal
        Vector3D P = origin.add(direction.multiply(closestT)); // P = O + closest_t * D
        Vector3D N = P.subtract(closestSphere.center).normalize(); // N = (P - center).normalize()

        double lightIntensity = computeLighting(P , N ,direction.negate(), closestSphere.specular);
        // Compute lighting and return the color
        return applyLighting(closestSphere.color , lightIntensity);
    }
    // Modifies a color based on lighting intensity
    public static int applyLighting(int color, double intensity) {
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * intensity)); // Red channel
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * intensity));  // Green channel
        int b = Math.min(255, (int) ((color & 0xFF) * intensity));         // Blue channel
        return (r << 16) | (g << 8) | b; // Combine channels into a single RGB color
    }
    // Compute the lighting at point P
    public double computeLighting(Vector3D P, Vector3D N, Vector3D V, int specular) {
        double intensity = 0.0;

        for (Light light : sceneLights) {
            Vector3D L = null; // Light vector

            if (light instanceof Light.AmbientLight) {
                // Ambient light contributes directly
                intensity += light.getIntensity();
                continue;
            }

            // Determine light direction for point and directional lights
            if (light instanceof Light.PointLight) {
                Light.PointLight pointLight = (Light.PointLight) light; // Downcast to PointLight
                L = pointLight.getPosition().subtract(P); // Direction from point to light
            } else if (light instanceof Light.DirectionalLight) {
                Light.DirectionalLight directionalLight = (Light.DirectionalLight) light; // Downcast to DirectionalLight
                L = directionalLight.getDirection(); // Predefined direction
            }

            if (L == null) continue; // Skip if L is not set (unlikely but ensures safety)

            // Diffuse lighting
            double nDotL = N.dot(L);
            if (nDotL > 0) {
                intensity += light.getIntensity() * nDotL / (N.length() * L.length());
            }

            // Specular lighting
            if (specular != -1) {
                Vector3D R = N.multiply(2 * N.dot(L)).subtract(L); // R = 2 * N * dot(N, L) - L
                double rDotV = R.dot(V);
                if (rDotV > 0) {
                    intensity += light.getIntensity() * Math.pow(rDotV / (R.length() * V.length()), specular);
                }
            }
        }

        return intensity;
    }
    // Scale function

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
                int color = (int) traceRay(new Vector3D(0, 0, 0), direction, 1, Double.POSITIVE_INFINITY);
                //System.out.println(Integer.parseInt(color.rgbToHex()));
                image.setRGB(x + WIDTH / 2, HEIGHT / 2 - y - 1, color);
            }
        }

        ImageIO.write(image, "png", new File(filename));
    }
    /* type = ambient
             intensity = 0.2
 }
     light {
         type = point
         intensity = 0.6
         position = (2, 1, 0)
     }
     light {
         type = directional
         intensity = 0.2
         direction = (1, 4, 4)
*/

    public static void main(String[] args) throws IOException {
        Light.AmbientLight ambientLight = new Light.AmbientLight(0.2);
        Light.PointLight pointLight = new Light.PointLight(0.6, new Vector3D(2,1,0));
        Light.DirectionalLight directionalLight = new Light.DirectionalLight(0.2, new Vector3D(1,4,4));
        List<Light> lights = new ArrayList<>();
        lights.add(directionalLight );
        lights.add(ambientLight );
        lights.add(pointLight );

        LightRaytracing rayTracer = new LightRaytracing(lights);
        rayTracer.render("output2.png");
    }
}
