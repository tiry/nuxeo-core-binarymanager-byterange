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

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;

/*
 * Service interface to give access to a byte-range of a given blob 
 * while abstracting the underlying BlobManager implementation		
 */
public interface BlobByteRangeManager {

	/**
	 * Return a sub {@link Blob} for the given input {@link Blob} and byte-range
	 * 
	 * @param blob the Blob property as retrieved from the {@link DocumentModel}
	 * @param offset the starting offset of the byte-range
	 * @param length the length of the byte-range
	 * @return a new Blob giving access to only the underlying byte-range
	 */
	Blob getBlobWithByteRange(Blob blob, long offset, long length);

	/**
	 * Return a {@link BlobHolder} wrapping a sub {@link Blob} for the given input {@link Blob} and byte-range
	 * 
	 * @param blob the Blob property as retrieved from the {@link DocumentModel}
	 * @param offset the starting offset of the byte-range
	 * @param length the length of the byte-range
	 * @return a BlobHolder wrapping a new Blob giving access to only the underlying byte-range
	 */
	BlobHolder getBlobHolderWithByteRange(Blob blob, long offset, long length);

}
