package cz.payola.model.generic

import java.util.UUID
import cz.payola.common.model.ModelObject

trait ConcreteModelObject extends ModelObject {
    // Lazy objectID creation
    var _objectID: String = null
    
    def objectID: String = {
        if (_objectID == null){
            // Hasn't been created yet, create it
            _objectID = UUID.randomUUID.toString
        }
        _objectID
    }

    def objectID_=(objID: String) = _objectID = objID
    
    
}

