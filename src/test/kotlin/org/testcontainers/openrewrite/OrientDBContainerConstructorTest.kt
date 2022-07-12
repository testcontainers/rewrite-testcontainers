package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class OrientDBContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.OrientDBContainerConstructorMigration
            displayName: Use OrientDBContainer(String)
            description: Move from OrientDBContainer() to OrientDBContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use OrientDBContainer(String)
                  fqn: org.testcontainers.containers.OrientDBContainer
                  code: new OrientDBContainer("orientdb:3.0.24-tp3")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.OrientDBContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "orientdb").build())
        },
        java("""
            import org.testcontainers.containers.OrientDBContainer;
            class Test {
                void doSomething() {
                    OrientDBContainer container = new OrientDBContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.OrientDBContainer;
            class Test {
                void doSomething() {
                    OrientDBContainer container = new OrientDBContainer("orientdb:3.0.24-tp3");
                }
            }
        """
        )
    )

}