package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class MongoDBContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.MongoDBContainerConstructorMigration
            displayName: Use MongoDBContainer(String)
            description: Move from MongoDBContainer() to MongoDBContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use MongoDBContainer(String)
                  fqn: org.testcontainers.containers.MongoDBContainer
                  code: new MongoDBContainer("mongo:4.0.10")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.MongoDBContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "mongodb").build())
        },
        java("""
            import org.testcontainers.containers.MongoDBContainer;
            class Test {
                void doSomething() {
                    MongoDBContainer container = new MongoDBContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.MongoDBContainer;
            class Test {
                void doSomething() {
                    MongoDBContainer container = new MongoDBContainer("mongo:4.0.10");
                }
            }
        """
        )
    )

}