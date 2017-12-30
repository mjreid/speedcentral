package com.speedcentral.lmp

import java.io.InputStream

import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class LmpAnalyzerTest extends FlatSpec with Matchers with BeforeAndAfterAll {

  private var classicDoom2Demo: Array[Byte] = _
  private var classicBoomDemo: Array[Byte] = _
  private var prboomPlusDemo: Array[Byte] = _
  private var multiWadPrboomDemo: Array[Byte] = _
  private var lmpAnalyzer: LmpAnalyzer = _

  override def beforeAll(): Unit = {
    val classLoader = classOf[LmpAnalyzerTest].getClassLoader
    classicDoom2Demo = streamToBytes(classLoader.getResourceAsStream("classic-doom2-demo.LMP"))
    classicBoomDemo = streamToBytes(classLoader.getResourceAsStream("classic-boom-demo.LMP"))
    prboomPlusDemo = streamToBytes(classLoader.getResourceAsStream("prboom-plus-demo.lmp"))
    multiWadPrboomDemo = streamToBytes(classLoader.getResourceAsStream("multiwad-test.lmp"))
    lmpAnalyzer = new LmpAnalyzer
  }

  "analyze" should "parse a vanilla Doom2 demo properly" in {
    val maybeResult = lmpAnalyzer.analyze(classicDoom2Demo)
    maybeResult.isSuccess should be(true)
    val result = maybeResult.get
    result.skillLevel should be(Some(3))
    result.map should be(Some(1))
    result.episode should be(Some(1))
    result.engineVersion should be(Some("doom2"))
    result.iwad should be(None)
    result.pwads should be(Seq.empty)
  }

  it should "fail to parse a Boom demo" in {
    val maybeResult = lmpAnalyzer.analyze(classicBoomDemo)
    maybeResult.isFailure should be(true)
  }

  it should "parse basic Prboom-Plus demo information properly" in {
    val maybeResult = lmpAnalyzer.analyze(prboomPlusDemo)
    maybeResult.isSuccess should be(true)
    val result = maybeResult.get
    result.skillLevel should be(Some(3))
    result.map should be(Some(2))
    result.episode should be(Some(1))
  }

  it should "parse Prboom-Plus engine information properly" in {
    val maybeResult = lmpAnalyzer.analyze(prboomPlusDemo)
    maybeResult.isSuccess should be(true)
    val result = maybeResult.get
    result.engineVersion should be(Some("prboom-plus"))
  }

  it should "parse Prboom-Plus wad information properly" in {
    val maybeResult = lmpAnalyzer.analyze(prboomPlusDemo)
    maybeResult.isSuccess should be(true)
    val result = maybeResult.get

    result.iwad should be (Some("doom2"))
    result.pwads should be(Seq("prwrcol2"))
  }

  it should "parse Prboom-Plus wad information properly when there are multiple wads" in {
    val maybeResult = lmpAnalyzer.analyze(multiWadPrboomDemo)
    maybeResult.isSuccess should be(true)
    val result = maybeResult.get

    result.pwads should be(Seq("mm2", "mm2mus"))
  }

  private def streamToBytes(is: InputStream): Array[Byte] = {
    if (is == null) throw new RuntimeException("Input stream was null!")
    Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
  }
}
