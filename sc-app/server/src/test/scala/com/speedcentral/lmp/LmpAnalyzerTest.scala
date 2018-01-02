package com.speedcentral.lmp

import java.io.InputStream

import com.speedcentral.api.Pwad
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.ExecutionContext.Implicits.global

class LmpAnalyzerTest extends FlatSpec with Matchers with ScalaFutures with BeforeAndAfterAll {

  private var classicDoom2Demo: Array[Byte] = _
  private var classicBoomDemo: Array[Byte] = _
  private var prboomPlusDemo: Array[Byte] = _
  private var multiWadPrboomDemo: Array[Byte] = _
  private var lmpAnalyzer: LmpAnalyzer = _

  implicit val patience: PatienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  override def beforeAll(): Unit = {
    val classLoader = classOf[LmpAnalyzerTest].getClassLoader
    classicDoom2Demo = streamToBytes(classLoader.getResourceAsStream("classic-doom2-demo.LMP"))
    classicBoomDemo = streamToBytes(classLoader.getResourceAsStream("classic-boom-demo.LMP"))
    prboomPlusDemo = streamToBytes(classLoader.getResourceAsStream("prboom-plus-demo.lmp"))
    multiWadPrboomDemo = streamToBytes(classLoader.getResourceAsStream("multiwad-test.lmp"))
    val pwadAnalyzer = new PwadAnalyzer("http://www.gamers.org/pub/idgames")
    lmpAnalyzer = new LmpAnalyzer(pwadAnalyzer)
  }

  "analyze" should "parse a vanilla Doom2 demo properly" in {
    val resultF = lmpAnalyzer.analyze(classicDoom2Demo)
    whenReady(resultF) { result =>
      result.skillLevel should be(Some(3))
      result.map should be(Some(1))
      result.episode should be(Some(1))
      result.engineVersion should be(Some("doom2"))
      result.iwad should be("doom2")
      result.primaryPwad should be(None)
      result.secondaryPwads should be(Seq.empty)
    }
  }

  it should "fail to parse a Boom demo" in {
    val resultF = lmpAnalyzer.analyze(classicBoomDemo)
    whenReady(resultF.failed) { res =>

    }
  }

  it should "parse basic Prboom-Plus demo information properly" in {
    val resultF = lmpAnalyzer.analyze(prboomPlusDemo)
    whenReady(resultF) { result =>
      result.skillLevel should be(Some(3))
      result.map should be(Some(2))
      result.episode should be(Some(1))
    }
  }

  it should "parse Prboom-Plus engine information properly" in {
    val resultF = lmpAnalyzer.analyze(prboomPlusDemo)
    whenReady(resultF) { result =>
      result.engineVersion should be(Some("prboom-plus"))
    }
  }

  it should "parse Prboom-Plus wad information properly" in {
    val resultF = lmpAnalyzer.analyze(prboomPlusDemo)
    whenReady(resultF) { result =>
      result.iwad should be ("doom2")
      result.primaryPwad should be(Some(Pwad("prwrcol2", "/levels/doom2/p-r/prwrcol2.zip")))
    }
  }

  it should "parse Prboom-Plus wad information properly when there are multiple wads" in {
    val resultF = lmpAnalyzer.analyze(multiWadPrboomDemo)
    whenReady(resultF) { result =>
      result.primaryPwad should be(Some(Pwad("mm2", "/themes/mm/mm2.zip")))
      result.secondaryPwads should be(Seq(Pwad("mm2mus", "/themes/mm/mm2.zip")))
    }
  }

  private def streamToBytes(is: InputStream): Array[Byte] = {
    if (is == null) throw new RuntimeException("Input stream was null!")
    Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
  }
}
