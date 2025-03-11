# Stanford Bunny by Ray Tracing 3D Model Viewer

This project implements a simple ray tracing system that loads and renders 3D models, specifically the Stanford Bunny model. using an `.obj` file format. The project parses the `.obj` file to extract the vertices and faces, creating a 3D scene that can be rendered with ray tracing techniques.

## Features
- **OBJ file loading**: Load 3D models in `.obj` format.
- **Extracting Triangle Vertices**: The Stanford Bunny `.obj` file is loaded as a list of `Vector3D` objects, which represent the vertices of the triangles.
- **Ray tracing**: Perform ray tracing to calculate intersections between rays and the 3D model's geometry.
- **Triangle intersection**: The ray tracing algorithm is updated to handle intersections with triangles and calculate lighting effects for each intersection.
- **Stanford Bunny model**: Render the Stanford Bunny using the `bunny.obj` file.

## Prerequisites

- Java Development Kit (JDK) 8 or higher

- JOML library (included in dependencies)

  ## Installation
- Clone the repository:  
```bash
  git clone https://github.com/7aidary/ICS415/Project1/StanfordBunny.git
```
  ## Navigate to the project directory:
```bash
cd ICS415/Project1/StanfordBunny
```

## Install JOML library:

```xml
<dependency>
    <groupId>org.joml</groupId>
    <artifactId>joml</artifactId>
    <version>1.10.5</version>
</dependency>
```
```bash
mvn clean install
```
## Running the Project

To run the main rendering files, use the following commands:

## Run StanforBunny3D
```bash
javac -cp .:joml.jar StanfordBunny3D.java
java -cp .:joml.jar StanfordBunny3D
```
## Example output
<img src="https://github.com/7aidary/ICS415/blob/c8403c5356250770465f1d64aaa8a8dc1db75981/Project1/StanfordBunny/output7.png" alt="Stanford Bunny" width="800">
