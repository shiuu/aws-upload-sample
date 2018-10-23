package sample

import java.io.File

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.transfer.{TransferManagerBuilder, Upload}
import com.amazonaws.services.s3.transfer.model.UploadResult

import scala.concurrent.{ExecutionContext, Future, blocking}
import ExecutionContext.Implicits.global

final class S3Uploader(config: AWSS3Config) {
  val credentials = new BasicAWSCredentials(
    config.accessKey,
    config.secretAccessKey
  )
  val s3Client = AmazonS3ClientBuilder
    .standard()
    .withCredentials(new AWSStaticCredentialsProvider(credentials))
    .withRegion(config.region.getFirstRegionId)
    .build()
  val transferManager = TransferManagerBuilder
    .standard()
    .withS3Client(s3Client)
    .build()

  def upload(key: String, file: File): Future[UploadResult] = {
//    println(s"uploading ${file.getName}")
    Future {
      val request: Upload = transferManager.upload(config.bucketName, key, file)
      blocking {
        request.waitForUploadResult()
      }
    }
  }

  def uploadFiles(files: Seq[File]): Future[Seq[UploadResult]] = {
    val results = files.map { f =>
      upload(s"test/${f.getName}", f)
    }
    Future.sequence(results)
  }

  def shutdown(): Unit = {
    transferManager.shutdownNow(true)
  }
}
