import org.joml.Matrix4d;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OBJLoader {
    static List<Vector3D> vertices = new ArrayList<>();
    static List<Triangle> triangles = new ArrayList<>();

    static List<Vector3D> vertexNormals = new ArrayList<>(Collections.nCopies(vertices.size(), new Vector3D(0, 0, 0)));


    public static List<Triangle> loadOBJ(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            if (parts[0].equals("v")) {  // Vertex line
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                vertices.add(new Vector3D(x, y, z));
            } else if (parts[0].equals("f")) { // Face line
                int v0 = Integer.parseInt(parts[1]) - 1;
                int v1 = Integer.parseInt(parts[2]) - 1;
                int v2 = Integer.parseInt(parts[3]) - 1;
                Triangle triangle = new Triangle(vertices.get(v0), vertices.get(v1), vertices.get(v2),0xCCE5FF,
                        50,
                        0.3,
                        0.2,
                        1.3,
                        new Matrix4d().scale(3)
                );

                triangles.add(triangle);

            }
        }


        // Normalize vertex normals
        reader.close();
        return triangles;
    }


}