package io.quarkus.liquibase.test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.liquibase.LiquibaseDataSource;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.QuarkusUnitTest;

public class LiquibaseExtensionConfigActiveFalseNamedDatasourceStaticInjectionTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.datasource.users.active", "false")
            // We need at least one build-time property for the datasource,
            // otherwise it's considered unconfigured at build time...
            .overrideConfigKey("quarkus.datasource.users.db-kind", "h2")
            // We need this otherwise it's going to be the *default* datasource making everything fail
            .overrideConfigKey("quarkus.datasource.db-kind", "h2")
            .overrideConfigKey("quarkus.datasource.username", "sa")
            .overrideConfigKey("quarkus.datasource.password", "sa")
            .overrideConfigKey("quarkus.datasource.jdbc.url",
                    "jdbc:h2:tcp://localhost/mem:test-quarkus-migrate-at-start;DB_CLOSE_DELAY=-1");

    @Inject
    MyBean myBean;

    @Test
    @DisplayName("If a named datasource is deactivated, the application should boot, but Liquibase should be deactivated for that datasource")
    public void testBootSucceedsButLiquibaseDeactivated() {
        assertThatThrownBy(myBean::useLiquibase)
                .cause()
                .hasMessageContainingAll("Unable to find datasource 'users' for Liquibase",
                        "Datasource 'users' was deactivated through configuration properties.",
                        "To solve this, avoid accessing this datasource at runtime",
                        "Alternatively, activate the datasource by setting configuration property 'quarkus.datasource.\"users\".active'"
                                + " to 'true' and configure datasource 'users'",
                        "Refer to https://quarkus.io/guides/datasource for guidance.");
    }

    @ApplicationScoped
    public static class MyBean {
        @Inject
        @LiquibaseDataSource("users")
        LiquibaseFactory liquibase;

        public void useLiquibase() {
            liquibase.getConfiguration();
        }
    }
}