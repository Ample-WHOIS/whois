package com.test.rnids.ui.lookup

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.rnids.databinding.FragmentLookupBinding
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


class LookupFragment : Fragment() {

    private lateinit var lookupViewModel: LookupViewModel
    private var _binding: FragmentLookupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lookupViewModel =
            ViewModelProvider(this).get(LookupViewModel::class.java)

        _binding = FragmentLookupBinding.inflate(inflater, container, false)

        _binding!!.queryButton.setOnClickListener { queryButtonListener(_binding!!) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun queryButtonListener(binding: FragmentLookupBinding)
    {
        binding.queryButton.text = getString(com.test.rnids.R.string.button_loading)
        binding.surfaceView.setPush()
    }
}

class BGSurfaceView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    private val renderer: BGRenderer = BGRenderer(resources.openRawResource(com.test.rnids.R.raw.bgshader))

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
    }

    fun setPush()
    {
        renderer.setPush()
    }
}

class BGRenderer(glsl: InputStream) : GLSurfaceView.Renderer {
    private val fragmentGLSL: InputStream = glsl
    private var shader: BGShader? = null

    private var frame: Int = 1000

    private var push: Boolean = false

    var vertices = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

    override fun onSurfaceCreated(unused: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        shader = BGShader(fragmentGLSL)
        glUseProgram(shader!!.glprog)
    }

    override fun onDrawFrame(unused: GL10) {
        glGetUniformLocation(shader!!.glprog, "uFrame").also { f ->
            glUniform1i(f, frame++)
        }

        if (push)
        {
            push = false
            glGetUniformLocation(shader!!.glprog, "uPush").also { p ->
                glUniform1i(p, frame - 1)
            }
        }

        glGetAttribLocation(shader!!.glprog, "aPosition").also { pos ->
            glEnableVertexAttribArray(pos)

            glVertexAttribPointer(pos, 2, GL_FLOAT, false, 0, vertexBuffer)

            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
            glDisableVertexAttribArray(pos)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        glGetUniformLocation(shader!!.glprog, "uResolution").also { loc ->
            glUniform3f(loc, width.toFloat(), height.toFloat(), 1.0f)
        }

        glViewport(0, 0, width, height)
    }

    fun setPush() {
        push = true
    }
}

class BGShader(glsl: InputStream) {
    var glprog: Int
    
    init {
        val vertexCode: String = "attribute vec2 aPosition;" +
                "void main() {" +
                "    gl_Position = vec4(aPosition, 0.0, 1.0);" +
                "}"

        var fragmentCode: String = ""
        try {
            val b = ByteArray(glsl.available())
            glsl.read(b)
            fragmentCode = String(b)
        } catch (e: Exception) { }

        val vertexShader: Int = loadShader(GL_VERTEX_SHADER, vertexCode)
        val fragmentShader: Int = loadShader(GL_FRAGMENT_SHADER, fragmentCode)

        glprog = glCreateProgram().also {
            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return glCreateShader(type).also { shader ->
            glShaderSource(shader, shaderCode)
            glCompileShader(shader)
        }
    }
}
