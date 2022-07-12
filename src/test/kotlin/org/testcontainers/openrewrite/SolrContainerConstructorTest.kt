package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class SolrContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.SolrContainerConstructorMigration
            displayName: Use SolrContainer(String)
            description: Move from SolrContainer() to SolrContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use SolrContainer(String)
                  fqn: org.testcontainers.containers.SolrContainer
                  code: new SolrContainer("solr:8.3.0")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.SolrContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "solr").build())
        },
        java("""
            import org.testcontainers.containers.SolrContainer;
            class Test {
                void doSomething() {
                    SolrContainer container = new SolrContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.SolrContainer;
            class Test {
                void doSomething() {
                    SolrContainer container = new SolrContainer("solr:8.3.0");
                }
            }
        """
        )
    )

}