package com.jeferson.api_crud_biblioteca.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import org.testcontainers.dockerclient.DockerClientProviderStrategy;
import org.testcontainers.dockerclient.InvalidConfigurationException;
import org.testcontainers.dockerclient.TransportConfig;

import java.net.URI;

/**
 * Testcontainers strategy for Docker Desktop on Windows (Docker Desktop 4.70+).
 * Connects directly to docker_engine_linux named pipe (the real WSL2 Docker engine),
 * bypassing the Docker Desktop proxy that returns stubs to unauthorized clients.
 * Forces API version 1.40 which is required by Docker 29.x
 * (the zerodep transport defaults to 1.32 which is rejected).
 */
public class WindowsDockerClientStrategy extends DockerClientProviderStrategy {

    private static final String DOCKER_PIPE = "npipe:////./pipe/docker_engine_linux";
    private static volatile DockerClient cachedClient;

    @Override
    public TransportConfig getTransportConfig() throws InvalidConfigurationException {
        return TransportConfig.builder()
                .dockerHost(URI.create(DOCKER_PIPE))
                .build();
    }

    @Override
    public DockerClient getClient() {
        if (cachedClient == null) {
            synchronized (WindowsDockerClientStrategy.class) {
                if (cachedClient == null) {
                    cachedClient = createDockerClient();
                }
            }
        }
        return cachedClient;
    }

    @Override
    public DockerClient getDockerClient() {
        return getClient();
    }

    private DockerClient createDockerClient() {
        URI dockerHost = URI.create(DOCKER_PIPE);
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(DOCKER_PIPE)
                .withApiVersion(RemoteApiVersion.VERSION_1_40)
                .build();
        ZerodepDockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(dockerHost)
                .build();
        return DockerClientImpl.getInstance(config, httpClient);
    }

    @Override
    public String getDescription() {
        return "Docker Desktop Windows - docker_engine_linux pipe with API 1.40";
    }

    @Override
    protected boolean isApplicable() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    @Override
    protected int getPriority() {
        return 10;
    }
}
