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

package org.nuxeo.ecm.core.blob.byterange;

import java.io.IOException;
import java.io.InputStream;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.core.storage.sql.S3BinaryManager;

import com.amazonaws.services.s3.model.GetObjectRequest;

public class S3ByteRangeAdapter implements ByteRangeAdapter {

	@Override
	public Blob getBlobWithByteRange(BlobProvider provider, Blob blob, long offset, long length) {

		final S3BinaryManager s3Provider = (S3BinaryManager) provider;
		
		// hack to get the bucketname
		String gcid = s3Provider.getGarbageCollector().getId();
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
				return s3Provider.getAmazonS3().getObject(request).getObjectContent();
			}
		};

	}
}
