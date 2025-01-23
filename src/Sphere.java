class Sphere {
    Vector3D center;
    double radius;
    int color;
    int specular;// RGB color stored as an integer

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


   /* public Color scaleLighting(double lightingIntensity) {
        // Assuming Color has a method to scale its components
        return color.scale(lightingIntensity);
    }*/
}
 class Color {
    public double r, g, b; // Red, Green, Blue components of the color

    // Constructor
    public Color(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    // Scale function to multiply each color component by a factor
    public Color scale(double factor) {
        return new Color(this.r * factor, this.g * factor, this.b * factor);
    }

    // To represent color as a string
    @Override
    public String toString() {
        return "Color(" + r + ", " + g + ", " + b + ")";
    }
     // Getters for RGB values as integers (0 to 255)
     public int getRed() {
         return (int) Math.floor(r * 255);  // Convert float to integer in the range [0, 255]
     }

     public int getGreen() {
         return (int) (Math.floor(g * 255));  // Convert float to integer in the range [0, 255]
     }

     public int getBlue() {
         return  (int)(Math.floor(b * 255));  // Convert float to integer in the range [0, 255]
     }

     // Method to convert RGB to Hex
     public String rgbToHex() {
         // Convert each color component to hex and combine them

         int rgb = (getRed() << 16) | (getGreen() << 8) | getBlue();
         return String.format("0x%06X", rgb); // Format as 6-character uppercase hex
     }



}

