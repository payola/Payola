package controllers

import controllers.helpers.Secured

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

}
