package sample

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Executors, ThreadFactory, TimeUnit}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.{Failure, Random, Success}
import ExecutionContext.Implicits.global


object Main {
  def main(args: Array[String]): Unit = {
    val fileNum = 2
    val maxUploadDuration = Duration(fileNum * 2, TimeUnit.MINUTES)
    val uploader = new S3Uploader(AWSS3Config.load())

    println("Generating big files ...")
    val files = for (i <- 0 until fileNum) yield
      generateBigFile(megabytes = 1)

    println("PRESS ENTER TO UPLOAD ...")
    StdIn.readLine()

    val periodicPing = startLiveCheck()

    println("Uploading ...")
    val fut = uploader.uploadFiles(files)
    fut onComplete {
      case Success(res) =>
        println("Successfully uploaded. [Result:] " + res)
      case Failure(th) =>
        println("An error has occured: " + th.getMessage)
    }

    Await.result(fut, maxUploadDuration)

    // clean up and shut down
    periodicPing.cancel(false)
    files.foreach(_.delete())
    uploader.shutdown()
    System.out.println("SHUTTING DOWN!")
  }

  /**
   * Scheduler used for periodic pings, to check for live-ness.
   */
  val scheduler = Executors.newScheduledThreadPool(2,
    new ThreadFactory {
      private[this] val count = new AtomicInteger(0)
      def newThread(r: Runnable) = {
        val th = new Thread(r)
        th.setDaemon(true)
        th.setName(s"my-executor-${count.getAndIncrement}")
        th
      }
    })

  /**
   * A periodic task whose purpose is to check if our thread-pool
   * can process other stuff - demonstrating that uploads are doing
   * blocking I/O.
   */
  def startLiveCheck() = {
    val runnable = new Runnable {
      var count = 0
      def run() = {
        count += 1
        println(s"$count - ping")
        System.out.flush()
      }
    }
    
    scheduler.scheduleAtFixedRate(
      runnable, 0, 1, TimeUnit.SECONDS)
  }

  /**
   * Generates big test file.
   */
  def generateBigFile(megabytes: Int) = {
    val kilobytes = megabytes.toLong * 1024
    val buffer = new Array[Byte](1024)
    val file = File.createTempFile("test", ".dat")
    val out = new BufferedOutputStream(new FileOutputStream(file))

    try {
      var idx = 0L
      while (idx < kilobytes) {
        Random.nextBytes(buffer)
        out.write(buffer)
        idx += 1
      }
      file
    }
    finally {
      out.close()
    }
  }
}