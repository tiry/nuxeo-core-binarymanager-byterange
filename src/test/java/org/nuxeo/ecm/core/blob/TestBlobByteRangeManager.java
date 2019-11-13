package org.nuxeo.ecm.core.blob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class })
@Deploy("org.nuxeo.ecm.core.nuxeo-core-binarymanager-byterange")
public class TestBlobByteRangeManager {

	@Inject
	protected BlobByteRangeManager bbrm;

	@Inject
	protected CoreSession session;

	@Test
	public void testServiceDeployed() {
		assertNotNull(bbrm);
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
