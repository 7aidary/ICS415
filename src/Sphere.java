class Sphere {
    Vector3D center;
    double radius;
    int color;
    int specular;

    double reflective;


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

    // constructor for Ass2

    public Sphere(Vector3D center, double radius, int color, int specular, double reflective) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
    }



}
