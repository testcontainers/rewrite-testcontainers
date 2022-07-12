package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class CassandraContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.CassandraContainerConstructorMigration
            displayName: Use CassandraContainer(String)
            description: Move from CassandraContainer() to CassandraContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use CassandraContainer(String)
                  fqn: org.testcontainers.containers.CassandraContainer
                  code: new CassandraContainer<>("cassandra:3.11.2")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.CassandraContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "cassandra").build())
        },
        java("""
            import org.testcontainers.containers.CassandraContainer;
            class Test {
                void doSomething() {
                    CassandraContainer container = new CassandraContainer<>();
                }
            }
        """,
        """
            import org.testcontainers.containers.CassandraContainer;
            class Test {
                void doSomething() {
                    CassandraContainer container = new CassandraContainer<>("cassandra:3.11.2");
                }
            }
        """
        )
    )

}