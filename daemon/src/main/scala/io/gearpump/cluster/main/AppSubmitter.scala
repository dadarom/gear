/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gearpump.cluster.main

import java.io.File
import java.net.{URL, URLClassLoader}
import java.util.jar.JarFile

import io.gearpump.cluster.{UserConfig, Application}
import io.gearpump.cluster.client.ClientContext
import io.gearpump.util.{Util, Constants}
import io.gearpump.util.{AkkaApp, Constants, LogUtil, Util}
import org.slf4j.Logger

import scala.util.Try

object AppSubmitter extends AkkaApp with ArgumentsParser {
  val LOG: Logger = LogUtil.getLogger(getClass)

  override val ignoreUnknownArgument = true

  override val description = "Submit an application to Master by providing a jar"

  override val options: Array[(String, CLIOption[Any])] = Array(
    "name" -> CLIOption[String]("<application name>", required = false, defaultValue = Some("")),
    "jar" -> CLIOption("<application>.jar", required = true),
    "verbose" -> CLIOption("<print verbose log on console>", required = false, defaultValue = Some(false)))

  def main(akkaConf: Config, args: Array[String]): Unit = {

    val config = parse(args)
    if (null == config) {
      return
    }

    val verbose = config.getBoolean("verbose")
    if (verbose) {
      LogUtil.verboseLogToConsole
    }

    val jar = config.getString("jar")

//    // Set jar path to be submitted to cluster
//    System.setProperty(Constants.GEARPUMP_APP_JAR, jar)

    val appName = config.getString("name")
    if (appName.nonEmpty) {
      if (!Util.validApplicationName(appName)) {
        throw new Exception(s"$appName is not a valid prefix for an application name")
      }
      System.setProperty(Constants.GEARPUMP_APP_NAME_PREFIX, "")//ignore name_prefix
    }

    val jarFile = new java.io.File(jar)
    if (!jarFile.exists()) {
      throw new Exception(s"jar $jar does not exist")
    }

    //submit
    val client = ClientContext(akkaConf)
    val appid  = client.submit(Application(appName, UserConfig.empty), jar);
    LOG.info(s"Submit [$appName] with [$jar] in [$appid] to gear cluster.")

    //close
    client.close();
  }

  private def parseMain(jar: File, remainArgs: Array[String], classLoader: ClassLoader): (String, Array[String]) = {
    val mainInManifest = Option(new JarFile(jar).getManifest.getMainAttributes.getValue("Main-Class")).getOrElse("")
    if (remainArgs.length > 0 && Try(classLoader.loadClass(remainArgs(0))).isSuccess) {
      (remainArgs(0), remainArgs.drop(1))
    } else if (mainInManifest.nonEmpty) {
      (mainInManifest, remainArgs)
    } else {
      throw new Exception("No main class specified")
    }
  }
}
