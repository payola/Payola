goog.provide('scala.math');
scala.math.E = Math.E;
scala.math.Pi = Math.PI;
scala.math.abs = function(x) {
var self = this;
return Math.abs(x);};
scala.math.acos = function(x) {
var self = this;
return Math.acos(x);};
scala.math.asin = function(x) {
var self = this;
return Math.asin(x);};
scala.math.atan = function(x) {
var self = this;
return Math.atan(x);};
scala.math.atan2 = function(x) {
var self = this;
return Math.atan2(x);};
scala.math.ceil = function(x) {
var self = this;
return Math.ceil(x);};
scala.math.cos = function(x) {
var self = this;
return Math.cos(x);};
scala.math.exp = function(x) {
var self = this;
return Math.exp(x);};
scala.math.floor = function(x) {
var self = this;
return Math.floor(x);};
scala.math.log = function(x) {
var self = this;
return Math.log(x);};
scala.math.max = function(x, y) {
var self = this;
return Math.max(x, y);};
scala.math.min = function(x, y) {
var self = this;
return Math.min(x, y);};
scala.math.pow = function(x, y) {
var self = this;
return Math.pow(x, y);};
scala.math.sqrt = function(x) {
var self = this;
return Math.sqrt(x);};
scala.math.random = function() {
var self = this;
return Math.random();};
scala.math.round = function(x) {
var self = this;
return Math.round(x);};
scala.math.sin = function(x) {
var self = this;
return Math.sin(x);};
scala.math.tan = function(x) {
var self = this;
return Math.tan(x);};
scala.math.signum = function(x) {
var self = this;
if(x > 0) { return 1; } else if(x == 0) { return 0; } else { return -1; }};
scala.math.__class__ = new s2js.Class('scala.math', []);
