package org.nuxeo.ecm.core.blob.s3.minio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.runtime.test.runner.RunnerFeature;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

public class MinioDockerFeature implements RunnerFeature {

	
    public void initDocker() throws Exception {
        
    	final DockerClient docker = DefaultDockerClient.fromEnv().build();

    	List<PortBinding> hostPorts = new ArrayList<>();
    	hostPorts.add(PortBinding.create("0.0.0,0", "9000"));
    	
    	Map<String, List<PortBinding>> portBindings = new HashMap<>();
    	portBindings.put("9000", hostPorts);
    	
    	HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

    	ContainerConfig containerConfig = ContainerConfig.builder()
    		.hostConfig(hostConfig)
    		.image("minio/minio")
    		.cmd(new String[] {"server","/data"})
        	.build();
    	
    	final ContainerCreation creation = docker.createContainer(containerConfig);
    	final String id = creation.id();
    	
    	System.out.println(id);
    	
    	
    }

}
