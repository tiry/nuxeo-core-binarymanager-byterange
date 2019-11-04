package org.nuxeo.ecm.core.blob;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;

public interface BlobByteRangeManager {

	Blob getBlobWithByteRange(Blob blob, long offset, long length);
	
	BlobHolder getBlobHolderWithByteRange(Blob blob, long offset, long length);

}
