package org.bodhi.remoting.tutorial

import com.typesafe.config.{ConfigFactory}


object Profile {
  
  implicit class StringLoader(val s: String) {
    def loadConfig = {
      val profileName = scala.util.Properties.envOrElse("profileName", "loopback")
      val profileConfig = ConfigFactory.load("local")

      ConfigFactory.parseString(s).withFallback(profileConfig).resolve
    }
 
  }
  
}
