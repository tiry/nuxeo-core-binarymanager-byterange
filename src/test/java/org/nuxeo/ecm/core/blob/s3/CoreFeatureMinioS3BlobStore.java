package org.nuxeo.ecm.core.blob.s3;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.StorageConfiguration;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.RuntimeFeature;
import org.nuxeo.runtime.test.runner.RuntimeHarness;
import org.osgi.framework.Bundle;;

@Deploy("org.nuxeo.ecm.core.nuxeo-core-binarymanager-byterange")
public class CoreFeatureMinioS3BlobStore extends CoreFeature {

	protected class MiniOS3StorageConfiguration extends StorageConfiguration {

		public MiniOS3StorageConfiguration(CoreFeature feature) {
			super(feature);
		}

		@Override
		public URL getBlobManagerContrib(FeaturesRunner runner) {
			String bundleName = "org.nuxeo.ecm.core.nuxeo-core-binarymanager-byterange";
			String contribPath = "test-minioblobstore-contrib.xml";			                      
			RuntimeHarness harness = runner.getFeature(RuntimeFeature.class).getHarness();
			Bundle bundle = harness.getOSGiAdapter().getRegistry().getBundle(bundleName);
			URL contribURL = bundle.getEntry(contribPath);
			assertNotNull("deployment contrib " + contribPath + " not found", contribURL);
			return contribURL;
		}
	}

    @Override
    public void start(FeaturesRunner runner) {
    	this.storageConfiguration = getStorageConfiguration();
    	super.start(runner);
    }

	@Override
	public StorageConfiguration getStorageConfiguration() {
		return new MiniOS3StorageConfiguration(this);
	}

}
