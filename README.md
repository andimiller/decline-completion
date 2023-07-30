# decline-completion
A module for the decline command line parser to enable bash and zsh autocomplete

## Caveats

This is currently oriented around supporting command line interfaces with a lot of subcommands, and will traverse the tree and expose options available on each subcommand for autocomplete.

If you heavily use shared configuration options which are not attached to subcommands, be aware these are currently not supported, but may be in a future release.

## Usage

Add the dependency, currently available for JVM, JS and Native:

```scala
libraryDependencies ++= List(
  "net.andimiller" %%% "decline-completion" % "0.0.2"
)
```

Add a new subcommand that can expose the output from this library:

```scala
lazy val othercommand = Opts.subcommand("???", "???").as(???)

lazy val completion = Opts.subcommand("completion", "output autocompletion scripts for common shells") {
  val bash = Opts.subcommand("bash", "output autocompletion script for bash").as(
    IO.println(
      Completion.bashCompletion(cli)
    )
  )
  val bash = Opts.subcommand("bash", "output autocompletion script for bash").as(
    IO.println(
      Completion.zshBashcompatCompletion(cli)
    )
  ) 

}

lazy val cli = Command("myprogram", "my program does things") {
  othercommand orElse completion
}
```

Then call it on the command line to use it:

### bash

```sh
myprogram completion bash > myprogram-completion.bash
source myprogram-completion.bash
```

### zsh

```sh
myprogram completion zsh > myprogram-completion.zsh
source myprogram-completion.zsh
```
