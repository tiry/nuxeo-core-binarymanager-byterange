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

import org.nuxeo.runtime.api.Framework;

/**
 * Static helper to avoid Nuxeo Runtime dependency on the caller side
 * 
 * @author tiry
 *
 */
public class BBRHelper {

	public static BlobByteRangeManager getService() {
		return Framework.getService(BlobByteRangeManager.class);
	}

}
