package com.speedcentral.hm.server.util

import java.nio.file.{Files, Path}

import com.speedcentral.hm.server.config.HmConfigException

object PathUtil {
  def validateDirectory(directoryStr: String, directory: Path, checkWritable: Boolean = false): Unit = {
    if (!Files.exists(directory)) {
      throw new HmConfigException(s"$directoryStr did not exist")
    } else if (!Files.isReadable(directory)) {
      throw new HmConfigException(s"$directoryStr can't be read")
    } else if (!Files.isDirectory(directory)) {
      throw new HmConfigException(s"$directoryStr was not a directory")
    }

    if (checkWritable && !Files.isWritable(directory)) {
      throw new HmConfigException(s"$directoryStr was not writable")
    }
  }

  def validateFile(fileStr: String, file: Path): Unit = {
    if (!Files.exists(file)) {
      throw new HmConfigException(s"$fileStr did not exist")
    } else if (!Files.isReadable(file)) {
      throw new HmConfigException(s"$fileStr can't be read")
    } else if (!Files.isRegularFile(file)) {
      throw new HmConfigException(s"$fileStr was not a regular file")
    }
  }
}
