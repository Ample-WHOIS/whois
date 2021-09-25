package com.test.rnids

import kotlin.reflect.KClass

object DepResolver {
    @JvmStatic val dependencies: MutableMap<KClass<*>, Any> = mutableMapOf()

    fun Resolve(cls: KClass<*>) : Any
    {
        if (dependencies.containsKey(cls))
        {
            return dependencies[cls]!!
        }
        else
        {
            val thing = cls::class.java.newInstance()
            dependencies[cls] = thing
            return thing
        }
    }

    fun Destroy()
    {
        dependencies.clear()
    }
}