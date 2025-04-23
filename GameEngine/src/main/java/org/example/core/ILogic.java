package org.example.core;

public interface ILogic {
    void init() throws Exception;
    void input();
    void update(float intervals,MouseInput mouseInput);
    void render();
    void cleanup();
}
