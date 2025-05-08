# Block World Engine (LWJGL + JOML)

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
![Alt text](relative/path/to/image.png)

## Setup

1. Install a Java IDE (like IntelliJ or Eclipse)
2. Add LWJGL and JOML to your project's dependencies
3. Make sure you have a texture atlas image available
4. Run the main application class

## Notes

- Rendering requires an OpenGL-compatible GPU
- Ensure texture dimensions and atlas mappings match
- Extendable for lighting, chunk saving, and world generation


