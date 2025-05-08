# Like MinecraftEngine Engine (LWJGL + JOML)

A basic 3D block world engine implemented in Java using LWJGL for rendering and JOML for math utilities.

## Features

- Procedural chunk-based terrain generation
- Real-time block placement and removal
- Optimized mesh building (only visible block faces)
- Basic camera movement and mouse look controls
- Texture atlas support for efficient rendering

## Dependencies

- [LWJGL 3](https://www.lwjgl.org/)
- [JOML](https://github.com/JOML-CI/JOML)

## Controls

- `W`, `A`, `S`, `D`: Move forward, left, backward, right
- Mouse movement: Look around
- Scroll or zoom keys: Adjust camera distance
- Click or keys (depending on implementation): Place or remove blocks

## How It Works

The world is divided into 3D chunks. Each chunk contains a grid of blocks (like grass, dirt, stone, etc). Only the visible faces of blocks are sent to the GPU to improve performance. Texture coordinates are pulled from a shared atlas to reduce draw calls.

Camera movement is smooth with acceleration and friction. The view matrix is updated every frame based on mouse and keyboard input.

## ScreenShot
![Alt text](https://github.com/7aidary/ICS415/blob/ae109aef5d827d4470dc5b3a451a7dbba99e4a0d/MinecraftEngine/minecraft.png)

## Setup

```bash
# 1. Clone the repository
git clone https://github.com/7aidary/ICS415.git

```

```bash
--2. Navigate to the minecragtEngine directory
cd ICS415/MinecraftEngine

```
###  3. Open the project in your Java IDE (e.g., IntelliJ or Eclipse)
 
 ### 4. Add the following libraries to your project:
     LWJGL (Lightweight Java Game Library)
     JOML (Java OpenGL Math Library)


### 5. Run the Main class to start the engine

## Notes

- Rendering requires an OpenGL-compatible GPU
- Ensure texture dimensions and atlas mappings match
- Extendable for lighting, chunk saving, and world generation


