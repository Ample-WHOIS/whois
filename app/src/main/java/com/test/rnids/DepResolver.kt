package com.test.rnids

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object DepResolver {
    @JvmStatic val dependencies: MutableMap<KClass<*>, Any> = mutableMapOf()

    fun Resolve(cls: KClass<*>) : Any
    {
        return if (dependencies.containsKey(cls)) {
            dependencies[cls]!!
        } else {
            val thing = cls.createInstance()
            dependencies[cls] = thing
            thing
        }
    }

    fun Destroy()
    {
        dependencies.clear()
    }
}