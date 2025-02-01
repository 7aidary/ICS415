import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

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
                .translate(1, 0, 0) // Example translation
                .rotateY((float) Math.toRadians(30)) // Example rotation
                .scale(2.0f); // Example scaling

        spheres.add(new Sphere(new Vector3D(0, -1, 3), 1, 0xFF0000, 100, 0.2, 1.5, 0.0, globalTransform));
        spheres.add(new Sphere(new Vector3D(2, 0, 4), 1, 0x00FF00, 0, 0.3, 1, 1.0, globalTransform));
        spheres.add(new Sphere(new Vector3D(-2, 0, 4), 1, 0x0000FF,40,0.4, 1.5, 0.0, globalTransform));
        spheres.add(new Sphere(new Vector3D(0, -5001, 0),500,0xFFFF00, 1000, 0.5, 0.2, 1.0, new Matrix4f().identity()));

        this.sceneLights = sceneLights;
        this.camera = camera;
    }

    public double traceRay(Vector3D origin, Vector3D direction, double tMin, double tMax, int recursionDepth) {
        if (recursionDepth <= 0) {
            return BACKGROUND_COLOR;
        }

        Sphere closestSphere = null;
        double closestT = Double.POSITIVE_INFINITY;

        for (Sphere sphere : spheres) {
            Matrix4f inverseTransform = sphere.inverseTransform;
            Vector4f localOrigin = new Vector4f((float) origin.x, (float) origin.y, (float) origin.z, 1.0f);
            Vector4f localDirection = new Vector4f((float) direction.x, (float) direction.y, (float) direction.z, 0.0f);

            localOrigin.mul(inverseTransform);
            localDirection.mulProject(inverseTransform);

            Vector3D transformedOrigin = new Vector3D(localOrigin.x, localOrigin.y, localOrigin.z);
            Vector3D transformedDirection = new Vector3D(localDirection.x, localDirection.y, localDirection.z).normalize();

            double[] ts = intersectRaySphere(transformedOrigin, transformedDirection, sphere);
            if (ts != null) {
                for (double t : ts) {
                    if (t > tMin && t < closestT) {
                        closestT = t;
                        closestSphere = sphere;
                    }
                }
            }
        }

        if (closestSphere == null) {
            return BACKGROUND_COLOR;
        }

        Vector4f P4 = new Vector4f(
                (float) (origin.x + closestT * direction.x),
                (float) (origin.y + closestT * direction.y),
                (float) (origin.z + closestT * direction.z),
                1.0f
        );
        P4.mul(closestSphere.transformMatrix);
        Vector3D P = new Vector3D(P4.x, P4.y, P4.z);

        Vector3D localNormal = P.subtract(closestSphere.center).normalize();
        Vector4f normal4 = new Vector4f((float) localNormal.x, (float) localNormal.y, (float) localNormal.z, 0.0f);
        normal4.mulProject(closestSphere.transformMatrix);
        Vector3D N = new Vector3D(normal4.x, normal4.y, normal4.z).normalize();

        double lightIntensity = computeLighting(P, N, direction.negate(), closestSphere.specular);
        int localColor = applyLighting(closestSphere.color, lightIntensity);

        // Transparency Handling
        if (closestSphere.transparency > 0) {
            // Compute the refraction direction if the material is transparent
            Vector3D refractedDirection = computeRefraction(direction, N, closestSphere.refraction);

            // Recursively trace the refracted ray
            int refractedColor = (int) traceRay(P, refractedDirection, 0.001, Double.POSITIVE_INFINITY, recursionDepth - 1);

            // Blend the refracted color with the local color based on transparency
            localColor = blendColors(localColor, refractedColor, closestSphere.transparency);
        }

        return localColor;
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
