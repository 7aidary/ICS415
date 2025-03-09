import org.joml.Matrix4d;

public class Sphere implements SceneObject {
    Vector3D center;
    double radius;
    int color;
    int specular;

    double reflective;

    double refraction;
     double transparency; // Transparency (0.0 - 1.0, where 1.0 is fully transparent)

    public Matrix4d transformMatrix;
    public Matrix4d inverseTransform;

    // constructor for Ass1
    public Sphere(Vector3D center, double radius, int color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }
    // constructor for Ass2

    public Sphere(Vector3D center, double radius, int color, int specular) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
    }

    // constructor for Ass3

    public Sphere(Vector3D center, double radius, int color, int specular, double reflective) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;

    }

    // constructor for Ass4

    public Sphere(Vector3D center, double radius, int color, int specular, double reflective, double refraction, double transparency) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
        this.refraction = refraction;
        this.transparency=transparency;
    }





    public Sphere(Vector3D center, double radius, int color, int specular, double reflective, double refraction, double transparency, Matrix4d transformMatrix) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.reflective = reflective;
        this.transparency = transparency;
        this.refraction = refraction;
        this.specular = specular;
        transform(transformMatrix);
    }
    public void transform(Matrix4d transformMatrix){
        Double scale = transformMatrix.get(0,0);

        transformMatrix.m00(1);
        transformMatrix.m11(1);
        transformMatrix.m22(1);

        this.radius*=scale;
        this.center= new Vector3D(transformMatrix.transform(center));

        transformMatrix.m00(scale);
        transformMatrix.m11(scale);
        transformMatrix.m22(scale);

    }



    public double[] intersectRaySphere(Vector3D origin, Vector3D direction) {
        Vector3D CO = origin.subtract(this.center);
        double a = direction.dot(direction);
        double b = 2 * CO.dot(direction);
        double c = CO.dot(CO) - this.radius * this.radius;

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return null; // No intersection

        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
        return new double[]{t1, t2};
    }

    @Override
    public double intersectRay(Vector3D rayOrigin, Vector3D rayDirection) {

        this.intersectRaySphere(rayOrigin,rayDirection);
        return Double.parseDouble(null);
        }






    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public int getSpecular() {
        return this.specular;
    }

    @Override
    public double getReflective() {
        return this.reflective;
    }

    @Override
    public double getTransparency() {
        return this.transparency;
    }

    @Override
    public double getRefraction() {
        return this.refraction;
    }


    // Apply transformations



}





