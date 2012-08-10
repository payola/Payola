s2js.runtime.client.core.get().classLoader.provide('s2js.bootstrap.initializeTypeSystem');

// Classes used to extend the prototypes of primitive JavaScript types and Arrays.
s2js.runtime.client.core.get().classLoader.declarationRequire('scala.collection.mutable.ArrayBuffer');
s2js.runtime.client.core.get().classLoader.declarationRequire('scala.String');

// Extend the JavaScript Object prototype with methods of a scala object.
Object.defineProperty(Object.prototype, 'getClass', {
    value: function() { return this.__class__; },
    writable: true,
    configurable: true
});

// Extend the JavaScript String prototype with methods of scala.String.
s2js.runtime.client.core.get().mixIn(String.prototype, scala.String.prototype);

// Extend the JavaScript Arrays with the methods of scala.collection.mutable.ArrayBuffer.
function extendJsArrayToArrayBuffer(jsArrayPrototype) {
    var arrayBufferPrototype = scala.collection.mutable.ArrayBuffer.prototype;
    for (var propertyName in arrayBufferPrototype) {
        var propertyValue = arrayBufferPrototype[propertyName];

        if (propertyName !== 'constructor' && typeof(propertyValue) === 'function') {
            Object.defineProperty(jsArrayPrototype, propertyName, {
                value: propertyValue,
                writable: true,
                configurable: true
            });
        }
    }

    // The JavaScript Array itself should behave as the internal JavaScript Array of the scala ArrayBuffer.
    Object.defineProperty(jsArrayPrototype, 'getInternalJsArray', {
        value: function() { return Array.prototype.slice.call(this); },
        writable: true,
        configurable: true
    });
}

extendJsArrayToArrayBuffer(Array.prototype);

// The CanvasPixelArray.prototype doesn't work in Chrome so the prototype has to be obtained from an instance.
var pixelArrayPrototype = document.createElement('canvas').getContext('2d').getImageData(0, 0, 1, 1).data.__proto__;
if (window.CanvasPixelArray) {
    pixelArrayPrototype = CanvasPixelArray.prototype;
}
extendJsArrayToArrayBuffer(pixelArrayPrototype);
