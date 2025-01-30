class Sphere {
    Vector3D center;
    double radius;
    int color;
    int specular;

    double reflective;

    double refraction;
     double transparency; // Transparency (0.0 - 1.0, where 1.0 is fully transparent)



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



}
