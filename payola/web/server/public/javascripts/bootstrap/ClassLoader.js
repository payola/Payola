s2js.ClassLoader.provide('bootstrap.ClassLoader');

// Move the loaded classes from the temporary class loader to the current class loader.
s2js.ClassLoader.loadedClasses = s2js.ClassLoader.loadedClasses.getInternalJsArray().concat(
    TemporaryClassLoader.internalLoadedClasses);

// The require won't be called on the temporary class loader so it will check whether the class is loaded. Therefore
// we have to move the loaded classes from the temporary class loader to the current class loader before the require is
// actually called. Otherwise, an exception stating that s2js.ClassLoader isn't loaded would be thrown.
s2js.ClassLoader.require('s2js.ClassLoader');
