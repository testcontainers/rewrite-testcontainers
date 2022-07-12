package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class ElasticsearchContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.ElasticsearchContainerConstructorMigration
            displayName: Use ElasticsearchContainer(String)
            description: Move from ElasticsearchContainer() to ElasticsearchContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use ElasticsearchContainer(String)
                  fqn: org.testcontainers.elasticsearch.ElasticsearchContainer
                  code: new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.9.2")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.ElasticsearchContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "elasticsearch").build())
        },
        java("""
            import org.testcontainers.elasticsearch.ElasticsearchContainer;
            class Test {
                void doSomething() {
                    ElasticsearchContainer container = new ElasticsearchContainer();
                }
            }
        """,
        """
            import org.testcontainers.elasticsearch.ElasticsearchContainer;
            class Test {
                void doSomething() {
                    ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.9.2");
                }
            }
        """
        )
    )

}