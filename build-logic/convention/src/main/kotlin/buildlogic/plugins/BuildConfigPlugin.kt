package buildlogic.plugins

import buildlogic.extensions.BuildConfigExtension
import buildlogic.getHeadHash
import buildlogic.tasks.CreateConfigFileTask
import buildlogic.tasks.GenerateChangeLogTask
import org.gradle.kotlin.dsl.configure
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BuildConfigPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // Create Extension
        val buildConfigExtension = project.extensions.create(
            BuildConfigExtension.NAME,
            BuildConfigExtension::class.java
        )

        // Set the default values for the extension
        buildConfigExtension.setConventions()

        // Register and configure changeLog generation task
        val generateChangeLogTask = project.tasks.register(
            GenerateChangeLogTask.NAME,
            GenerateChangeLogTask::class.java
        ) {
            // Set the task's isEnabled to be the combination of the extension's enabled &
            // generateChangeLog configs.
            val combinedEnablerProvider = with(buildConfigExtension) {
                enabled.zip(generateChangeLog) { f, s ->
                    f and s
                }
            }

            shouldRun.set(combinedEnablerProvider)

            // Get current Git hash, so the chang-log generating does not skip on new commits!
            val currentHeadHash = project.provider { project.getHeadHash() }
            gitHeadHash.set(currentHeadHash)
        }

        // Register and configure CreateConfigFileTask
        val createConfigFileTask = project.tasks.register(
            CreateConfigFileTask.NAME,
            CreateConfigFileTask::class.java,
        ) {
            // hook the GenerateChangeLogTask outputs to this task's input
            bugFixesFile.set(generateChangeLogTask.flatMap { it.bugFixFile })
            featuresFile.set(generateChangeLogTask.flatMap { it.featureFile })
            customValuesMap.set(buildConfigExtension.customValues)

            dependsOn(generateChangeLogTask)
        }

        // Register the lifecycle task customBuildConfig that embodies the actionable tasks
        // Give it group and description so it can be seen using the tasks command
        val customBuildConfigTask = project.tasks.register(BUILD_CONFIG_TASK_NAME) {
            group = "build"
            description = BUILD_CONFIG_DESCRIPTION

            dependsOn(createConfigFileTask)

            doLast {
                println("BuildConfig file generated!")
            }
        }

        project.tasks.whenObjectAdded {
            when {
                // Add dependency so our task is run on make for different build-types
                name.contains("assemble") -> {
                    dependsOn(customBuildConfigTask)
                }
                // Create buildConfig file before compiling
                name.contains("compile") -> {
                    mustRunAfter(createConfigFileTask)
                }
            }
        }

        // Add generated directory to Android sources
        project.extensions.configure<ApplicationExtension> {
            sourceSets.named("main") {
                kotlin {
                    srcDir(project.layout.buildDirectory.dir(CreateConfigFileTask.GENERATED_DIR_PATH))
                }
            }
        }
    }

    private fun BuildConfigExtension.setConventions() {
        enabled.convention(false)
        generateChangeLog.convention(false)
        customValues.convention(emptyMap())
    }

    companion object {
        const val BUILD_CONFIG_TASK_NAME = "customBuildConfig"
        const val BUILD_CONFIG_DESCRIPTION =
            "Generates a custom Kotlin file populated with pre-configured values and an auto-generated change-log"
    }
}