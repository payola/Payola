goog.require("goog");
goog.require("goog.object");
goog.require("s2js.Class");
goog.require("s2js");

goog.require("scala.Predef");
goog.require("scala.collection.mutable.ArrayBuffer");
goog.require("scala.String");

// Extend the JavaScript String prototype with methods of scala.String.
goog.object.extend(String.prototype, scala.String.prototype);

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
        value: function() { return this; },
        writable: true,
        configurable: true
    });
}

extendJsArrayToArrayBuffer(Array.prototype);

var pixelArrayPrototype = document.createElement('canvas').getContext('2d').getImageData(0, 0, 1, 1).data.__proto__;
if (window.CanvasPixelArray) {
    pixelArrayPrototype = CanvasPixelArray.prototype;
}
extendJsArrayToArrayBuffer(pixelArrayPrototype);
