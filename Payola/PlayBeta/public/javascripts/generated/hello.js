goog.provide('generated.Hello');
goog.provide('generated.A');
goog.provide('generated.B');


if (!generated.Hello) generated.Hello = {};
/**
 */
generated.Hello.main = function () {
  
  var b = new generated.B(4, "Ahoy");
  alert(b.x(3));
  alert(b.y("123*"));
  alert("Hello world");
};

goog.exportSymbol("generated.Hello.main", generated.Hello.main); 

/**
 * @constructor
 * @param {number} foo
 * @param {string} bar
 */
generated.A = function (foo, bar) {
  


  this.foo  = foo 

};

/**
 * @private
 * @type {number}
 */
generated.A.prototype.foo  = null;


/**
 * @param {number} baz
 * @return {string}
 */
generated.A.prototype.x = function (baz) {
  
  return this.bar.charAt(baz).toString();
};


/**
 * @constructor
 * @param {number} foo
 * @param {string} bar
 */
generated.B = function (foo, bar) {
  




};

/**
 * @param {string} baz
 * @return {string}
 */
generated.B.prototype.y = function (baz) {
  
  return baz.charAt(this.foo).toString();
};

