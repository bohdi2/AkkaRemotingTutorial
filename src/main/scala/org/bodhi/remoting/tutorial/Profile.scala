package org.bodhi.remoting.tutorial

import com.typesafe.config.{ConfigResolveOptions, ConfigParseOptions, ConfigFactory, Config}


object Profile {
  
  def load(name: String): Config = {
    val conf = ConfigFactory.load("common")
    val profiles = conf.withOnlyPath("profiles")

    val profile = profiles.getString("profiles.profile")

    val pc = profiles.getConfig("profiles." + profile)

    ConfigFactory.load(name,
                       ConfigParseOptions.defaults(),
                       ConfigResolveOptions.defaults().setAllowUnresolved(true)).resolveWith(pc)
  }
}
