package com.test.rnids

import android.content.Context
import com.test.rnids.providers.FileProvider
import com.test.rnids.providers.PriorityProvider
import java.lang.reflect.Constructor
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object DepResolver {
    @JvmStatic val dependencies: MutableMap<KClass<*>, Any> = mutableMapOf()
    @JvmStatic val dependencyCtorArgs: Map<KClass<*>, KClass<*>> = mapOf( // TODO
        Pair(PriorityProvider::class, Context::class),
        Pair(FileProvider::class, Context::class)
    )

    @JvmStatic val commonArgs: MutableMap<KClass<*>, Any?> = mutableMapOf(
        Pair(Context::class, null)
    )

    inline fun <reified T : Any> setCommonArg(thing: T)
    {
        if (commonArgs[T::class] == null)
        {
            commonArgs[T::class] = thing
        }
    }

    fun Resolve(cls: KClass<*>) : Any
    {
        return if (dependencies.containsKey(cls)) {
            dependencies[cls]!!
        } else {
            val thing : Any
            if (!dependencyCtorArgs.containsKey(cls))
            {
                thing = cls.createInstance()
            }
            else
            {
                val ctor: Constructor<*> = cls.java.getConstructor(dependencyCtorArgs[cls]!!.java)
                thing = ctor.newInstance(dependencyCtorArgs[cls]?.let { getCommonArg(it) })
            }

            dependencies[cls] = thing
            thing
        }
    }

    private fun getCommonArg(ctorArg: KClass<*>) : Any
    {
        if (commonArgs.containsKey(ctorArg))
        {
            return commonArgs[ctorArg]!!
        }
        else
        {
            throw ClassNotFoundException()
        }
    }

    fun Destroy()
    {
        dependencies.clear()
        commonArgs.clear()
    }
}