// The temporary class loader script is actually the first script that gets executed in a s2js application. After that,
// the s2js.runtime.client.core is loaded.
s2js = {
    bootstrap: {
        // A class loader that may be used during the phase when the full-featured class loader isn't declared yet. The
        // full-featured class loader declaration starts with a provide call on the current class loader, os there has
        // to be some object, that will process it. When the full-featured class loader is declared, it may be switched
        // for the temporary class loader.
        temporaryClassLoader: {
            loadedClasses: [],
            provide: function(className) { this.loadedClasses.push(className); },
            declarationRequire: function(className) { },
            require: function(className) { }
        }
    },
    runtime: {
        client: {
            core: {

            }
        }
    }
};

// Setup the temporary class loader. Note that the temporary class loader can't be declared directly in the core object
// because when the full-featured class loader gets declared, the temporary class loader would be overriden and we would
// lost track of it.
s2js.runtime.client.core.get = function() {
    return {
        classLoader: s2js.bootstrap.temporaryClassLoader,
        mixIn: function(target, source) {
            for (var i in source) { target[i] = source[i]; }
        }
    };
};

// The provide has to be used after the class loader is declared so this file can be required.
s2js.runtime.client.core.get().classLoader.provide('s2js.bootstrap.temporaryClassLoader');
