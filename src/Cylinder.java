public class Cylinder implements SceneObject {
    public Vector3D baseCenter;
    public Vector3D axis; // Must be a unit vector
    public double radius;
    public double height;
    int color;
    int specular;

    double reflective;

    double refraction;
    double transparency; // Transparency (0.0 - 1.0, where 1.0 is fully transparent)

    public Cylinder(Vector3D baseCenter, Vector3D axis, double radius, double height, int color, int specular, double reflective, double transparency, double refraction) {
        this.color=color;
        this.reflective=reflective;
        this.refraction=refraction;
        this.specular=specular;
        this.transparency=transparency;
        this.baseCenter = baseCenter;
        this.axis = axis.normalize();
        this.radius = radius;
        this.height = height;
    }

    public double[] intersectRayCylinder(Vector3D origin, Vector3D direction) {
        Vector3D CO = origin.subtract(baseCenter);
        Vector3D V = axis;
        Vector3D D = direction;

        // Project ray direction and origin onto the plane perpendicular to the cylinder's axis
        Vector3D D_proj = D.subtract(V.multiply(D.dot(V)));
        Vector3D CO_proj = CO.subtract(V.multiply(CO.dot(V)));

        double a = D_proj.dot(D_proj);
        double b = 2 * CO_proj.dot(D_proj);
        double c = CO_proj.dot(CO_proj) - radius * radius;

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return null; // No intersection

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDiscriminant) / (2 * a);
        double t2 = (-b + sqrtDiscriminant) / (2 * a);

        // Check intersection points lie within the cylinder's height
        double[] ts = {t1, t2};
        double[] validTs = new double[2];
        int count = 0;
        for (double t : ts) {
            if (t > 0) {
                Vector3D P = origin.add(direction.multiply(t));
                double heightAlongAxis = P.subtract(baseCenter).dot(V);
                if (heightAlongAxis >= 0 && heightAlongAxis <= height) {
                    validTs[count++] = t;
                }
            }
        }
        return count == 0 ? null : java.util.Arrays.copyOf(validTs, count);
    }

    public Vector3D getNormal(Vector3D P) {
        // Compute the normal at point P on the cylinder's surface
        Vector3D CO = P.subtract(baseCenter);
        double projection = CO.dot(axis);
        Vector3D closestPoint = baseCenter.add(axis.multiply(projection));
        return P.subtract(closestPoint).normalize();
    }

    @Override
    public double intersectRay(Vector3D rayOrigin, Vector3D rayDirection) {
        this.intersectRayCylinder(rayOrigin,rayDirection);
        return 0;
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
}
