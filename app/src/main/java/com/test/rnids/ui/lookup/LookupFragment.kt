package com.test.rnids.ui.lookup

import android.Manifest
import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.rnids.MainActivity
import com.test.rnids.WHOISClientWrapper
import com.test.rnids.databinding.FragmentLookupBinding
import java.io.InputStream
import java.net.IDN
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext
import com.test.rnids.parsers.BasicParser

import android.content.SharedPreferences
import android.os.Handler
import android.text.InputFilter
import androidx.core.widget.doOnTextChanged
import com.test.rnids.util.validateDomain


class LookupFragment : Fragment() {
    private lateinit var lookupViewModel: LookupViewModel
    private var _binding: FragmentLookupBinding? = null

    private val binding get() = _binding!!

    val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lookupViewModel =
            ViewModelProvider(this).get(LookupViewModel::class.java)

        _binding = FragmentLookupBinding.inflate(inflater, container, false)

        _binding!!.queryButton.setOnClickListener { queryButtonListener(_binding!!) }

        _binding!!.textInput.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            {
                _binding!!.textInput.error = null
            }
        }

        _binding!!.textInput.editText!!.doOnTextChanged { _, _, _, _ ->
            _binding!!.textInput.error = null
        }

        MainActivity.whoisClientWrapper.result.observe(viewLifecycleOwner, {
            val parser = BasicParser(requireContext())
            parser.processRaw(it)
            binding.testtext.text = parser.toString()
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun queryButtonListener(binding: FragmentLookupBinding)
    {
        binding.textInput.error = null

        binding.surfaceView.setPush()
        var domain = binding.textInput.editText!!.text.toString()

        if (domain.isEmpty())
        {
            binding.textInput.error = getString(com.test.rnids.R.string.empty_error)
            return
        }

        var tld = false
        if (domain.startsWith('.'))
        {
            tld = true
            domain = domain.removePrefix(".")
        }
        domain = IDN.toASCII(domain, IDN.ALLOW_UNASSIGNED)

        if (!tld && !validateDomain(domain))
        {
            binding.textInput.error = getString(com.test.rnids.R.string.invalid_error)
            return
        }

        binding.queryButton.text = getString(com.test.rnids.R.string.button_loading)

        val mainActivity = context as MainActivity
        scope.launch {
            MainActivity.whoisClientWrapper
                .queryChain(domain, mainActivity.TLDProviderInst.getServer(domain))
        }.invokeOnCompletion {
            Handler(mainActivity.mainLooper).post {
                mainActivity.switchToResults()
            }
        }

        /*val settings: SharedPreferences = (context as MainActivity)
            .getSharedPreferences(PREFS_NAME, 0)
        val editor = settings.edit()
        editor.putInt("homeScore", YOUR_HOME_SCORE)

        editor.apply()*/
    }
}

class BGSurfaceView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    private val renderer: BGRenderer = BGRenderer(
        resources.openRawResource(com.test.rnids.R.raw.bgshader))

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

    private var frame: Int = 3140

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

/*
class BGRendererPerf(glsl: InputStream) : GLSurfaceView.Renderer {
    private val fragmentGLSL: InputStream = glsl
    private var shader: BGShader? = null

    private var frame: Int = 0
    private var activePhase: Boolean = true

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

        glGetIntegerv(GL_FRAMEBUFFER_BINDING, &old_fbo);

        glGenFramebuffers(1, &framebuffer)
        glGenTextures(1, &tex)

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer)
        glBindTexture(GL_TEXTURE_2D, tex)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
            width, height, 0, GL_RGBA,
            GL_UNSIGNED_BYTE, NULL)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
            tex, 0)

        glClearColor(1, 0, 0, 1)
        glClear(GL_COLOR_BUFFER_BIT)

        glBindFramebuffer(GL_FRAMEBUFFER, old_fbo);
    }

    override fun onDrawFrame(unused: GL10) {
        while (activePhase && frame < 314)
        {
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
 */

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
