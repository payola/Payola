package cz.payola.domain.entities.plugins.compiler

import scala.tools.nsc.reporters.Reporter
import scala.tools.nsc.util._

/**
  * A compiler error reporter that reports errors by throwing exceptions instead of writing it to the standard output.
  */
class ExceptionReporter extends Reporter
{
    protected def info0(pos: Position, msg: String, severity: this.type#Severity, force: Boolean) {
        if (severity != INFO) {
            val severityDescription = severity match {
                case WARNING => "Warning"
                case ERROR => "Error"
            }
            val position = pos match {
                case NoPosition => ""
                case _ => " on line %s, column %s".format(pos.line, pos.column)
            }
            throw new PluginCompilationException("%s%s: %s".format(severityDescription, position, msg))
        }
    }
}
