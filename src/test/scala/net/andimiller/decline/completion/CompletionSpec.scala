package net.andimiller.decline.completion

import cats.effect.IO
import cats.implicits._
import com.monovore.decline.completion.Folder
import com.monovore.decline.completion.Folder.CompleteableCommand
import com.monovore.decline.{Command, Opts}
import munit.CatsEffectSuite

class CompletionSpec extends CatsEffectSuite {
  test("Folder should fold a cli into a model we can use") {
    val cli = Command("foo", "the foo command") {
      (Opts.argument[String]("file.txt"), Opts.flag("verbose", "enable verbose logs").orNone.map(_.isDefined)).mapN { case (f, v) =>
        123
      }
    }
    IO {
      Folder.buildModel(cli)
    }.assertEquals(
      CompleteableCommand(
        "foo",
        "the foo command",
        List("--help" -> "Print help and usage information.", "--verbose" -> "enable verbose logs"),
        List("file.txt")
      )
    )
  }
  test("Completion should be able to produce zsh and bash scripts") {
    val deeper = Command("binary", "help") {
      val number    = Opts.argument[Int]("n")
      val verbose   = Opts.flag("verbose", "enable verbose debug logs").orFalse
      val extraFlag = Opts.flag("extra-flag", "extra flag only on add, with a short form", "e")
      val add       = Opts.subcommand("add", "add two numbers") {
        (number, number, verbose, extraFlag).mapN { case (a, b, v, _) =>
          if (v) println(s"adding $a and $b")
          println(a + b)
        }
      }
      val multiply  = Opts.subcommand("multiply", "multiply two numbers") {
        (number, number, verbose).mapN { case (a, b, v) =>
          if (v) println(s"multiplying $a and $b")
          println(a * b)
        }
      }
      add orElse multiply
    }
    IO {
      Completion.bashCompletion(deeper)
      Completion.zshBashcompatCompletion(deeper)
    }
  }
}
