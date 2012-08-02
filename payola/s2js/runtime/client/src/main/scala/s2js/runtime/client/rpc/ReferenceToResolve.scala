package s2js.runtime.client.rpc

import s2js.runtime.shared.rpc.RpcException

class ReferenceToResolve(val reference: Reference, val sourceObject: Any, val propertyName: String)
{
    def resolve(context: DeserializationContext) {
        val targetObjectID = reference.targetObjectID
        val targetObject = context.objectRegistry.get(targetObjectID)
        if (targetObject.isEmpty) {
            throw new RpcException("The deserialized object graph contains an invalid reference '" + targetObjectID + "'.")
        }

        // The reference has to be resolved using eval, because the property name can be nontrivial (array item access).
        s2js.adapters.js.eval("self.sourceObject." + propertyName + " = targetObject.get()")
    }
}
