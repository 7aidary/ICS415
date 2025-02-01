import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Transformation {
    static final int WIDTH = 800;
    static final int HEIGHT = 800;
    static final double VIEWPORT_SIZE = 1.0;
    static final double PROJECTION_PLANE_Z = 1.0;
    static final int BACKGROUND_COLOR = 0xFFFFFF;

    private final List<Light> sceneLights;
    private final List<Sphere> spheres = new ArrayList<>();
    private Camera camera;

    public Transformation(List<Light> sceneLights, Camera camera) {
        Matrix4f globalTransform = new Matrix4f()
                .translate(1, 5, 0) // Example translation
                .rotateY((float) Math.toRadians(0)) // Example rotation
                .scale(1.0f); // Example scaling

        spheres.add(new Sphere(new Vector3D(0, -1, 3), 1, 0xFF0000, 100, 0.2, 1.5, 0.0, globalTransform));
        spheres.add(new Sphere(new Vector3D(2, 0, 4), 1, 0x00FF00, 0, 0.3, 1, 1.0, globalTransform));
        spheres.add(new Sphere(new Vector3D(-2, 0, 4), 1, 0x0000FF,40,0.4, 1.5, 0.0, globalTransform));
        spheres.add(new Sphere(new Vector3D(0, -5001, 0),500,0xFFFF00, 1000, 0.5, 0.2, 1.0, new Matrix4f().identity()));

        this.sceneLights = sceneLights;
        this.camera = camera;
    }

    public double traceRay(Vector3D origin, Vector3D direction, double tMin, double tMax, int recursion_depth) {
        if (recursion_depth <= 0) {
            return BACKGROUND_COLOR; // Stop recursion if depth is 0
        }

        IntersectionResult result = closestIntersection(origin, direction, tMin, tMax);

        // If no sphere was hit, return the background color
        if (result.closestSphere == null) {

            return BACKGROUND_COLOR;
        }
        // Compute intersection point and normal
        Vector3D P = origin.add(direction.multiply(result.closestT)); // P = O + closest_t * D
        Vector3D N = P.subtract(result.closestSphere.center).normalize(); // N = (P - center).normalize()

        double lightIntensity = computeLighting(P, N, direction.negate(), result.closestSphere.specular);
        // Compute lighting and return the color
        int localColor = applyLighting(result.closestSphere.color, lightIntensity);

        double r = result.closestSphere.reflective;
        double transparency = result.closestSphere.transparency;

        // If no reflection or transparency, return local color
        if (recursion_depth <= 0 || (r <= 0 && transparency <= 0)) {
            return localColor;
        }

        // Reflection computation
        int reflectedColor = 0;
        if (r > 0) {
            Vector3D R = Vector3D.reflectRay(direction.normalize().negate(), N);
            reflectedColor = (int) traceRay(P, R, 0.001, Double.POSITIVE_INFINITY, recursion_depth - 1);
        }

        // Transparency and refraction calculation
        int refractedColor = 0;
        if (transparency > 0) {
            Vector3D T = computeRefraction(direction, N, result.closestSphere.refraction);
            refractedColor = (int) traceRay(P, T, 0.001, Double.POSITIVE_INFINITY, recursion_depth - 1);
        }

        // Blend the local, reflected, and refracted colors based on the transparency/reflection properties
        int blendedColor = blendColors(localColor, reflectedColor, r); // First blend local + reflection
        blendedColor = blendColors(blendedColor, refractedColor, transparency); // Then blend with refraction

        // Combine the local, reflected, and refracted colors based on the transparency/reflection properties
        return blendedColor;
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

    public double computeLighting(Vector3D P, Vector3D N, Vector3D V, int specular) {
        double intensity = 0.0;
        double t_max = 1;
        for (Light light : sceneLights) {
            Vector3D L = null; // Light vector

            if (light instanceof Light.AmbientLight) {
                intensity += light.getIntensity(); // Ambient light contributes directly
            } else {
                if (light instanceof Light.PointLight) {
                    Light.PointLight pointLight = (Light.PointLight) light; // Downcast to PointLight
                    L = pointLight.getPosition().subtract(P); // Direction from point to light
                    t_max = 1;
                } else if (light instanceof Light.DirectionalLight) {
                    Light.DirectionalLight directionalLight = (Light.DirectionalLight) light; // Downcast to DirectionalLight
                    L = directionalLight.getDirection(); // Direction of the light
                    t_max = Double.POSITIVE_INFINITY;
                }

                IntersectionResult result = closestIntersection(P, L, 0.001, t_max);
                if (result.closestSphere != null) {
                    continue;
                }

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
        }

        return intensity;
    }
    public IntersectionResult closestIntersection(Vector3D O, Vector3D D, double tMin, double tMax) {
        double closestT = Double.POSITIVE_INFINITY;
        Sphere closestSphere = null;

        for (Sphere sphere : spheres) {
            double[] tValues = intersectRaySphere(O, D, sphere);
            if (tValues != null) {
                for (double t : tValues) {
                    if (t >= tMin && t <= tMax && t < closestT) {
                        closestT = t;
                        closestSphere = sphere;
                    }
                }
            }
        }
        return new IntersectionResult(closestSphere, closestT);
    }


    public Vector3D computeRefraction(Vector3D direction, Vector3D normal, double refractionIndex) {
        // Calculate the refractive index ratio (n1 / n2)
        double refractiveIndexRatio = 1.0 / refractionIndex;

        // Calculate the dot product between the direction of the ray and the normal
        double cosI = direction.dot(normal);

        // Calculate the angle using Snell's Law to determine refraction
        double sinT2 = refractiveIndexRatio * refractiveIndexRatio * (1 - cosI * cosI);

        // If total internal reflection occurs, return the reflected direction instead
        if (sinT2 > 1) {
            // Total internal reflection, reflect the ray instead
            return Vector3D.reflectRay(direction, normal);
        }

        // Calculate cosT (the cosine of the angle of refraction)
        double cosT = Math.sqrt(1.0 - sinT2);

        // Compute the refracted ray direction using Snell's Law
        Vector3D refractedDirection = direction.multiply(refractiveIndexRatio).subtract(
                normal.multiply(refractiveIndexRatio * cosI + cosT)
        );

        return refractedDirection.normalize();
    }

    public static int applyLighting(int color, double intensity) {
        int r = Math.min(255, (int) ((color >> 16 & 0xFF) * intensity));
        int g = Math.min(255, (int) ((color >> 8 & 0xFF) * intensity));
        int b = Math.min(255, (int) ((color & 0xFF) * intensity));

        return (r << 16) | (g << 8) | b;
    }
    public static int blendColors(int color1, int color2, double ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) (r1 * (1 - ratio) + r2 * ratio);
        int g = (int) (g1 * (1 - ratio) + g2 * ratio);
        int b = (int) (b1 * (1 - ratio) + b2 * ratio);

        return (r << 16) | (g << 8) | b;
    }

    public void render(String filename) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Vector3D direction = camera.getRayDirection(x - WIDTH / 2, y - HEIGHT / 2);
                int color = (int) traceRay(camera.position, direction, 0.001, Double.POSITIVE_INFINITY, 5);
                image.setRGB(x, y, color);
            }
        }
        ImageIO.write(image, "PNG", new File(filename));
    }

    public static void main(String[] args) throws IOException {
        Camera camera = new Camera(new Vector3D(0, 0, 0), new Matrix3f().identity());
        List<Light> lights = new ArrayList<>();
        lights.add(new Light.AmbientLight(0.2));
        lights.add(new Light.PointLight(0.6, new Vector3D(2, 1, 0)));
        lights.add(new Light.DirectionalLight(0.2, new Vector3D(1, 4, 4)));

        Transformation rayTracer = new Transformation(lights, camera);
        rayTracer.render("output5.png");
    }

    public static class IntersectionResult {
        public Sphere closestSphere;
        public double closestT;

        public IntersectionResult(Sphere closestSphere, double closestT) {
            this.closestSphere = closestSphere;
            this.closestT = closestT;
        }
    }
}
