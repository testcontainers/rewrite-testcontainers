package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class CouchbaseContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.CouchbaseContainerConstructorMigration
            displayName: Use CouchbaseContainer(String)
            description: Move from CouchbaseContainer() to CouchbaseContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use CouchbaseContainer(String)
                  fqn: org.testcontainers.couchbase.CouchbaseContainer
                  code: new CouchbaseContainer("couchbase/server:6.5.1")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.CouchbaseContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "couchbase").build())
        },
        java("""
            import org.testcontainers.couchbase.CouchbaseContainer;
            class Test {
                void doSomething() {
                    CouchbaseContainer container = new CouchbaseContainer();
                }
            }
        """,
        """
            import org.testcontainers.couchbase.CouchbaseContainer;
            class Test {
                void doSomething() {
                    CouchbaseContainer container = new CouchbaseContainer("couchbase/server:6.5.1");
                }
            }
        """
        )
    )

}