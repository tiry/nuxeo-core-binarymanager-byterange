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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolderWithProperties;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;
import org.nuxeo.ecm.core.blob.binary.DefaultBinaryManager;
import org.nuxeo.ecm.core.storage.sql.S3BinaryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

import com.amazonaws.services.s3.model.GetObjectRequest;

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
			return getBlobWithByteRangeFromS3((S3BinaryManager) provider, blob, offset, length);
		} else if (provider.getBinaryManager() instanceof DefaultBinaryManager) {
			return getBlobWithByteRangeFromFS((DefaultBinaryManager) provider.getBinaryManager(), blob, offset, length);
		}

		return null;
	}

	protected Blob getBlobWithByteRangeFromS3(S3BinaryManager provider, Blob blob, long offset, long length) {

		// hack to get the bucketname
		String gcid = provider.getGarbageCollector().getId();
		String bucketName = gcid.split(":")[1];

		// Build a byterange request
		GetObjectRequest request = new GetObjectRequest(bucketName, blob.getDigest());
		request.setRange(offset, offset + length-1);

		return new AbstractBlob() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getEncoding() {
				return blob.getEncoding();
			}

			@Override
			public String getFilename() {
				return blob.getFilename();
			}

			@Override
			public InputStream getStream() throws IOException {
				return provider.getAmazonS3().getObject(request).getObjectContent();
			}
		};

	}

	protected static class ByteRangeInputStream extends FilterInputStream {

		protected long remaining;

		protected ByteRangeInputStream(InputStream in, long offset, long length) throws IOException {
			super(in);
			this.in.skip(offset);
			remaining = length;
		}

		@Override
		public int read() throws IOException {
			return --remaining >= 0 ? in.read() : -1;
		}
		
		@Override
		public int read(byte b[]) throws IOException {
		    return read(b, 0, b.length);
		}

		@Override
		public int read(byte b[], int off, int len) throws IOException {
	        if (b == null) {
	            throw new NullPointerException();
	        } else if (off < 0 || len < 0 || len > b.length - off) {
	            throw new IndexOutOfBoundsException();
	        } else if (len == 0) {
	            return 0;
	        }

	        int c = read();
	        if (c == -1) {
	            return -1;
	        }
	        b[off] = (byte)c;

	        int i = 1;
	        try {
	            for (; i < len ; i++) {
	                c = read();
	                if (c == -1) {
	                    break;
	                }
	                b[off + i] = (byte)c;
	            }
	        } catch (IOException ee) {
	        }
	        return i;
		}
	}

	protected Blob getBlobWithByteRangeFromFS(DefaultBinaryManager binaryManager, Blob blob, long offset, long length) {

		return new AbstractBlob() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getEncoding() {
				return blob.getEncoding();
			}

			@Override
			public String getFilename() {
				return blob.getFilename();
			}

			@Override
			public long getLength() {		
				return length;
			}

			@Override
			public InputStream getStream() throws IOException {
				InputStream input = binaryManager.getBinary(blob).getStream();
				return new ByteRangeInputStream(input, offset, length);
			}
		};

	}

	@Override
	public BlobHolder getBlobHolderWithByteRange(Blob blob, long offset, long length) {
		Map<String, Serializable> propertyMap = new HashMap<String, Serializable>();
		propertyMap.put("offset", offset);
		propertyMap.put("length", length);
		return new SimpleBlobHolderWithProperties(getBlobWithByteRange(blob, offset, length), propertyMap);
	}

}
