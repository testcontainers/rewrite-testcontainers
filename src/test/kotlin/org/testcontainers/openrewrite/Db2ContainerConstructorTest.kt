package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class Db2ContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.Db2ContainerConstructorMigration
            displayName: Use Db2Container(String)
            description: Move from Db2Container() to Db2Container(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use Db2Container(String)
                  fqn: org.testcontainers.containers.Db2Container
                  code: new Db2Container("ibmcom/db2:11.5.0.0a")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.Db2ContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "db2").build())
        },
        java("""
            import org.testcontainers.containers.Db2Container;
            class Test {
                void doSomething() {
                    Db2Container container = new Db2Container();
                }
            }
        """,
        """
            import org.testcontainers.containers.Db2Container;
            class Test {
                void doSomething() {
                    Db2Container container = new Db2Container("ibmcom/db2:11.5.0.0a");
                }
            }
        """
        )
    )

}