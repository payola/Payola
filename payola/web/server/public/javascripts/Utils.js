Utils = {
    declareNamespace: function(namespace) {
        var parentNamespace = window;
        while (namespace != '') {
            var index = namespace.indexOf('.');
            var name = '';
            if (index >= 0) {
                name = namespace.substring(0, index);
                namespace = namespace.substring(index + 1);
            } else {
                name = namespace;
                namespace = '';
            }

            if (typeof(parentNamespace[name]) === 'undefined') {
                parentNamespace[name] = {};
            }
            parentNamespace = parentNamespace[name];
        }
    }
};

TemporaryClassLoader = {
    internalLoadedClasses: [],
    provide: function(className) {
        Utils.declareNamespace(className);
        this.internalLoadedClasses.push(className);
    },
    require: function(className) { }
}

Utils.declareNamespace('s2js.runtime.client');
s2js.runtime.client.ClassLoader = TemporaryClassLoader

// The provide has to be used after the class loader is declared.
s2js.runtime.client.ClassLoader.provide('Utils');
