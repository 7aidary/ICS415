import java.io.IOException;
import java.util.List;

public class StanfordBunny3D {
    static List<Triangle> triangles;

    public static void main(String[] args) {
        try {
            triangles = OBJLoader.loadOBJ("/stanford_bunny.obj");
        } catch (IOException e) {
            System.err.println("Error loading model: " + e.getMessage());
            return;
        }
}
}
