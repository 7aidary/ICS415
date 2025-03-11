public interface SceneObject {
    /**
     * Computes the intersection of a ray with this object.
     *
     * @param rayOrigin    The origin of the ray.
     * @param rayDirection The direction of the ray.
     * @return The distance t from the ray origin to the intersection point,
     * or null if there is no intersection.
     */
    double intersectRay(Vector3D rayOrigin, Vector3D rayDirection);

    /**
     * Returns the surface normal at the given point.
     * @param point The point on the surface.
     * @return The normalized surface normal.
     */


    /**
     * Returns the color of the object.
     */
    int getColor();



    /**
     * Get the object's specular exponent.
     */
    int getSpecular();

    /**
     * Get the reflection coefficient.
     */
    double getReflective();

    /**
     * Get the transparency coefficient.
     */
    double getTransparency();

    /**
     * Get the refraction index.
     */
    double getRefraction();

    // Optionally include getters for specular, reflective, transparency, refraction, etc.
}