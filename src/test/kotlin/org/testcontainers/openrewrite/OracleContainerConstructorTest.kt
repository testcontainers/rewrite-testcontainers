package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class OracleContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.OracleContainerConstructorMigration
            displayName: Use OracleContainer(String)
            description: Move from OracleContainer() to OracleContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use OracleContainer(String)
                  fqn: org.testcontainers.containers.OracleContainer
                  code: new OracleContainer("gvenzl/oracle-xe:18.4.0-slim")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.OracleContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "oracle-xe").build())
        },
        java("""
            import org.testcontainers.containers.OracleContainer;
            class Test {
                void doSomething() {
                    OracleContainer container = new OracleContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.OracleContainer;
            class Test {
                void doSomething() {
                    OracleContainer container = new OracleContainer("gvenzl/oracle-xe:18.4.0-slim");
                }
            }
        """
        )
    )

}