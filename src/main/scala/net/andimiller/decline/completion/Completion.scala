package net.andimiller.decline.completion

import cats.implicits._
import com.monovore.decline._
import com.monovore.decline.completion.Folder

object Completion {

  def bashCompletion(c: Command[_]): String = {
    val model      = Folder.buildModel(c)
    val conditions = model.completionTree().toList.sortBy(-_._1.size).map { case (path, wordsWithHelp) =>
      val (words, _) = wordsWithHelp.separate
      val cond       = path.reverse.zipWithIndex
        .map { case (cmd, idx) =>
          s"\"$${cmds[$idx]}\" = \"$cmd\""
        }
        .mkString("[[ ", " && ", " ]]")
      s"""if $cond; then
         |  COMPREPLY=( $$(compgen -W '${words.mkString(" ")}' -- "$$cur"))
         |  return
         |fi
         |""".stripMargin
    }

    s"""
       |_${c.name}()
       |{
       |local cmds=($${COMP_WORDS[@]//-*}) # Filter out any flags or options
       |local cur
       |if [[ "$$SHELL" =~ ".*zsh" ]]; then
       |  cur=$$COMP_CWORD
       |else
       |  cur=`_get_cword`
       |fi
       |${conditions.mkString("\n")}
       |}
       |complete -F _${c.name} ${c.name}
       |""".stripMargin
  }

  def zshBashcompatCompletion(c: Command[_]): String =
    """autoload bashcompinit
      |bashcompinit
      |""".stripMargin + bashCompletion(c)

}
