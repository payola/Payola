s2js.runtime.client.ClassLoader.provide('bootstrap.ClassLoader');

// Move the loaded classes from the temporary class loader to the current class loader.
s2js.runtime.client.ClassLoader.loadedClasses = scala.collection.mutable.ArrayBuffer.fromJsArray(
    s2js.runtime.client.ClassLoader.loadedClasses.getInternalJsArray().concat(TemporaryClassLoader.internalLoadedClasses));

// The require won't be called on the temporary class loader so it will check whether the class is loaded. Therefore
// we have to move the loaded classes from the temporary class loader to the current class loader before the require is
// actually called. Otherwise, an exception stating that s2js.runtime.client.ClassLoader isn't loaded would be thrown.
s2js.runtime.client.ClassLoader.require('s2js.runtime.client.ClassLoader');
