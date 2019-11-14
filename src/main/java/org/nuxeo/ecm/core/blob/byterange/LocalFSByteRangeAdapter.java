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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.core.blob.binary.DefaultBinaryManager;

public class LocalFSByteRangeAdapter implements ByteRangeAdapter {

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

	@Override
	public Blob getBlobWithByteRange(BlobProvider provider, Blob blob, long offset, long length) {

		DefaultBinaryManager binaryManager = (DefaultBinaryManager) provider.getBinaryManager();
		
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

}
