package io.quarkus.kubernetes.deployment;

import static io.quarkus.kubernetes.deployment.Constants.KUBERNETES;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import io.dekorate.kubernetes.config.DeploymentStrategy;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.kubernetes.spi.DeployStrategy;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Kubernetes
 */
@ConfigMapping(prefix = "quarkus.kubernetes")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface KubernetesConfig extends PlatformConfiguration {

    @Override
    default String targetPlatformName() {
        return Constants.KUBERNETES;
    }

    /**
     * The kind of the deployment resource to use.
     * Supported values are 'StatefulSet', 'Job', 'CronJob' and 'Deployment' defaulting to the latter.
     */
    Optional<DeploymentResourceKind> deploymentKind();

    /**
     * The target deployment platform. Defaults to kubernetes. Can be kubernetes, openshift, knative, minikube etc.,
     * or any combination of the above as comma separated list.
     */
    Optional<List<String>> deploymentTarget();

    /**
     * Specifies the deployment strategy.
     */
    @WithDefault("None")
    DeploymentStrategy strategy();

    /**
     * Specifies rolling update configuration. The configuration is applied when DeploymentStrategy == RollingUpdate, or
     * when explicit configuration has been provided. In the later case RollingUpdate is assumed.
     */
    RollingUpdateConfig rollingUpdate();

    /**
     * The number of desired pods
     */
    @WithDefault("1")
    Integer replicas();

    /**
     * The nodePort to set when serviceType is set to node-port.
     */
    OptionalInt nodePort();

    /**
     * Ingress configuration
     */
    IngressConfig ingress();

    /**
     * Job configuration. It's only used if and only if {@code quarkus.kubernetes.deployment-kind} is `Job`.
     */
    JobConfig job();

    /**
     * CronJob configuration. It's only used if and only if {@code quarkus.kubernetes.deployment-kind} is `CronJob`.
     */
    CronJobConfig cronJob();

    /**
     * Debug configuration to be set in pods.
     */
    DebugConfig remoteDebug();

    /**
     * Flag to enable init task externalization. When enabled (default), all initialization tasks created by
     * extensions, will be externalized as Jobs. In addition, the deployment will wait for these jobs.
     *
     * @deprecated use {@link #initTasks} configuration instead
     */
    @Deprecated(since = "3.1", forRemoval = true)
    @WithDefault("true")
    boolean externalizeInit();

    /**
     * Init tasks configuration.
     * <p>
     * The init tasks are automatically generated by extensions like Flyway to perform the database migration before staring
     * up the application.
     * <p>
     * This property is only taken into account if `quarkus.kubernetes.externalize-init` is true.
     */
    Map<String, InitTaskConfig> initTasks();

    /**
     * Default Init tasks configuration.
     * <p>
     * The init tasks are automatically generated by extensions like Flyway to perform the database migration before staring
     * up the application.
     */
    InitTaskConfig initTaskDefaults();

    /**
     * Optionally set directory generated kubernetes resources will be written to. Default is `target/kubernetes`.
     */
    Optional<String> outputDirectory();

    /**
     * If set to true, Quarkus will attempt to deploy the application to the target Kubernetes cluster
     */
    @WithDefault("false")
    boolean deploy();

    /**
     * If deploy is enabled, it will follow this strategy to update the resources to the target Kubernetes cluster.
     */
    @WithDefault("CreateOrUpdate")
    DeployStrategy deployStrategy();

    default DeploymentResourceKind getDeploymentResourceKind(Capabilities capabilities) {
        if (deploymentKind().isPresent()) {
            return deploymentKind().filter(k -> k.isAvailalbleOn(KUBERNETES)).get();
        } else if (capabilities.isPresent(Capability.PICOCLI)) {
            return DeploymentResourceKind.Job;
        }
        return DeploymentResourceKind.Deployment;
    }
}
