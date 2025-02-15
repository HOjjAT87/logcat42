package buildlogic.versioning

data class ChangeLog(
    val features: List<String>,
    val bugFixes: List<String>,
)
