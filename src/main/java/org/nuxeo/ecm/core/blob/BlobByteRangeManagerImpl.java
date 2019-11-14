/*
 * (C) Copyright 2006-2019 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Tiry
 */
package org.nuxeo.ecm.core.blob;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolderWithProperties;
import org.nuxeo.ecm.core.blob.binary.DefaultBinaryManager;
import org.nuxeo.ecm.core.blob.byterange.LocalFSByteRangeAdapter;
import org.nuxeo.ecm.core.blob.byterange.S3ByteRangeAdapter;
import org.nuxeo.ecm.core.storage.sql.S3BinaryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

/** 
 * Nuxeo Runtime component providing a pluggable implementation for the {@link BlobByteRangeManager} interface.
 * 
 * The idea is that this component hides the implementation details that are specific to each {@link BlobProvider}.
 * 
 * @author tiry
 *
 */
public class BlobByteRangeManagerImpl extends DefaultComponent implements BlobByteRangeManager {

	@Override
	public Blob getBlobWithByteRange(Blob blob, long offset, long length) {

		BlobManager bm = Framework.getService(BlobManager.class);
		BlobProvider provider = bm.getBlobProvider(blob);
		
		if (provider instanceof S3BinaryManager) {
			return new S3ByteRangeAdapter().getBlobWithByteRange(provider, blob, offset, length);
		} else if (provider.getBinaryManager() instanceof DefaultBinaryManager) {
			return new LocalFSByteRangeAdapter().getBlobWithByteRange(provider, blob, offset, length);
		}

		return null;
	}
	
	@Override
	public BlobHolder getBlobHolderWithByteRange(Blob blob, long offset, long length) {
		Map<String, Serializable> propertyMap = new HashMap<String, Serializable>();
		propertyMap.put("offset", offset);
		propertyMap.put("length", length);
		return new SimpleBlobHolderWithProperties(getBlobWithByteRange(blob, offset, length), propertyMap);
	}

}
