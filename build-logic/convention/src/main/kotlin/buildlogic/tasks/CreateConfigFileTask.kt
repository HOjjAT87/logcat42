package buildlogic.tasks

import buildlogic.BuildConfigCreator
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject


abstract class CreateConfigFileTask @Inject constructor(
    projectLayout: ProjectLayout,
) : DefaultTask() {

    @get: InputFile
    abstract val bugFixesFile: RegularFileProperty

    @get: InputFile
    abstract val featuresFile: RegularFileProperty

    @get:Input
    abstract val customValuesMap: MapProperty<String, Any>

    @get: OutputFile
    val buildConfigFile: Provider<RegularFile> =
        projectLayout.buildDirectory.file(GENERATED_FILE_PATH)

    @TaskAction
    fun createFile() {
        // Read the change-log from input files
        val bugFixesText = bugFixesFile.get().asFile.readText()
        val featuresText = featuresFile.get().asFile.readText()


        // Generate the Kotlin file
        val generatedKtFile = BuildConfigCreator.createClassFileText(
            BUILD_CONFIG_PACKAGE,
            bugFixesText,
            featuresText,
            customValuesMap.get()
        )

        // Write the Kotlin file to disk
        buildConfigFile.get().asFile.writeText(generatedKtFile)
    }


    companion object {
        internal const val NAME = "createBuildConfig"
        private const val BUILD_CONFIG_PACKAGE = "package com.example.logcat42"
        const val GENERATED_DIR_PATH = "generated/source/custom/"
        private const val GENERATED_FILE_PATH = "$GENERATED_DIR_PATH/com/example/logcat42/BuildConfig.kt"
    }
}
