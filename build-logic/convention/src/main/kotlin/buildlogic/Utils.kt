package buildlogic

import org.gradle.api.Project
import java.io.ByteArrayOutputStream


fun List<String>.reduceToText() = buildString {
    append("listOf(")
    appendLine()
    this@reduceToText.forEach {
        append("    ")
        append('"')
        append(it)
        append('"')
        append(',')
        appendLine()
    }
    append(')')
}

fun Project.getHeadHash(): String? = try {
    val outputStream = ByteArrayOutputStream()
    val result = exec {
        workingDir = rootDir
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = outputStream
    }
    result.rethrowFailure()
    outputStream.toString().trimEnd()
} catch (t: Throwable) {
    println("Error getting tag at HEAD: $t")
    null
}