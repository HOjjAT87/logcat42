package buildlogic.tasks


import buildlogic.reduceToText
import buildlogic.versioning.Versioning
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.TaskInternal
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class GenerateChangeLogTask @Inject constructor(
    projectLayout: ProjectLayout,
) : DefaultTask() {

    @get:Input
    abstract val shouldRun: Property<Boolean>

    // Only using gitHeadHash as input so task is rerun when git head changes~
    @get:Input
    abstract val gitHeadHash: Property<String>

    @get: OutputFile
    val bugFixFile: Provider<RegularFile> =
        projectLayout.buildDirectory.file(BUG_FIX_FILE_NAME)

    @get: OutputFile
    val featureFile: Provider<RegularFile> =
        projectLayout.buildDirectory.file(FEATURES_FILE_NAME)

    private val versioning: Versioning = Versioning.get()

    @TaskAction
    fun create() {
        // Get the change-log from the versioning interface
        val changeLog = versioning.createChangeLog()

        // Convert the change-log to a writable-format for other tasks to use
        val bugFixesText = changeLog.bugFixes.reduceToText()
        val featuresFixesText = changeLog.features.reduceToText()

        // Write each type to a separate file
        bugFixFile.get().asFile.writeText(bugFixesText)
        featureFile.get().asFile.writeText(featuresFixesText)
    }


    // Skip this task if change-log or build config generation is disabled.
    override fun getOnlyIf(): Spec<in TaskInternal> = Spec<Task> {
        shouldRun.get()
    }

    companion object {
        internal const val NAME = "buildConfig"
        private const val BUG_FIX_FILE_NAME = "bugFixes.txt"
        private const val FEATURES_FILE_NAME = "features.txt"
    }
}