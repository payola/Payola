goog.require("goog");
goog.require("goog.object");
goog.require("s2js.Class");
goog.require("s2js");

goog.require("scala.Predef");
goog.require("scala.collection.mutable.ArrayBuffer");
goog.require("java.lang.String");

// Extend the JavaScript String prototype with methods of java.lang.String.
goog.object.extend(String.prototype, java.lang.String.prototype);

// Extend the JavaScript Arrays with the methods of scala.collection.mutable.ArrayBuffer.
function extendJsArrayToArrayBuffer(jsArray) {
    var arrayBufferPrototype = scala.collection.mutable.ArrayBuffer.prototype;
    for (var propertyName in arrayBufferPrototype) {
        var propertyValue = arrayBufferPrototype[propertyName];

        if (propertyName !== 'constructor' && typeof(propertyValue) === 'function') {
            Object.defineProperty(jsArray.prototype, propertyName, {
                value: propertyValue,
                writable: true,
                configurable: true
            });
        }
    }

    // The JavaScript Array itself should behave as the internal JavaScript Array of the scala ArrayBuffer.
    Object.defineProperty(jsArray.prototype, 'getInternalJsArray', {
        value: function() { return this; },
        writable: true,
        configurable: true
    });
}

extendJsArrayToArrayBuffer(Array);
extendJsArrayToArrayBuffer(CanvasPixelArray);
