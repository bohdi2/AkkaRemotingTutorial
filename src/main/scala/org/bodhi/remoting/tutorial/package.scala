package org.bodhi.remoting

import com.typesafe.config.ConfigFactory

package object tutorial {
  case object Start
  case object Nag

  implicit class StringLoader(val s: String) {
    def loadConfig = {
      val profileName = System.getProperties.getProperty("profileName", "loopback")

      val profileConfig = ConfigFactory.parseResources(s"$profileName.conf")
      ConfigFactory.parseString(s).withFallback(profileConfig).resolve
    }

  }
}
