import org.joml.Matrix4f;

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





        public Sphere(Vector3D center, double radius, int color,int specular, double reflective, double refraction,double transparency, Matrix4f transform) {
            this.center = center;
            this.radius = radius;
            this.color = color;
            this.reflective = reflective;
            this.transparency = transparency;
            this.refraction = refraction;
            this.specular = specular;

            this.transformMatrix = new Matrix4f().identity(); // Default identity matrix
            this.inverseTransform = new Matrix4f().identity();
            // Apply the transformation matrix
            this.transformMatrix = new Matrix4f(transform);

            // Compute the inverse transformation matrix
            this.inverseTransform = new Matrix4f(transform).invert();
        }

        // Apply transformations

        public void setTransformation(Matrix4f transformation) {
            this.transformMatrix = transformation;
            this.inverseTransform = new Matrix4f(transformation).invert(); // Precompute inverse
        }
    }





