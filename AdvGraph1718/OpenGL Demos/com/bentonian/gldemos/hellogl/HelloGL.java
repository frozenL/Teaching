package com.bentonian.gldemos.hellogl;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class HelloGL {

	public static float[] getFilledArcVertexes(float x, float y, float radius, double startingAngleDeg, double endAngleDeg, int slices) {
        double arcAngleLength = (endAngleDeg - startingAngleDeg) / 360f;

        float[] vertexes = new float[slices*6+6];

        double initAngle = Math.PI / 180f * startingAngleDeg;
        float prevXA = (float) Math.sin(initAngle) * radius;
        float prevYA = (float) Math.cos(initAngle) * radius;

        for(int arcIndex = 0; arcIndex < slices+1; arcIndex++) {
            double angle = Math.PI * 2 * ((float)arcIndex) / ((float)slices);
            angle += Math.PI / 180f;
            angle *= arcAngleLength;
            int index = arcIndex * 6;
            float xa = (float) Math.sin(angle) * radius;
            float ya = (float) Math.cos(angle) * radius;
            vertexes[index] = x;
            vertexes[index+1] = y;
            vertexes[index+2] = x+prevXA;
            vertexes[index+3] = y+prevYA;
            vertexes[index+4] = x+xa;
            vertexes[index+5] = y+ya;
            prevXA = xa;
            prevYA = ya;
        }

        return vertexes;
  }

  public static void main(String[] args) {

    ///////////////////////////////////////////////////////////////////////////
    // Set up GLFW window

    GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
    GLFW.glfwSetErrorCallback(errorCallback);
    GLFW.glfwInit();
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
    long window = GLFW.glfwCreateWindow(600 /* width */, 600 /* height */, "HelloGL", 0, 0);
    GLFW.glfwMakeContextCurrent(window);
    GLFW.glfwSwapInterval(1);
    GLFW.glfwShowWindow(window);

    ///////////////////////////////////////////////////////////////////////////
    // Set up OpenGL

    GL.createCapabilities();
    GL11.glClearColor(0.2f, 0.4f, 0.6f, 0.0f);
    GL11.glClearDepth(1.0f);

    ///////////////////////////////////////////////////////////////////////////
    // Set up minimal shader programs

    // Vertex shader source
    String[] vertex_shader = {
      "#version 330\n",
      "in vec3 v;",
      "out vec3 c;",
      "void main() {",
      "  gl_Position = vec4(v, 1.0);",
      "	 c = v;",
      "}"
    };

    // Fragment shader source
    String[] fragment_shader = {
        "#version 330\n",
        "in vec3 c;",
        "out vec4 frag_color;",
        "void main() {",
        "  vec3 color;",
        "  vec3 position, useBrick;",
        "  position = c / vec3(0.3, 0.15, 1);",
        "  if (fract(position.y * 0.5) > 0.5)",
        "        position.x += 0.5;",
        "  position = fract(position);",
        "  useBrick = step(position, vec3(0.9, 0.9, 1));",
        "  frag_color = vec4(mix(vec3(1, 1, 1), vec3(1, 0, 0), useBrick.x * useBrick.y), 1);",
        "}"
    };

    // Compile vertex shader
    int vs = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
    GL20.glShaderSource(vs, vertex_shader);
    GL20.glCompileShader(vs);
    
    // Compile fragment shader
    int fs = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
    GL20.glShaderSource(fs, fragment_shader);
    GL20.glCompileShader(fs);
    
    // Link vertex and fragment shaders into an active program
    int program = GL20.glCreateProgram();
    GL20.glAttachShader(program, vs);
    GL20.glAttachShader(program, fs);
    GL20.glLinkProgram(program);
    GL20.glUseProgram(program);
    
    ///////////////////////////////////////////////////////////////////////////
    // Set up data

    // Fill a Java FloatBuffer object with memory-friendly floats
    // triangle
    // float[] coords = new float[] {  -0.5f, -0.5f, 0,  0, 0.0f, 0,  0.5f, -0.5f, 0,
    								   // -1.0f, -0.5f, 0, -1.0f, 0.0f, 0, -0.5f, -0.5f, 0};
    // circle
    float[] coords = getFilledArcVertexes(0, 0, 0.5f, 0, 360, 200);
    FloatBuffer fbo = BufferUtils.createFloatBuffer(coords.length);
    fbo.put(coords);                                // Copy the vertex coords into the floatbuffer
    fbo.flip();                                     // Mark the floatbuffer ready for reads

    // Store the FloatBuffer's contents in a Vertex Buffer Object
    int vbo = GL15.glGenBuffers();                  // Get an OGL name for the VBO
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);   // Activate the VBO
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fbo, GL15.GL_STATIC_DRAW);  // Send VBO data to GPU

    // Bind the VBO in a Vertex Array Object
    int vao = GL30.glGenVertexArrays();             // Get an OGL name for the VAO
    GL30.glBindVertexArray(vao);                    // Activate the VAO
    GL20.glEnableVertexAttribArray(0);              // Enable the VAO's first attribute (0)
    // triangle
    // GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);  // Link VBO to VAO attrib 0
    // circle
    GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);  // Link VBO to VAO attrib 0

    ///////////////////////////////////////////////////////////////////////////
    // Loop until window is closed

	while (!GLFW.glfwWindowShouldClose(window)) {
	      GLFW.glfwPollEvents();

	      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	      GL30.glBindVertexArray(vao);
	      // triangle
	      // GL11.glDrawArrays(GL11.GL_TRIANGLES, 0 /* start */, 3 /* num vertices */);
	      // circle
	      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0 /* start */, coords.length / 2/* num vertices */);
	      GLFW.glfwSwapBuffers(window);
	}
    ///////////////////////////////////////////////////////////////////////////
    // Clean up

    GL15.glDeleteBuffers(vbo);
    GL30.glDeleteVertexArrays(vao);
    GLFW.glfwDestroyWindow(window);
    GLFW.glfwTerminate();
    GLFW.glfwSetErrorCallback(null).free();
  }
}
