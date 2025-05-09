# Using JOML for Transformations in Ray Tracing

This project utilizes the **Java OpenGL Math Library (JOML)** for handling matrix transformations applied to the camera and objects (spheres) in a ray tracing engine.

## Why JOML?
JOML provides an efficient and easy-to-use API for working with **3D transformations**, including translation, scaling, and rotation. It simplifies applying transformations to objects in the scene and adjusting the camera’s position and orientation.

## Transformations in This Project

### 1. **Camera Transformation**
   - The camera is represented using a **position vector** and an **orientation matrix (Matrix3d)**.
   - The orientation matrix is initialized as an identity matrix but can be modified for rotations.

### 2. **Object (Sphere) Transformations**
   - Each sphere can have a **global transformation matrix (Matrix4d)** applied.
   - This allows for scaling, translation, and rotation of individual spheres.
   - Example:
     ```java
     Matrix4d globalTransform = new Matrix4d()
         .translate(0, 1, 3)
         .scale(0.8);
     ```
   - The transformation is applied when computing intersections and normal vectors.

## How It Works
1. **Spheres are defined in world space** with transformations applied.
2. **Ray intersections are computed considering transformations** to ensure proper object placement.
3. **Lighting and shading calculations** take into account the transformed positions and normals.

## Prerequisites

- Java Development Kit (JDK) 8 or higher

- JOML library (included in dependencies)

  ## Installation
- Clone the repository:  
```bash
  git clone https://github.com/7aidary/ICS415/Project1/Assignment5.git
```
  ## Navigate to the project directory:
```bash
cd ICS415/Project1/Assignment5
```

## Install JOML library:

```xml
<dependency>
    <groupId>org.joml</groupId>
    <artifactId>joml</artifactId>
    <version>1.10.5</version>
</dependency>
Then run:
```
```bash
mvn clean install
```
## Running the Project

To run the main rendering files, use the following commands:

## Run FinalProject
```bash
javac -cp .:joml.jar Assignment5.java
java -cp .:joml.jar Assignment5
```

## Example Output
![Alt](https://github.com/7aidary/ICS415/blob/f3feb6fb02f3b434769f53e40cc5379c1b4e2812/Project1/%E2%80%8F%E2%80%8FAssignement5/output5.png)
