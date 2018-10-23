# aws-upload-sample

Demonstrates usage of AWS SDK's TransferManager for handling S3 uploads.
Also demostrates how Scala Futures simplify the reasoning of concurrency.

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
