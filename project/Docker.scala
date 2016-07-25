import sbt._
import sbt.Keys._
import scala.language.postfixOps

/*
 * Manages the Docker image
 */
object docker {
  lazy val dockerImage = settingKey[DockerImage]("docker-image")
  lazy val dockerTag = settingKey[String]("docker-tag")
  lazy val dockerMount = settingKey[String]("docker-mount")
  lazy val dockerImageName = settingKey[String]("docker-image-name")

  lazy val settings = Seq(
    dockerTag in ThisBuild := "latest",
    dockerMount in ThisBuild := "/var/demo",
    dockerImageName in ThisBuild := "ranjandas/docker-cdh5-pseudo", // Use an available image

    dockerImage in ThisBuild := new DockerImage(
      dockerImageName.value,
      dockerTag.value,
      dockerMount.value,
      baseDirectory.value
    )
  )

  /**
   * testSettings to invoke as "it:run"
   */
  def testSettings(): Seq[Setting[_]] = {
    // override the run settings, run command on sbt will invoke the tests
    run := {
      val dockerImg = docker.dockerImage.value

      log(s"System Test Using docker is about to start...")
      val containerId = dockerImg.startContainer().substring(0, 12)
      success(s"Container kicked off with id ${containerId}")

      if(dockerImg.checkIfContainerReady(containerId)) {
        info("Will start the Job now")
        val result = dockerImg.runJob(containerId)
        info(s"return code = $result")
      } else {
        error(s"Container not started or something went wrong, cannot proceed")
        System.exit(-1)
      }
      dockerImg.stopContainer(containerId)
    }
  }

  class DockerImage(dockerImageName: String, dockerTag: String, mountDir: String, baseDir: File) {
    val localDir = (baseDir)

    def startContainer(): String = {
      Process(
        "docker" :: "run" :: "-d" ::
          "-p" :: "8020:8020" ::
          "-p" :: "8021:8021" ::
          "-p" :: "8022:8022" ::
          "-p" :: "8023:8023" ::
          "-p" :: "9083:9083" ::
          "-p" :: "10000:10000" ::
          "-p" :: "50010:50010" ::
          "-p" :: "50020:50020" ::
          "-p" :: "50030:50030" ::
          "-p" :: "50060:50060" ::
          "-p" :: "50070:50070" ::
          "-p" :: "50075:50075" ::
          "-p" :: "50090:50090" ::
          "-p" :: "50111:50111" ::
          "-p" :: "50475:50475" ::
          "-v" :: s"${localDir}:${mountDir}" :: s"${dockerImageName}:${dockerTag}" :: Nil
      ) !!
    }

    def runJob(containerId: String) = {
      Process ("docker" :: "exec" :: "-t" ::  s"$containerId"  :: "/bin/bash" :: "/var/demo/src/main/scripts/run.sh" :: Nil)!
    }

    def stopContainer(containerId: String): Unit = {
      info("Stopping the container...")
      Process("docker" :: "stop" :: s"$containerId" :: Nil)!
    }

    def checkIfContainerReady(containerId: String): Boolean = {
      val status =
        Process("docker" :: "exec" :: "-t" :: s"$containerId" :: "/bin/bash" :: "/var/demo/src/main/scripts/checkContainer.sh" :: Nil)!

      if (status != 0) {
        stopContainer(containerId)
        false
      }
      else
        true
    }
  }

  /** Not a nice place to keep these functions, this is example project */
  def log(message: String) = println(s"=========== ${message}")

  def info(message: String): Unit = {
    log(s"INFO :=> ${message}")
  }

  def error(message: String): Unit = {
    log(s"ERROR :=> {message}")
  }

  def success(message: String): Unit = {
    log(s"SUCCESS :=> ${message}")
  }
}