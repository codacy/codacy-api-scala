package com.codacy.api

import com.codacy.api.util.JsonEnumeration

object Language extends JsonEnumeration {
  val 
    Javascript,
    Scala,
    CSS,
    PHP,
    Python,
    Ruby,
    Java,
    CoffeeScript,
    Swift,
    CPPC,
    Shell,
    TypeScript,
    Dockerfile,
    SQL,
    PLSQL,
    JSON,
    SASS,
    LESS,
    Go,
    JSP,
    Velocity,
    XML,
    Apex,
    Elixir,
    Clojure,
    Rust,
    Haskell,
    Erlang,
    YAML,
    Dart,
    Elm,
    HTML,
    Groovy,
    VisualForce,
    Perl,
    CSharp,
    VisualBasic,
    ObjectiveC,
    FSharp,
    Cobol,
    Fortran,
    R,
    Scratch,
    Lua,
    Lisp,
    Prolog,
    Julia,
    Kotlin,
    OCaml
      = Value

  val NotDefined = Value("UndefinedLanguage")
}
