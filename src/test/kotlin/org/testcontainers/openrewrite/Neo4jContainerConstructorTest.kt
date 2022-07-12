package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class Neo4jContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.Neo4jContainerConstructorMigration
            displayName: Use Neo4jContainer(String)
            description: Move from Neo4jContainer() to Neo4jContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use Neo4jContainer(String)
                  fqn: org.testcontainers.containers.Neo4jContainer
                  code: new Neo4jContainer<>("neo4j:4.4")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.Neo4jContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "neo4j").build())
        },
        java("""
            import org.testcontainers.containers.Neo4jContainer;
            class Test {
                void doSomething() {
                    Neo4jContainer container = new Neo4jContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.Neo4jContainer;
            class Test {
                void doSomething() {
                    Neo4jContainer container = new Neo4jContainer<>("neo4j:4.4");
                }
            }
        """
        )
    )

}