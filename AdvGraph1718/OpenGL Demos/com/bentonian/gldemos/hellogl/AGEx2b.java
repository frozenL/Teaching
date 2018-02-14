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

public class AGEx2b {

  private static void matrices(int program) {
	int uniModel = GL20.glGetUniformLocation(program, "model");
	Matrix4f model = Matrix4f.rotate(60, 1, 1, 0);
	FloatBuffer fboModel = BufferUtils.createFloatBuffer(16);
	model.toBuffer(fboModel);
	GL20.glUniformMatrix4fv(uniModel, false, fboModel);
	
	int uniView = GL20.glGetUniformLocation(program, "view");
	Matrix4f view = Matrix4f.translate(0, 0f, -2.5f);
	FloatBuffer fboView = BufferUtils.createFloatBuffer(16);
	view.toBuffer(fboView);
	GL20.glUniformMatrix4fv(uniView, false, fboView);
	
	int uniProjection = GL20.glGetUniformLocation(program, "projection");
	Matrix4f projection = Matrix4f.perspective(80f, 1f, 0.5f, 10f);
	FloatBuffer fboProjection = BufferUtils.createFloatBuffer(16);
	projection.toBuffer(fboProjection);
	GL20.glUniformMatrix4fv(uniProjection, false, fboProjection);
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
      "layout(location = 0) in vec3 position;",
      "layout(location = 1) in vec3 vertexColor;",
      "uniform mat4 model;",
      "uniform mat4 view;",
      "uniform mat4 projection;",
      "out vec3 fragmentColor;",
      "out vec3 c;",
      "void main() {",
    "  gl_Position = projection * view * model * vec4(position, 1.0);",
      "	 fragmentColor = vertexColor;",
      "  c = position;",
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
    float[] coords = new float[] {
    		-1.0f,-1.0f,-1.0f, // triangle 1 : begin
    	    -1.0f,-1.0f, 1.0f,
    	    -1.0f, 1.0f, 1.0f,
    	    1.0f, 1.0f,-1.0f, // triangle 2 : begin
    	    -1.0f,-1.0f,-1.0f,
    	    -1.0f, 1.0f,-1.0f,
    	    1.0f,-1.0f, 1.0f,
    	    -1.0f,-1.0f,-1.0f,
    	    1.0f,-1.0f,-1.0f,
    	    1.0f, 1.0f,-1.0f,
    	    1.0f,-1.0f,-1.0f,
    	    -1.0f,-1.0f,-1.0f,
    	    -1.0f,-1.0f,-1.0f,
    	    -1.0f, 1.0f, 1.0f,
    	    -1.0f, 1.0f,-1.0f,
    	    1.0f,-1.0f, 1.0f,
    	    -1.0f,-1.0f, 1.0f,
    	    -1.0f,-1.0f,-1.0f,
    	    -1.0f, 1.0f, 1.0f,
    	    -1.0f,-1.0f, 1.0f,
    	    1.0f,-1.0f, 1.0f,
    	    1.0f, 1.0f, 1.0f,
    	    1.0f,-1.0f,-1.0f,
    	    1.0f, 1.0f,-1.0f,
    	    1.0f,-1.0f,-1.0f,
    	    1.0f, 1.0f, 1.0f,
    	    1.0f,-1.0f, 1.0f,
    	    1.0f, 1.0f, 1.0f,
    	    1.0f, 1.0f,-1.0f,
    	    -1.0f, 1.0f,-1.0f,
    	    1.0f, 1.0f, 1.0f,
    	    -1.0f, 1.0f,-1.0f,
    	    -1.0f, 1.0f, 1.0f,
    	    1.0f, 1.0f, 1.0f,
    	    -1.0f, 1.0f, 1.0f,
    	    1.0f,-1.0f, 1.0f,
    };
    FloatBuffer fboVertices = BufferUtils.createFloatBuffer(coords.length);
    fboVertices.put(coords);                                // Copy the vertex coords into the floatbuffer
    fboVertices.flip();                                     // Mark the floatbuffer ready for reads

    // Store the FloatBuffer's contents in a Vertex Buffer Object
    int vboVertices = GL15.glGenBuffers();                  // Get an OGL name for the VBO
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertices);   // Activate the VBO
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fboVertices, GL15.GL_STATIC_DRAW);  // Send VBO data to GPU
    System.out.println(vboVertices);

    float[] colors = new float[] {
    		0.583f,  0.771f,  0.014f,
    	    0.609f,  0.115f,  0.436f,
    	    0.327f,  0.483f,  0.844f,
    	    0.822f,  0.569f,  0.201f,
    	    0.435f,  0.602f,  0.223f,
    	    0.310f,  0.747f,  0.185f,
    	    0.597f,  0.770f,  0.761f,
    	    0.559f,  0.436f,  0.730f,
    	    0.359f,  0.583f,  0.152f,
    	    0.483f,  0.596f,  0.789f,
    	    0.559f,  0.861f,  0.639f,
    	    0.195f,  0.548f,  0.859f,
    	    0.014f,  0.184f,  0.576f,
    	    0.771f,  0.328f,  0.970f,
    	    0.406f,  0.615f,  0.116f,
    	    0.676f,  0.977f,  0.133f,
    	    0.971f,  0.572f,  0.833f,
    	    0.140f,  0.616f,  0.489f,
    	    0.997f,  0.513f,  0.064f,
    	    0.945f,  0.719f,  0.592f,
    	    0.543f,  0.021f,  0.978f,
    	    0.279f,  0.317f,  0.505f,
    	    0.167f,  0.620f,  0.077f,
    	    0.347f,  0.857f,  0.137f,
    	    0.055f,  0.953f,  0.042f,
    	    0.714f,  0.505f,  0.345f,
    	    0.783f,  0.290f,  0.734f,
    	    0.722f,  0.645f,  0.174f,
    	    0.302f,  0.455f,  0.848f,
    	    0.225f,  0.587f,  0.040f,
    	    0.517f,  0.713f,  0.338f,
    	    0.053f,  0.959f,  0.120f,
    	    0.393f,  0.621f,  0.362f,
    	    0.673f,  0.211f,  0.457f,
    	    0.820f,  0.883f,  0.371f,
    	    0.982f,  0.099f,  0.879f,
    };
    FloatBuffer fboColors = BufferUtils.createFloatBuffer(colors.length);
    fboColors.put(colors);                                // Copy the vertex coords into the floatbuffer
    fboColors.flip();                                     // Mark the floatbuffer ready for reads

    // Store the FloatBuffer's contents in a Vertex Buffer Object
    int vboColors = GL15.glGenBuffers();                  // Get an OGL name for the VBO
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors);   // Activate the VBO
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fboColors, GL15.GL_STATIC_DRAW);  // Send VBO data to GPU
    System.out.println(vboColors);

    
    // Bind the VBO in a Vertex Array Object
    int vao = GL30.glGenVertexArrays();             // Get an OGL name for the VAO
    GL30.glBindVertexArray(vao);                    // Activate the VAO
    GL20.glEnableVertexAttribArray(0);              // Enable the VAO's first attribute (0)
    GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);  // Link VBO to VAO attrib 0
    GL20.glEnableVertexAttribArray(1);              // Enable the VAO's first attribute (0)
    GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);  // Link VBO to VAO attrib 0

    // matrices
    matrices(program);
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Loop until window is closed

	while (!GLFW.glfwWindowShouldClose(window)) {
	      GLFW.glfwPollEvents();

	      // Enable depth test
	      GL11.glEnable(GL11.GL_DEPTH_TEST);
	      // Accept fragment if it closer to the camera than the former one
	      GL11.glDepthFunc(GL11.GL_LESS);
	      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	      GL20.glEnableVertexAttribArray(0);              // Enable the VAO's first attribute (0)
	      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertices);   // Activate the VBO
	      GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);  // Link VBO to VAO attrib 0
	      
	      GL20.glEnableVertexAttribArray(1);              // Enable the VAO's first attribute (0)
	      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors);   // Activate the VBO
	      GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);  // Link VBO to VAO attrib 0
//	      GL30.glBindVertexArray(vao);
	      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0 /* start */, coords.length / 3/* num vertices */);
	      GLFW.glfwSwapBuffers(window);
	}
    ///////////////////////////////////////////////////////////////////////////
    // Clean up

    GL15.glDeleteBuffers(vboVertices);
    GL15.glDeleteBuffers(vboColors);
    GL30.glDeleteVertexArrays(vao);
    GLFW.glfwDestroyWindow(window);
    GLFW.glfwTerminate();
    GLFW.glfwSetErrorCallback(null).free();
  }
}
