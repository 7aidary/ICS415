import org.joml.Matrix3d;
import org.joml.Matrix4d;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StanfordBunny3D {
    static final int WIDTH = 200;
    static final int HEIGHT = 200;

    static final int BACKGROUND_COLOR = 0xFFFFFF;

    private final List<Light> sceneLights;    //  private  List<Triangle> triangles;
    private Camera camera;

    private  List<Triangle> triangles;


    public StanfordBunny3D(List<Light> sceneLights, Camera camera,List<Triangle> triangles ) {
        System.out.println("Rendering...");
        this.triangles=triangles;
        this.sceneLights = sceneLights;
        this.camera = camera;
    }






    public double traceRay(Vector3D origin, Vector3D direction, double tMin, double tMax, int recursion_depth) {
        if (recursion_depth <= 0) {
            return BACKGROUND_COLOR; // Stop recursion if depth is 0
        }

        IntersectionResult result = closestIntersection(origin, direction, tMin, tMax);

        // If no sphere was hit, return the background color
        if (result.closestObject == null) {

            return BACKGROUND_COLOR;
        }
        // Compute intersection point and normal
        Vector3D P = origin.add(direction.multiply(result.closestT)); // P = O + closest_t * D
        Vector3D N = null;

        if (result.closestObject instanceof Sphere) {
            Sphere sphere = (Sphere) result.closestObject;
            N = P.subtract(sphere.center).normalize();
        }
        else if (result.closestObject instanceof Triangle) {
            Triangle triangle = (Triangle) result.closestObject;
            N = triangle.getNormal(); // Normal is precomputed
        } else if (result.closestObject instanceof Cylinder) {
            Cylinder cylinder = (Cylinder) result.closestObject;
            N=cylinder.getNormal(P).normalize();

        }
        double lightIntensity = computeLighting(P, N, direction.negate(), result.closestObject.getSpecular());
        // Compute lighting and return the color
        int localColor = applyLighting(result.closestObject.getColor(), lightIntensity);

        double r = result.closestObject.getReflective();
        double transparency = result.closestObject.getTransparency();

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
            Vector3D T = computeRefraction(direction, N, result.closestObject.getRefraction());
            refractedColor = (int) traceRay(P, T, 0.001, Double.POSITIVE_INFINITY, recursion_depth - 1);
        }

        // Blend the local, reflected, and refracted colors based on the transparency/reflection properties
        int blendedColor = blendColors(localColor, reflectedColor, r); // First blend local + reflection
        blendedColor = blendColors(blendedColor, refractedColor, transparency); // Then blend with refraction

        // Combine the local, reflected, and refracted colors based on the transparency/reflection properties
        return blendedColor;
    }





    public IntersectionResult closestIntersection(Vector3D O, Vector3D D, double tMin, double tMax) {
        double closestT = Double.POSITIVE_INFINITY;
        SceneObject closestObject = null;



       for (Triangle triangle : triangles) {
            Double tValue = triangle.intersectRayTriangle(O, D);
            if (tValue != null && tValue >= tMin && tValue <= tMax && tValue < closestT) {
                closestT = tValue;
                closestObject = triangle;
            }
        }

        return new IntersectionResult(closestObject, closestT);
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
                if (result.closestObject != null) {
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
                Vector3D direction = camera.getRayDirection3(x - WIDTH / 2, y - HEIGHT / 2);
                int color = (int) traceRay(camera.position, direction, 0.001, Double.POSITIVE_INFINITY, 5);
                image.setRGB(x, y, color);
            }
        }
        ImageIO.write(image, "PNG", new File(filename));
    }



    public static void main(String[] args) throws IOException {
        List<Triangle> triangles;
        // Now create your camera with the position
        Camera camera = new Camera(new Vector3D(0, 0, -7), new Matrix3d().identity());


       try {
            triangles = OBJLoader.loadOBJ("Data/bunny.obj");

        } catch (IOException e) {
            System.err.println("Error loading model: " + e.getMessage());
            return;
        }


        triangles.add(new Triangle(
                new Vector3D(-50, 0, 0),
                new Vector3D(50, 0, 0),
                new Vector3D(0, 1, 50),
                0x00FF00, // Green color
                50,
                0.3,
                0.2,
                1.3,
                new Matrix4d()
        ));
        List<Light> lights = new ArrayList<>();
        lights.add(new Light.AmbientLight(0.3));
        lights.add(new Light.PointLight(0.6, new Vector3D(2, 1, 0)));
        lights.add(new Light.DirectionalLight(0.5, new Vector3D(0, 1, 0)));

        StanfordBunny3D rayTracer = new StanfordBunny3D(lights, camera,triangles);
        rayTracer.render("output7.png");
    }

    public static class IntersectionResult {
        public SceneObject closestObject;
        public double closestT;

        public IntersectionResult(SceneObject closestObject, double closestT) {
            this.closestObject = closestObject;
            this.closestT = closestT;
        }
    }

}

