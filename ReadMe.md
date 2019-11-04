# Blob ByteRange Manager

## Goal

Provide a service interface to access Nuxeo Blob on a given byte-range independently of the underlying backend implementation.

This service is supposed to be a temporary soluton until we reintegrate this directlt inside the `BlobManager` or `BinaryManager` interface.

## Build and usage

### Build + Tests

	mvn clean install

### Deploying

You first need to deploy the plugin inside your Nuxeo server.

	cp target/nuxeo-core-binarymanager-byterange-X.Y-Z.jar into nxserver/bundles

## WIP

 - [x] Initial service
 - [x] S3 and LocalFS implementation
 - [ ] S3 unit tests 
 - [ ] Make service pluggable to contribue providers from the outside

