s2js.runtime.client.core.get().classLoader.provide('s2js.bootstrap.swapClassLoaders');

// Move the loaded classes from the temporary class loader to the current class loader.
for (var i in s2js.bootstrap.temporaryClassLoader.loadedClasses) {
    s2js.runtime.client.core.get().classLoader.loadedClasses.push(s2js.bootstrap.temporaryClassLoader.loadedClasses[i]);
}
