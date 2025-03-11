# Light Ray Tracing

This is a basic implementation of ray tracing using lighting calculations in Java. The program simulates light interactions with spherical objects in a scene to produce an image of a 3D scene rendered to a 2D canvas.

## Overview
The program creates a scene with spheres and light sources (ambient, point, and directional light). It traces rays from the camera (the origin) through the viewport and calculates intersections with the spheres. Lighting is then applied based on the light sources and the material properties of the spheres.

## Key Components

- **Scene Lights**: The scene contains three types of lights:
  - **Ambient Light**: Provides a constant light intensity across the entire scene.
  - **Point Light**: A light source with a specific position in space.
  - **Directional Light**: A light source with a constant direction but no specific position.

- **Spheres**: The scene includes several spheres of different sizes, colors, and specular properties.

- **Ray Tracing**: For each pixel in the image, a ray is traced from the origin through the scene to check for intersections with the spheres. The closest sphere determines the color of the pixel, taking into account the lighting and surface properties of the sphere.

- **Lighting Calculation**: The program calculates both diffuse and specular lighting at the point of intersection using the Phong reflection model. Diffuse lighting depends on the angle between the normal vector at the intersection and the light direction. Specular lighting depends on the reflection of the light and the view direction.

- **Rendering**: The final image is rendered pixel by pixel and saved as a PNG image.

## How it Works

1. **Scene Setup**:
   - The scene consists of multiple spheres, each with a position, radius, color, and specular coefficient.
   - The scene also contains multiple light sources.

2. **Ray Tracing**:
   - For each pixel in the final image, a ray is cast from the camera through the pixel’s location on the viewport.
   - The ray is tested for intersections with each sphere in the scene.
   - If an intersection occurs, the lighting at that point is computed, considering ambient, point, and directional lights.

3. **Lighting Calculation**:
   - **Ambient Lighting** contributes a constant amount to all points.
   - **Diffuse Lighting** is calculated based on the angle between the surface normal and the light direction.
   - **Specular Lighting** is calculated based on the reflection of the light direction and the view direction.

4. **Rendering**:
   - After determining the color for each pixel, the image is created and saved as a PNG file.

## Code Breakdown
### In addition to previous functions here wd add :
1. **applyLighting()**: Modifies a sphere’s color based on the intensity of the light at the intersection point.
2. **computeLighting()**: Computes the lighting intensity at the point of intersection considering all light sources in the scene.


## How to Run

#### 1. Clone the Repository  
```sh
git clone https://github.com/7aidary/ICS415.git
cd ICS415/Project1/Assignement2
```

#### 2. Compile the Java Code  
```sh
javac LightRaytracing.java
```

#### 3. Run the Program  
```sh
java LightRaytracing
```

## Example Output
![Light effects](https://github.com/7aidary/ICS415/blob/71d012f14d13fdbcaf53df7b95f3f8ac9847959e/Project1/%E2%80%8F%E2%80%8FAssignement2/output2.png)
