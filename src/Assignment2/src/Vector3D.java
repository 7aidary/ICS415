import org.joml.Vector4d;

class Vector3D extends Vector4d {
    double x, y, z;

    public Vector3D(double x, double y, double z) {
        super(x,y,z,1);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector4d transform) {
        super(transform);
        this.x = transform.x;
        this.y=transform.y;
        this.z=transform.z;
    }

    public Vector3D add(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    public Vector3D subtract(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public Vector3D multiply(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }

    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3D normalize() {
        double len = length();
        return new Vector3D(x / len, y / len, z / len);
    }




    // Negate function
    public Vector3D negate() {
        return new Vector3D(-this.x, -this.y, -this.z);
    }

   public static Vector3D reflectRay(Vector3D L, Vector3D N) {
        return N.multiply(2*N.dot(L)).subtract(L);
    }

    public Vector3D cross(Vector3D v) {
        double crossX = this.y * v.z - this.z * v.y;
        double crossY = this.z * v.x - this.x * v.z;
        double crossZ = this.x * v.y - this.y * v.x;
        return new Vector3D(crossX, crossY, crossZ);
    }
}






