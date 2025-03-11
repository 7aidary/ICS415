# Ray Tracing with Transparency and Refraction

This project implements a ray tracing engine that supports transparency and refraction effects for realistic rendering of 3D spheres. The implementation accounts for reflection, refraction, and lighting using various types of light sources.

## Features

- **Transparency**: Allows light to pass through objects partially, blending background colors.
- **Refraction**: Simulates light bending through transparent materials using Snell's Law.

## Implementation Details

1. **Ray Tracing Algorithm**
   - Casts rays from the camera into the scene.
   - Finds intersections with objects.
   - Calculates lighting, reflection, and refraction contributions.

2. **Refraction Calculation**
   - Uses Snell’s Law to compute refracted ray directions.
   - Handles total internal reflection when necessary.

3. **Blending Colors**
   - Uses weighted blending for combining local color, reflection, and refraction contributions.

## How to Run
#### 1. Clone the Repository  
```sh
git clone https://github.com/7aidary/ICS415.git
cd ICS415/Project1/Assignement4
```

#### 2. Compile the Java Code  
```sh
javac TransparencyAndRefraction.java
```

#### 3. Run the Program  
```sh
java TransparencyAndRefraction
```

## Example Output
![Alt](https://github.com/7aidary/ICS415/blob/777b29474caee913b3588eb0ea0689812fe24524/Project1/%E2%80%8F%E2%80%8FAssignement4/output4.png)

