package org.nuxeo.ecm.core.blob.s3.minio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RunnerFeature;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

public class MinioDockerFeature implements RunnerFeature {

	protected DockerClient dockerClient;

	protected String containerID;

	protected String entryPoint;
	protected String accessKey;
	protected String secretKey;

	protected AmazonS3 s3Client;
	protected Bucket bucket;

	@Override
	public void initialize(FeaturesRunner runner) throws Exception {
		initDocker();
	}

	@Override
	public void stop(FeaturesRunner runner) throws Exception {
		cleanup();
	}

	protected void initDocker() throws Exception {

		dockerClient = DefaultDockerClient.fromEnv().build();

		List<PortBinding> hostPorts = new ArrayList<>();
		hostPorts.add(PortBinding.create("0.0.0,0", "9000"));

		Map<String, List<PortBinding>> portBindings = new HashMap<>();
		portBindings.put("9000", hostPorts);

		HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

		ContainerConfig containerConfig = ContainerConfig.builder().hostConfig(hostConfig).image("minio/minio")
				.tty(true).attachStdout(true).cmd(new String[] { "server", "/data" }).build();

		ContainerCreation creation = dockerClient.createContainer(containerConfig);
		containerID = creation.id();
		dockerClient.startContainer(containerID);

		System.out.println("Started container: " + containerID);

		StringBuffer logs = new StringBuffer();

		while (!logs.toString().contains("Endpoint:") && !logs.toString().contains("Browser Access:"))
			try (LogStream stream = dockerClient.logs(containerID, LogsParam.stdout(), LogsParam.stderr())) {
				logs.append(stream.readFully());
				Thread.sleep(250);
			}
		paserLog(logs.toString());

		initBucket();

		System.getProperties().put("minio.accessKey", accessKey);
		System.getProperties().put("minio.secretKey", secretKey);
		System.getProperties().put("minio.url", entryPoint);
		System.getProperties().put("minio.bucket", getBucketName());
	}

	protected String getBucketName() {
		return "test";
	}

	protected void initBucket() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setSignerOverride("AWSS3V4SignerType");
		s3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration(entryPoint, Regions.US_EAST_1.name()))
				.withPathStyleAccessEnabled(true).withClientConfiguration(clientConfiguration)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		bucket = s3Client.createBucket(getBucketName());
	}

	protected void paserLog(String log) {
		String[] lines = log.split("\\n");
		for (String line : lines) {
			if (line.contains("Endpoint:")) {
				entryPoint = line.split(" ")[2];
				System.out.println("Endpoint:" + entryPoint);
			} else if (line.contains("AccessKey:")) {
				accessKey = line.split(" ")[1].substring(8);
				System.out.println("AccessKey:" + accessKey);
			} else if (line.contains("SecretKey:")) {
				secretKey = line.split(" ")[1].substring(8);
				System.out.println("SecretKey:" + secretKey);
			}
		}
	}

	protected void cleanup() throws Exception {
		System.out.print("Shuting down Minio");
		if (dockerClient != null) {
			if (containerID != null) {
				dockerClient.killContainer(containerID);
				dockerClient.removeContainer(containerID);
			}
			dockerClient.close();
		}
	}

}
