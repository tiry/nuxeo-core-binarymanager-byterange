package org.nuxeo.ecm.core.blob.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.blob.BlobByteRangeManager;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.core.storage.sql.S3BinaryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(CoreFeatureMinioS3BlobStore.class)
public class TestBlobByteRangeManagerS3 {

    @Inject
    protected BlobByteRangeManager bbrm;

    @Inject
    protected CoreSession session;
    
    @Test
    public void testServiceDeployed() {
        assertNotNull(bbrm);
    }

    @Test
    public void testS3Deployed() {
        
		BlobManager bm = Framework.getService(BlobManager.class);

		Map<String, BlobProvider> providers = bm.getBlobProviders();
		
		assertEquals(1, providers.size());		
				
		assertEquals("test", providers.entrySet().iterator().next().getKey());

		BlobProvider provider = providers.entrySet().iterator().next().getValue();
		assertEquals(S3BinaryManager.class.getName(), provider.getClass().getName());

    }
        
    
    
    
    @Test
    public void testFSBlob() throws Exception {

    	
    	
    	
    	Blob blob = new StringBlob("0123456789");
    	blob.setFilename("whatever.txt");
    	
    	DocumentModel doc = session.createDocumentModel("/", "Tst", "File");     	
    	doc.setPropertyValue("file:content", (Serializable) blob);    	
    	doc = session.createDocument(doc);
    	
    	blob = (Blob) doc.getPropertyValue("file:content");
    	
    	Blob rangedBlob = bbrm.getBlobWithByteRange(blob, 2, 2);    	
    	String str = rangedBlob.getString();
    	assertEquals("23", str);
    	
    	rangedBlob = bbrm.getBlobWithByteRange(blob, 2, 3);    	
    	str = rangedBlob.getString();
    	assertEquals("234", str);
    	    	

    }

}
