# Shadow and Reflections Ray Tracing

This project implements a ray tracing engine with advanced features such as shadows and reflections. It renders spheres with varying colors and effects, simulating a realistic lighting model. The project supports different types of light sources, including ambient light, point light, and directional light. Reflection and shadow effects are applied to the rendered scene to enhance realism.

## Features

- **Shadows**: Calculates the effect of shadows in the scene by checking whether a light source is blocked by objects.
- **Reflections**: Simulates reflection of light on objects using recursive ray tracing.
- **Lighting Model**: Diffuse and specular lighting components are calculated for each object to determine how the light interacts with the surface.

## Scene Setup

The scene consists of several spheres, each with different colors and reflective properties. The lighting setup includes ambient, point, and directional lights to create a dynamic and realistic lighting environment.

- **Spheres**: Red, Green, Blue, and a large yellow sphere acting as the floor.
- **Light Sources**:
  - **Ambient Light**: Provides uniform lighting across the scene.
  - **Point Light**: A light that originates from a specific point in the 3D space.
  - **Directional Light**: A light that comes from a specific direction, simulating sunlight.

## How It Works

The program uses ray tracing to compute the color of each pixel in the rendered image. For each pixel, a ray is traced from the camera through the viewport, and the closest object (sphere) is determined. Based on the intersection point and surface normal, the lighting is calculated to determine the final color.

- **Shadows**: The program checks for intersections between light rays and objects in the scene to determine if a point is in shadow.
- **Reflections**: The program recursively traces reflected rays at the intersection point to simulate reflective surfaces.




## How to Run 

### Prerequisites
Ensure that you have Java 8 or higher installed on your machine. You can check your Java version by running:
```bash
java -version
```

#### 1. Clone the Repository  
```sh
git clone https://github.com/7aidary/ICS415.git
cd ICS415/Project1/Assignement3
```

#### 2. Compile the Java Code  
```sh
javac ShadowAndReflections.java
```

#### 3. Run the Program  
```sh
java ShadowAndReflections
```
## Example Output
![Alt](https://github.com/7aidary/ICS415/blob/78c149eec297d27091b09d8d406e8cd4d91bb14b/Project1/%E2%80%8F%E2%80%8FAssignement3/output3.png)
