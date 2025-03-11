public class Light {
    protected String type;
    protected double intensity;

    // Constructor for the base class
    public Light(String type, double intensity) {
        this.type = type;
        this.intensity = intensity;
    }

    // Getter and setter methods (optional)
    public String getType() {
        return type;
    }

    public double getIntensity() {
        return intensity;
    }

    @Override
    public String toString() {
        return "Light [type=" + type + ", intensity=" + intensity + "]";
    }

    static class AmbientLight extends Light {
        public AmbientLight(double intensity) {
            super("ambient", intensity);
        }

        @Override
        public String toString() {
            return "AmbientLight [intensity=" + intensity + "]";
        }
    }

     static class PointLight extends Light {
        private Vector3D position; // e.g., (x, y, z)

        public PointLight(double intensity, Vector3D position) {
            super("point", intensity);
            this.position = position;
        }

        public Vector3D getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return "PointLight [intensity=" + intensity + ", position=(" +
                    position.x+ ", " + position.y + ", " + position.z + ")]";
        }
    }

     static class DirectionalLight extends Light {
        protected Vector3D direction; // e.g., (dx, dy, dz)

        public DirectionalLight(double intensity, Vector3D direction) {
            super("directional", intensity);
            this.direction = direction;
        }

        public Vector3D getDirection() {
            return direction;
        }

        @Override
        public String toString() {
            return "DirectionalLight [intensity=" + intensity + ", direction=(" +
                    direction.x+ ", " + direction.y + ", " + direction.z + ")]";
        }
    }


}



