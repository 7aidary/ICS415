# RayTracing in Java

## Description
This repository contains a ray tracing implementation in Java, capable of rendering 3D scenes with spheres, cylinders, and lighting effects. The project utilizes the JOML library for vector and matrix operations.

## Features
- Ray tracing for rendering 3D objects

- Support for spheres and cylinders

- Lighting effects including ambient, diffuse, and specular lighting

- Reflection and refraction handling

- Stanford Bunny 3D rendering

## Prerequisites

- Java Development Kit (JDK) 8 or higher

- JOML library (included in dependencies)

  ## Installation
- Clone the repository:  
```bash
  git clone https://github.com/7aidary/ICS415.git
```
  ## Navigate to the project directory:
```bash
cd ICS415
```

## Install dependencies:
```bash
npm install
```

## Running the Project

To run the main rendering files, use the following commands:

## Run FinalProject
```bash
javac -cp .:joml.jar FinalProject.java
java -cp .:joml.jar FinalProject
```

## Run stanfordBunny3D
```bash
javac -cp .:joml.jar stanfordBunny3D.java
java -cp .:joml.jar stanfordBunny3D
```

Replace joml.jar with the correct path to the JOML library if needed.

## Output
- You can find the generated image of spheres in "output6.png"
- You can find the generated image of Stanford bunny in "output7.png"
