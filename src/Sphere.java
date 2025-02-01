import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Sphere {
    Vector3D center;
    double radius;
    int color;
    int specular;

    double reflective;

    double refraction;
     double transparency; // Transparency (0.0 - 1.0, where 1.0 is fully transparent)

    public Matrix4f transformMatrix;
    public Matrix4f inverseTransform;

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





    public Sphere(Vector3D center, double radius, int color, int specular, double reflective, double refraction, double transparency, Matrix4f transform) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.reflective = reflective;
        this.transparency = transparency;
        this.refraction = refraction;
        this.specular = specular;

        setTransformation(transform); // تحديث المركز ونصف القطر بعد التحويل
    }


    // Apply transformations

    public void setTransformation(Matrix4f transformation) {
        this.transformMatrix = transformation;
        this.inverseTransform = new Matrix4f(transformation).invert(); // Precompute inverse

        // تحديث المركز بعد التحويل
        Vector4f newCenter = new Vector4f((float) center.x, (float) center.y, (float) center.z, 1.0f);
        newCenter.mul(transformMatrix);
        this.center = new Vector3D(newCenter.x, newCenter.y, newCenter.z);

        // تحديث نصف القطر بناءً على التحجيم
        float scaleX = transformMatrix.get(0, 0);
        float scaleY = transformMatrix.get(1, 1);
        float scaleZ = transformMatrix.get(2, 2);
        float scaleFactor = (float) Math.sqrt((scaleX * scaleX + scaleY * scaleY + scaleZ * scaleZ) / 3.0); // متوسط التحجيم

        this.radius *= scaleFactor; // تطبيق التحجيم على نصف القطر
    }

}





