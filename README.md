# aws-upload-sample

Demonstrates usage of AWS SDK's [TransferManager](docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/transfer/TransferManager.html) for handling S3 uploads.
Also demonstrates that Scala Future makes concurrent programming easy in reasoning.

## Configuration

Configuration lives in [application.conf](src/main/resources/application.conf) and
must be configured with access keys:

```javascript
aws.s3 {
  accessKey = "changeMe"
  secretAccessKey = "changeMe"
  bucketName = "someBucket"
  region = "ap-southeast-2"
}
```
