package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.openrewrite.properties.PropertiesRecipeTest
import java.nio.file.Path

class UpgradeTestcontainersGradlePropertiesVersionTest : PropertiesRecipeTest {

    override val recipe =
        UpgradeTestcontainersGradlePropertiesVersion("1.17.3")

    @Test
    fun changeValueTestcontainersVersion(@TempDir tempDir: Path) {
        val properties = tempDir.resolve("gradle.properties").apply {
            toFile().parentFile.mkdirs()
            toFile().writeText("testcontainersVersion=1.16.2")
        }.toFile()

        assertChanged(recipe = recipe, before = properties, after = "testcontainersVersion=1.17.3")
    }

    @Test
    fun changeValueTestcontainersDotVersion(@TempDir tempDir: Path) {
        val properties = tempDir.resolve("gradle.properties").apply {
            toFile().parentFile.mkdirs()
            toFile().writeText("testcontainers.version=1.16.2")
        }.toFile()

        assertChanged(recipe = recipe, before = properties, after = "testcontainers.version=1.17.3")
    }
}
