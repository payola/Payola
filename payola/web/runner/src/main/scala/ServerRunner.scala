package cz.payola.web.runner

import java.util.Calendar

/**
 * Running this object will drop the existing database, create a new one and fill it with the initial data.
 */
object ServerRunner extends App
{
    val restartHour = 4

    private var currentServerProcess: Option[Process] = None

    while (true) {
        currentServerProcess.foreach { p =>
            print("Killing the Payola Web Server ... ")
            p.destroy()
            println("OK")
        }

        print("Starting the Payola Web Server ... ")
        currentServerProcess = Some(Runtime.getRuntime.exec("""sbt.bat "project server" "run"""))
        println("OK")
        println("The Payola Web Server is running ... ")

        // Wait for the restart.
        var hours = (getCurrentHour, getCurrentHour)
        while (!(hours._1 < restartHour && hours._2 == restartHour)) {
            Thread.sleep(1000 * 60) // Sleep for a minute.
            hours = (hours._2, getCurrentHour)
        }

        println("Restarting the Payola Web Server ... ")
    }

    private def getCurrentHour = Calendar.getInstance.get(Calendar.HOUR_OF_DAY)
}

