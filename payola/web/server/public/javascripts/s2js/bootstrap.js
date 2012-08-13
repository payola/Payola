s2js.runtime.client.core.get().classLoader.provide('s2js.bootstrap');

// The first script to execute.
s2js.runtime.client.core.get().classLoader.declarationRequire('s2js.bootstrap.temporaryClassLoader');

// The following classes have to be declared first because all class declarations create an instance of the Class class
// and all object declarations also create instances of the Lazy class.
s2js.runtime.client.core.get().classLoader.declarationRequire('s2js.runtime.client.core.Class');
s2js.runtime.client.core.get().classLoader.declarationRequire('s2js.runtime.client.core.Lazy');

// Bootstrap the core. The full-featured class loader is part of the core object and because the core gets accessed
// immediately, the class loader is instantiated immediately, so it has to be already declared.
s2js.runtime.client.core.get().classLoader.declarationRequire('s2js.runtime.client.core.ClassLoader');
s2js.runtime.client.core.get().classLoader.declarationRequire('s2js.runtime.client.core');

// Swap the temporary class loader for the full-featured class loader.
s2js.runtime.client.core.get().classLoader.declarationRequire('s2js.bootstrap.swapClassLoaders');

// Bootstrap the features of scala that are provided by default.
s2js.runtime.client.core.get().classLoader.declarationRequire('scala.Predef');
s2js.runtime.client.core.get().classLoader.declarationRequire('s2js.bootstrap.initializeTypeSystem');

// Bootstrap the additional features.
s2js.runtime.client.core.get().classLoader.declarationRequire('s2js.runtime.client.rpc.Wrapper');
