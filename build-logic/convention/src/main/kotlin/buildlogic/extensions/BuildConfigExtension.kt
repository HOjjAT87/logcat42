package buildlogic.extensions

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface BuildConfigExtension {

    val enabled: Property<Boolean>

    val generateChangeLog: Property<Boolean>

    val customValues: MapProperty<String, Any>

    companion object {
        internal const val NAME = "buildConfig"
    }
}