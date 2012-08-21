package controllers

import controllers.helpers.Secured
import cz.payola.domain.entities.User
import cz.payola.web.shared.Payola
import cz.payola.web.shared.managers.PluginManager

object Plugin extends PayolaController with Secured
{
    /** Shows the create page for plugin.
     *
     * @return Create page for plugin.
     */
    def createPlugin = authenticated{ user =>
        Ok(views.html.plugin.create(user))
    }

    /** Shows the listing page for plugins.
     *
     * @return Listing page for plugins.
     */
    def listPlugins(page: Int = 1) = authenticated { user =>
        Ok(views.html.plugin.list(user, page))
    }

    def approvePlugin(className: String, userID: String) = authenticated { user: User =>
        // Make sure the user approving the plugin is really the admin
        val pluginOwnerOption = Payola.model.userModel.getById(userID)
        if (pluginOwnerOption.isDefined){
            val pluginOption = PluginManager.approvePluginByUser(className, pluginOwnerOption.get, user)
            if (pluginOption.isDefined){
                Ok(views.html.plugin.approval_process(user, pluginOption.get.name, "approved"))
            }else{
                NotFound(views.html.errors.err404("The plugin cannot be found."))
            }
        }else{
            NotFound(views.html.errors.err404("The plugin owner cannot be found."))
        }
    }

    def rejectPlugin(className: String, userID: String) = authenticated { user: User =>
        val ownerOption = Payola.model.userModel.getById(userID)
        if (ownerOption.isDefined){
            if (PluginManager.rejectPlugin(className, ownerOption.get, user)){
                Ok(views.html.plugin.approval_process(user, className, "rejected"))
            }else{
                NotFound(views.html.errors.err404("The plugin cannot be found."))
            }
        }else{
            NotFound(views.html.errors.err404("The plugin owner cannot be found."))
        }
    }

}
