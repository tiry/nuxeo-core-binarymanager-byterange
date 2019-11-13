# Blob ByteRange Manager

## Goal

Provide a service interface to access Nuxeo Blob on a given byte-range independently of the underlying backend implementation.

This service is supposed to be a temporary soluton until we reintegrate this directlt inside the `BlobManager` or `BinaryManager` interface.

## Build and usage

### Build + Tests

	mvn clean install

To be able to run the test with S3, you need to provide a working S3 bucket.

You can either:

 - provision a real S3 bucket on AWS
 - use [Minio](https://github.com/minio/minio)

In both cases, you will need to update the file `test-minioblobstore-contrib.xml` accordingly (see [doc](https://doc.nuxeo.com/nxdoc/amazon-s3-online-storage/))

To run Minio locally:

    docker pull minio/minio

    docker run -it -p 9000:9000 minio/minio server /data

The `-it` should allow to get the dynamically generated Key/Secret

    Endpoint:  http://172.18.0.2:9000  http://127.0.0.1:9000
    AccessKey: 57HHE9NCXNLBOBKSPEXU 
    SecretKey: sMSXC8x+++RD+H6M72fhANPFNr+BWbsOZa07naVe 

Update the `endpoint`, `awsid` & `awssecret` entries of the `test-minioblobstore-contrib.xml` accordingly.

NB: you also need to create an empty bucket though the UI (here the expected name is "test").


### Deploying

You first need to deploy the plugin inside your Nuxeo server.

	cp target/nuxeo-core-binarymanager-byterange-X.Y-Z.jar into nxserver/bundles

### Using

The `BlobByteRangeManager` is exposed via a  Nuxeo Runtime service, so it can be accessed via:

    import org.nuxeo.runtime.api.Framework;     
     
    Blob byteRangedBlob = Framework.getService(BlobByteRangeManager.class).getBlobWithByteRange(srcBlob, offset, length);

A Helper class is also provided in order to avoid direct dependency on Nuxeo runtime:


    import org.nuxeo.ecm.core.blob.BBRHelper;
     
    Blob byteRangedBlob = BBRHelper.getService().getBlobWithByteRange(srcBlob, offset, length)

## WIP

 - [x] Initial service
 - [x] S3 and LocalFS implementation
 - [x] S3 unit tests 
 - [ ] Automate MiniIO deployment and configuration for tests
 - [ ] Make service pluggable to contribue providers from the outside

