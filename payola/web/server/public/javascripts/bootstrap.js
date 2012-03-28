s2js.ClassLoader.provide('bootstrap');

// The utils have to be required first because the temporary class loader is declared there and it's necessary for all
// the following requirements.
s2js.ClassLoader.require('Utils');

// Base Google Closure library files that declare additional methods (object extension, type checks) that are used in
// the compiled JavaScript code.
s2js.ClassLoader.require('goog');
s2js.ClassLoader.require('goog.object');

// The following files are all compiled from Scala to JavaScript. The Class has to be required first because all class
// declarations create an instance of the Class class.
s2js.ClassLoader.require('s2js.Class');
s2js.ClassLoader.require('s2js');

// Implicit requirements that are imported to scala programs by default.
s2js.ClassLoader.require('scala.Predef');

// Bootstrap the type system.
s2js.ClassLoader.require('bootstrap.TypeSystem');

// Bootstrap the class loader.
s2js.ClassLoader.require('bootstrap.ClassLoader');
