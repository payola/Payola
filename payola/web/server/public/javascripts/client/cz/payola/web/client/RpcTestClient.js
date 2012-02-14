goog.provide('cz.payola.web.client.RpcTestClient');
goog.require('s2js.Rpc');
cz.payola.web.client.RpcTestClient.test = function() {
var self = this;
window.alert(s2js.Rpc.callSync('cz.payola.web.shared.RpcTestRemote.foo',[123, 'xyz']));
};
cz.payola.web.client.RpcTestClient.metaClass_ = new s2js.MetaClass('cz.payola.web.client.RpcTestClient', []);
