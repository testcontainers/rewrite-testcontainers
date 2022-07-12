package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class MockServerContainerConstructorTest : RewriteTest {

    @Test
    fun convertWithCopyFileToContainer() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.containers.MockServerContainer
            displayName: Use MockServerContainer(String)
            description: Move from MockServerContainer() to MockServerContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use MockServerContainer(String)
                  fqn: org.testcontainers.containers.MockServerContainer
                  code: new MockServerContainer("jamesdbloom/mockserver:mockserver-5.5.4")
            """.trimIndent().byteInputStream(), "org.testcontainers.containers.MockServerContainer")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "mockserver").build())
        },
        java("""
            import org.testcontainers.containers.MockServerContainer;
            class Test {
                void doSomething() {
                    MockServerContainer container = new MockServerContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.MockServerContainer;
            class Test {
                void doSomething() {
                    MockServerContainer container = new MockServerContainer("jamesdbloom/mockserver:mockserver-5.5.4");
                }
            }
        """
        )
    )

}