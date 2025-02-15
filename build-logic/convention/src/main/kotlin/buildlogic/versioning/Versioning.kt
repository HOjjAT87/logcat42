package buildlogic.versioning

interface Versioning {

    fun createChangeLog(): ChangeLog

    companion object {
        fun get(): Versioning = FakeVersioning()
    }
}