import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShadowAndReflections {

    static final int WIDTH = 800;  // Canvas width
    static final int HEIGHT = 800; // Canvas height
    static final double VIEWPORT_SIZE = 1.0;
    static final double PROJECTION_PLANE_Z = 1.0;
    static final int BACKGROUND_COLOR =0xFFFFFF ; // Black

    private final List<Light> sceneLights;

    List<Sphere> spheres = new ArrayList<>();


        public ShadowAndReflections(List<Light> sceneLights){
            // Add spheres to the scene
            spheres.add(new Sphere(new Vector3D(0, -1, 3), 1, 0xFF0000,100,0.2)); // Red sphere
            spheres.add(new Sphere(new Vector3D(2, 0, 4), 1, 0x00FF00,20,0.3)); // Green sphere
            spheres.add(new Sphere(new Vector3D(-2, 0, 4), 1, 0x0000FF,10,0.4)); // Blue sphere
            spheres.add(new Sphere(new Vector3D(0, -5001, 0), 5000,0xFFFF00,1000, 0.5));
            this.sceneLights=sceneLights;
        }



        public Vector3D canvasToViewport(int x, int y) {
            return new Vector3D(
                    x * VIEWPORT_SIZE / WIDTH,
                    y * VIEWPORT_SIZE / HEIGHT,
                    PROJECTION_PLANE_Z
            );
        }
        public double traceRay(Vector3D origin, Vector3D direction, double tMin, double tMax, int recursion_depth) {
            IntersectionResult result = closestIntersection(origin,direction,tMin,tMax);
            // If no sphere was hit, return the background color
            if (result.closestSphere == null) {
                return BACKGROUND_COLOR;
            }
            // Compute intersection point and normal
            Vector3D P = origin.add(direction.multiply(result.closestT)); // P = O + closest_t * D
            Vector3D N = P.subtract(result.closestSphere.center).normalize(); // N = (P - center).normalize()

            double lightIntensity = computeLighting(P , N ,direction.negate(), result.closestSphere.specular);
            // Compute lighting and return the color
            int localColor= applyLighting(result.closestSphere.color , lightIntensity);
            double r = result.closestSphere.reflective;
            if (recursion_depth <= 0 || r <= 0) {
                return localColor;
            }

           // Compute the reflected color
           Vector3D R =  Vector3D.reflectRay(direction.normalize().negate() , N) ;
           int reflected_color = (int) traceRay(P, R, 0.001, Double.POSITIVE_INFINITY, recursion_depth - 1);
           return ShadowAndReflections.blendColors(localColor,reflected_color,result.closestSphere.reflective);
        }


        public  IntersectionResult closestIntersection(
                Vector3D O, Vector3D D, double tMin, double tMax) {
            double closestT = Double.POSITIVE_INFINITY;
            Sphere closestSphere = null;

            for (Sphere sphere : spheres) {
                double[] tValues = intersectRaySphere(O, D, sphere); // Assuming this function exists

                if (tValues != null) {
                    double t1 = tValues[0];
                    double t2 = tValues[1];

                    if (t1 >= tMin && t1 <= tMax && t1 < closestT) {
                        closestT = t1;
                        closestSphere = sphere;
                    }
                    if (t2 >= tMin && t2 <= tMax && t2 < closestT) {
                        closestT = t2;
                        closestSphere = sphere;
                    }
                }
            }

            return new IntersectionResult(closestSphere, closestT);
        }

        // Compute the lighting at point P
        public double computeLighting(Vector3D P, Vector3D N, Vector3D V, int specular) {
            double intensity = 0.0;
            double t_max=1;
            for (Light light : sceneLights) {
                Vector3D L = null; // Light vector

                if (light instanceof Light.AmbientLight) {
                        // Ambient light contributes directly
                        intensity += light.getIntensity();

                    }
                else {

                    // Determine light direction for point and directional lights
                if (light instanceof Light.PointLight) {
                    Light.PointLight pointLight = (Light.PointLight) light; // Downcast to PointLight
                    L = pointLight.getPosition().subtract(P);   // Direction from point to light
                    t_max =1 ;

                } else if (light instanceof Light.DirectionalLight) {
                    Light.DirectionalLight directionalLight = (Light.DirectionalLight) light; // Downcast to DirectionalLight
                    L = directionalLight.getDirection();                                      // Direction of the light
                     t_max =Double.POSITIVE_INFINITY ;


                }

                IntersectionResult result = closestIntersection(P, L, 0.001, t_max);
                if( result.closestSphere != null ) {
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

    public static int applyLighting(int color, double intensity) {
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * intensity)); // Red channel
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * intensity));  // Green channel
        int b = Math.min(255, (int) ((color & 0xFF) * intensity));         // Blue channel
        return (r << 16) | (g << 8) | b; // Combine channels into a single RGB color
    }

    public static int blendColors(int color1, int color2, double ratio) {
        int r = (int) (((color1 >> 16) & 0xFF) * (1 - ratio) + ((color2 >> 16) & 0xFF) * ratio);
        int g = (int) (((color1 >> 8) & 0xFF) * (1 - ratio) + ((color2 >> 8) & (0xFF)) * ratio);
        int b = (int) (((color1 & 0xFF) * (1 - ratio)) + ((color2 & 0xFF) * ratio));
        return (r << 16) | (g << 8) | b;
    }
        public void render(String filename) throws IOException {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

            for (int x = -WIDTH / 2; x < WIDTH / 2; x++) {
                for (int y = -HEIGHT / 2; y < HEIGHT / 2; y++) {
                    Vector3D direction = canvasToViewport(x, y).normalize();
                    int color = (int) traceRay(new Vector3D(0, 0, 0), direction, 1, Double.POSITIVE_INFINITY,3);
                    image.setRGB(x + WIDTH / 2, HEIGHT / 2 - y - 1, color);
                }
            }

            ImageIO.write(image, "png", new File(filename));
        }


        public static void main(String[] args) throws IOException {
            Light.AmbientLight ambientLight = new Light.AmbientLight(0.2);
            Light.PointLight pointLight = new Light.PointLight(0.6, new Vector3D(2,1,0));
            Light.DirectionalLight directionalLight = new Light.DirectionalLight(0.2, new Vector3D(1,4,4));
            List<Light> lights = new ArrayList<>();
            lights.add(directionalLight );
            lights.add(ambientLight );
            lights.add(pointLight );

            ShadowAndReflections rayTracer = new ShadowAndReflections(lights);
            rayTracer.render("output3.png");
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
