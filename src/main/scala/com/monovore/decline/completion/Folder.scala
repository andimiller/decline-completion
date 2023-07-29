package com.monovore.decline.completion

import cats.implicits._
import com.monovore.decline.Opts.Name
import com.monovore.decline.{Command, Opt, Opts}

object Folder {

  case class CompleteableCommand(
      name: String,
      help: String,
      flags: List[(String, String)] = Nil,
      arguments: List[String] = Nil,
      subcommands: List[CompleteableCommand] = Nil
  ) {

    private def completionTree(path: List[String])(c: CompleteableCommand): Map[List[String], List[(String, String)]] = {
      val here        = c.name :: path
      val completions = c.flags ++ c.subcommands.map(s => s.name -> s.help)
      c.subcommands
        .map { s =>
          completionTree(here)(s)
        }
        .fold(Map(here -> completions)) { case (m, n) => m ++ n }
    }

    def completionTree(): Map[List[String], List[(String, String)]] = completionTree(List.empty)(this)
  }

  type Modifier = CompleteableCommand => CompleteableCommand

  def nameToString(n: Name): String = n match {
    case Opts.LongName(flag)  => "--" + flag
    case Opts.ShortName(flag) => "-" + flag
  }

  def one(p: Opt[_]): Modifier = p match {
    case Opt.Regular(names, metavar, help, visibility) => identity // TODO figure out what this is
    case Opt.OptionalOptArg(names, _, _, _)            =>
      c => c.copy(arguments = c.arguments ++ names.map(nameToString))
    case Opt.Flag(names, help, visibility)             =>
      c => c.copy(flags = c.flags ++ names.map(nameToString).tupleRight(help))
    case Opt.Argument(metavar)                         =>
      c => c.copy(arguments = c.arguments ++ List(metavar))
  }

  def many(p: Opts[_]): Modifier = p match {
    case Opts.Pure(_)        => identity
    case Opts.HelpFlag(a)    =>
      c => c.copy(flags = c.flags.prepended("--help", "Print help and usage information."))
    case Opts.App(f, a)      =>
      many(f) andThen many(a)
    case Opts.OrElse(a, b)   =>
      many(a) andThen many(b)
    case Opts.Single(opt)    =>
      one(opt)
    case Opts.Repeated(opt)  =>
      one(opt)
    case Opts.Validate(a, _) =>
      many(a)
    case Opts.Subcommand(s)  =>
      c => c.copy(subcommands = c.subcommands.prepended(buildModel(s)))
    case _: Opts.Env         => identity
    case _                   => identity
  }

  def buildModel(c: Command[_]): CompleteableCommand =
    many(c.options)(CompleteableCommand(c.name, c.header))

}
