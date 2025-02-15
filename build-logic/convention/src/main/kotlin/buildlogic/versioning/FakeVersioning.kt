package buildlogic.versioning

class FakeVersioning : Versioning {

    override fun createChangeLog(): ChangeLog {
        return ChangeLog(
            listOf("foo", "bar"),
            listOf("bug-1", "bug-2", "bug-3")
        )
    }
}