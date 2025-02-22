import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FinalProject {

    static final int WIDTH = 1200;
    static final int HEIGHT = 800;
    static final double VIEWPORT_SIZE = 1.0;
    static final double PROJECTION_PLANE_Z = 1.0;
    static final int BACKGROUND_COLOR = 0xCCE5FF;

    private final List<Light> sceneLights;
    private final List<Sphere> spheres = new ArrayList<>();

    private  final List<Cylinder> cylinders=new ArrayList<>();
    private Camera camera;
    static Random rand = new Random();
    public FinalProject(List<Light> sceneLights, Camera camera ) {
        System.out.println("Rendering...");
        int i=0;
        for (int a = -8; a < 14; a++) {
            for (int b = -8; b < 18; b++) {
                i++;
                double chooseMat = rand.nextDouble();
                double cx = a + 0.9 * rand.nextDouble();
                double cy = 0.3;
                double cz = b + b * rand.nextDouble();

                if (Math.sqrt((cx - 4) * (cx - 4) + (cy - 0.2) * (cy - 0.2) + (cz * cz)) > 0.9) {
                    if (chooseMat < 0.8) {
                        // Diffuse material
                        double[] albedo = randomColor();
                        spheres.add(new Sphere(new Vector3D(cx, cy, cz), 0.3, getColorHex(albedo), 50, 0, 0, 0, new Matrix4d()));
                    } else if (chooseMat < 0.95) {
                        // Metal material
                        double[] albedo = {randomDouble(0.5, 1), randomDouble(0.5, 1), randomDouble(0.5, 1)};
                        double fuzz = randomDouble(0, 0.5);
                        spheres.add(new Sphere(new Vector3D(cx, cy, cz), 0.3, getColorHex(albedo), 100, fuzz, 0, 0, new Matrix4d()));
                    } else {
                        // Glass material
                        spheres.add( new Sphere(new Vector3D(cx, cy, cz), 0.3, 0xFFFFFF, 500, 0, 1.5, 0.5, new Matrix4d()));
                    }
                }
            }

        }
        System.out.println(i);

        // Large spheres
        spheres.add(new Sphere(new Vector3D(4, 2, 10), 2.0, 0xFFFFFF, 100, 0.0, 0.0, 1.0, new Matrix4d()));
        spheres.add( new Sphere(new Vector3D(-2, 2, 10), 2.0, 0x66331A, 0, 0, 1.0, 0, new Matrix4d()));
        spheres.add(new Sphere(new Vector3D(8, 2, 10), 2.0, 0xB29980, 2000, 0.35, 1.0, 0, new Matrix4d()));







        spheres.add(new Sphere(
                new Vector3D(0, -1000, 0),
                1000,
                0xB3B3B3,  // Slightly gray floor
                4,
                0.0,
                0.0,
                0.0,
                new Matrix4d()
        ));
        this.sceneLights = sceneLights;
        this.camera = camera;
    }

    static double randomDouble(double min, double max) {
        return min + (max - min) * rand.nextDouble();
    }

    static double[] randomColor() {
        return new double[]{rand.nextDouble(), rand.nextDouble(), rand.nextDouble()};
    }

    static int getColorHex(double[] color) {
        int r = (int) (color[0] * 255);
        int g = (int) (color[1] * 255);
        int b = (int) (color[2] * 255);
        return (r << 16) | (g << 8) | b;
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

        for (Sphere sphere : spheres) {
            double[] tValues = sphere.intersectRaySphere(O, D);
            if (tValues != null) {
                for (double t : tValues) {
                    if (t >= tMin && t <= tMax && t < closestT) {
                        closestT = t;
                        closestObject = sphere;
                    }
                }
            }
        }

       /* for (Triangle triangle : triangles) {
            Double tValue = triangle.intersectRayTriangle(O, D);
            if (tValue != null && tValue >= tMin && tValue <= tMax && tValue < closestT) {
                closestT = tValue;
                closestObject = triangle;
            }
        }*/

        for (Cylinder cylinder : cylinders) {
            double[] tValues = cylinder.intersectRayCylinder(O, D);
            if (tValues != null) {
                for (double t : tValues) {
                    if (t >= tMin && t <= tMax && t < closestT) {
                        closestT = t;
                        closestObject = cylinder;
                    }
                }
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
         List<Triangle> triangles=new ArrayList<>();
// Invert the view matrix to obtain the world transformation (which gives the camera's orientation).
        // Define your camera's desired position, target, and up vector.
        Vector3d lookFrom = new Vector3d(13, 2, 3);
        Vector3d lookAt   = new Vector3d(0, 0, 0);
        Vector3d vup      = new Vector3d(0, 1, 0);


        // Compute forward vector (pointing from lookat to lookfrom)
        Vector3d forward = new Vector3d();
        lookFrom.sub(lookAt, forward);
        forward.normalize();

        // Compute right vector (perpendicular to both vup and forward)
        Vector3d right = new Vector3d(0,0,0);
        vup.cross(forward, right);
        right.normalize();

        // Compute true up vector
        Vector3d trueUp = new Vector3d();
        forward.cross(right, trueUp);
        // trueUp is already normalized if right and forward are unit vectors and perpendicular
        // Here, we place the basis vectors as columns:
         Matrix3d rotation = new Matrix3d(
                right.x,   trueUp.x,   forward.x,
                right.y,   trueUp.y,   forward.y,
                right.z,   trueUp.z,   forward.z
        );

// Create a rotation matrix for a rotation around the Y axis.=       // rotation.mul(leftRotation);
        // Now create your camera with the position and rotation
        Camera camera = new Camera(lookFrom, rotation.rotateY(0.5));



      /* try {
            triangles = OBJLoader.loadOBJ("Data/bunny.obj");

        } catch (IOException e) {
            System.err.println("Error loading model: " + e.getMessage());
            return;
        }
*/

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
        //lights.add(new Light.PointLight(0.6, new Vector3D(2, 1, 0)));
        lights.add(new Light.DirectionalLight(0.5, new Vector3D(0, 1, 0)));

        FinalProject rayTracer = new FinalProject(lights, camera);
        rayTracer.render("output6.png");
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
