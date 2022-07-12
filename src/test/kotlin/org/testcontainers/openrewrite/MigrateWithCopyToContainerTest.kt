package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest

class MigrateWithCopyToContainerTest : JavaRecipeTest {

    override val parser: JavaParser = JavaParser.fromJavaVersion()
        .logCompilationWarningsAndErrors(true)
        .classpath("junit", "testcontainers")
        .build()

    override val recipe: Recipe
        get() = MigrateWithCopyToContainer()

    @Test
    fun paramIsNotThrowable() = assertUnchanged(
        recipe = recipe,
        before = """
            import org.testcontainers.containers.GenericContainer;
            import org.testcontainers.images.builder.Transferable;

            class Test {
                void doSomething() {
                    GenericContainer container = new GenericContainer<>("myImage")
                        .withCopyToContainer(Transferable.of("a"), "b");
                }
            }
        """
    )

    @Test
    fun convertWithCopyToContainer() = assertChanged(
        recipe = recipe,
        before = """
            import org.testcontainers.containers.GenericContainer;
            import org.testcontainers.utility.MountableFile;
            class Test {
                void doSomething() {
                    GenericContainer container = new GenericContainer<>("myImage")
                        .withCopyFileToContainer(MountableFile.forClasspathResource("a"), "b");
                }
            }
        """,
        after = """
            import org.testcontainers.containers.GenericContainer;
            import org.testcontainers.images.builder.Transferable;

            class Test {
                void doSomething() {
                    GenericContainer container = new GenericContainer<>("myImage").withCopyToContainer(Transferable.of("a"), "b");
                }
            }
        """
    )

}