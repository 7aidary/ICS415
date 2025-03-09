import org.joml.Matrix4d;
import org.joml.Vector3d;

public class Triangle implements SceneObject {
    public final Vector3D v0;
    public final Vector3D v1;
    public final Vector3D v2;
    public int color;
    public int specular;
    public double reflective;
    public double transparency;
    public double refraction;

    // Precomputed normal of the triangle (computed from transformed vertices)
    private Vector3D normal;

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2){
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, int color,
                    int specular, double reflective, double transparency, double refraction,
                    Matrix4d globalTransform) {
        // Apply the transformation (if any) to each vertex.
        // Here we assume that Vector3D has public double fields x, y, z.
        this.v0 = transformVertex(v0, globalTransform);
        this.v1 = transformVertex(v1, globalTransform);
        this.v2 = transformVertex(v2, globalTransform);

        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
        this.transparency = transparency;
        this.refraction = refraction;

        // Precompute the triangle's normal (assumed constant over the triangle surface)
        this.normal = computeNormal();
    }

    private Vector3D transformVertex(Vector3D vertex, Matrix4d transform) {
        // Apply global transformation and scale transformation
        Matrix4d finalTransform = new Matrix4d(transform); // Combine both transformations
        // Convert to JOML's Vector3d for transformation
        Vector3d temp = new Vector3d(vertex.x, vertex.y, vertex.z);
        finalTransform.transformPosition(temp);
        return new Vector3D(temp.x, temp.y, temp.z);
    }

    private Vector3D computeNormal() {
        Vector3D edge1 = v1.subtract(v0);
        Vector3D edge2 = v2.subtract(v0);
        return edge1.cross(edge2).normalize();
    }

    public Double intersectRayTriangle(Vector3D rayOrigin, Vector3D rayDirection) {
        final double EPSILON = 1e-6;
        Vector3D edge1 = v1.subtract(v0);
        Vector3D edge2 = v2.subtract(v0);
        Vector3D h = rayDirection.cross(edge2);
        double a = edge1.dot(h);

        if (Math.abs(a) < EPSILON) {
            // The ray is parallel to the triangle.
            return null;
        }

        double f = 1.0 / a;
        Vector3D s = rayOrigin.subtract(v0);
        double u = f * s.dot(h);
        if (u < 0.0 || u > 1.0) {
            return null;
        }

        Vector3D q = s.cross(edge1);
        double v = f * rayDirection.dot(q);
        if (v < 0.0 || u + v > 1.0) {
            return null;
        }

        // At this stage, we can compute t to find out where the intersection point is on the ray.
        double t = f * edge2.dot(q);
        if (t > EPSILON) {
            return t;
        } else {
            // Line intersection but not a ray intersection.
            return null;
        }
    }

    @Override
    public double intersectRay(Vector3D rayOrigin, Vector3D rayDirection) {
        this.intersectRayTriangle(rayOrigin, rayDirection);
        return Double.parseDouble(null);
    }

    public Vector3D getNormal() {
        return this.computeNormal();
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
