package buildlogic


object BuildConfigCreator {

    fun createClassFileText(
        packageName: String,
        bugFixes: String,
        features: String,
        values: Map<String, Any>
    ): String {
        val globalValues = serializeValues(values)
        val bugFixesValue = "val bugFixes = $bugFixes"
        val featuresValue = "val features = $features"

        val entries = listOf(bugFixesValue, featuresValue, globalValues)

        return buildString {
            append(packageName)
            appendLine()
            appendLine()

            entries.forEach {
                append(it)
                appendLine()
                appendLine()
            }
            deleteRange(length - 3, length)
        }
    }

    private fun serializeValues(values: Map<String, Any>): String = buildString {
        values.entries.forEach { (name, value) ->
            value.toCodeString()?.let {
                append("val ")
                append(name)
                append(": ")
                append(value::class.simpleName)
                append(" = ")
                append(it)
                appendLine()
                appendLine()
            }
        }
    }

    private fun Any.toCodeString(): String? = when (this) {
        is Number, is Boolean -> toString()
        is String -> "\"$this\""
        is Char -> "\'$this\'"
        else -> null

    }
}