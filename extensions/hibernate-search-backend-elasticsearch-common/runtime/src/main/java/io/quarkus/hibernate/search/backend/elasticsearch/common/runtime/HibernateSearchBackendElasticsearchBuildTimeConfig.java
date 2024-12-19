package io.quarkus.hibernate.search.backend.elasticsearch.common.runtime;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.search.backend.elasticsearch.ElasticsearchVersion;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithParentName;

@ConfigGroup
public interface HibernateSearchBackendElasticsearchBuildTimeConfig {
    /**
     * The version of Elasticsearch used in the cluster.
     *
     * As the schema is generated without a connection to the server, this item is mandatory.
     *
     * It doesn't have to be the exact version (it can be `7` or `7.1` for instance) but it has to be sufficiently precise
     * to choose a model dialect (the one used to generate the schema) compatible with the protocol dialect (the one used
     * to communicate with Elasticsearch).
     *
     * There's no rule of thumb here as it depends on the schema incompatibilities introduced by Elasticsearch versions. In
     * any case, if there is a problem, you will have an error when Hibernate Search tries to connect to the cluster.
     *
     * @asciidoclet
     */
    Optional<ElasticsearchVersion> version();

    /**
     * The default configuration for the Elasticsearch indexes.
     */
    @WithParentName
    IndexConfig indexDefaults();

    /**
     * Per-index configuration overrides.
     */
    @ConfigDocSection
    @ConfigDocMapKey("index-name")
    Map<String, IndexConfig> indexes();

    @ConfigGroup
    interface IndexConfig {
        /**
         * Configuration for automatic creation and validation of the Elasticsearch schema:
         * indexes, their mapping, their settings.
         */
        SchemaManagementConfig schemaManagement();

        /**
         * Configuration for full-text analysis.
         */
        AnalysisConfig analysis();
    }

    @ConfigGroup
    interface SchemaManagementConfig {

        // @formatter:off
        /**
         * Path to a file in the classpath holding custom index settings to be included in the index definition
         * when creating an Elasticsearch index.
         *
         * The provided settings will be merged with those generated by Hibernate Search, including analyzer definitions.
         * When analysis is configured both through an analysis configurer and these custom settings, the behavior is undefined;
         * it should not be relied upon.
         *
         * See link:{hibernate-search-docs-url}#backend-elasticsearch-configuration-index-settings[this section of the reference documentation]
         * for more information.
         *
         * @asciidoclet
         */
        // @formatter:on
        Optional<String> settingsFile();

        // @formatter:off
        /**
         * Path to a file in the classpath holding a custom index mapping to be included in the index definition
         * when creating an Elasticsearch index.
         *
         * The file does not need to (and generally shouldn't) contain the full mapping:
         * Hibernate Search will automatically inject missing properties (index fields) in the given mapping.
         *
         * See link:{hibernate-search-docs-url}#backend-elasticsearch-mapping-custom[this section of the reference documentation]
         * for more information.
         *
         * @asciidoclet
         */
        // @formatter:on
        Optional<String> mappingFile();

    }

    @ConfigGroup
    interface AnalysisConfig {
        /**
         * One or more xref:#bean-reference-note-anchor[bean references]
         * to the component(s) used to configure full text analysis (e.g. analyzers, normalizers).
         *
         * The referenced beans must implement `ElasticsearchAnalysisConfigurer`.
         *
         * See xref:#analysis-configurer[Setting up the analyzers] for more
         * information.
         *
         * [NOTE]
         * ====
         * Instead of setting this configuration property,
         * you can simply annotate your custom `ElasticsearchAnalysisConfigurer` implementations with `@SearchExtension`
         * and leave the configuration property unset: Hibernate Search will use the annotated implementation automatically.
         * See xref:#plugging-in-custom-components[this section]
         * for more information.
         *
         * If this configuration property is set, it takes precedence over any `@SearchExtension` annotation.
         * ====
         *
         * @asciidoclet
         */
        Optional<List<String>> configurer();
    }
}