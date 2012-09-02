package cz.payola.web.client.presenters.entity.plugins

import s2js.compiler.javascript
import s2js.adapters.browser._
import cz.payola.web.shared.managers.PluginManager
import cz.payola.web.client._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.common.ValidationException
import cz.payola.web.client.views.bootstrap.Icon

/**
 * Can't pass the editor's pre ID as we're using it in the native JS, which needs to
 * be compile-time ready
 */
class PluginCreator(val buttonContainerID: String, val listPluginsURL: String) extends Presenter
{
    // Create the ACE editor
    createEditor()

    // Create submit button
    val buttonContainer = document.getElementById(buttonContainerID)
    val submitButton = new Button(new Text("Create Plugin"), "btn-primary", new Icon(Icon.plus, true))

    submitButton.mouseClicked += { e =>
        postCodeToServer(getCode)
        false
    }
    submitButton.render(buttonContainer)

    /**
     * Creates a new editor.
     */
    @javascript(
        """
            window.ace_editor = ace.edit('editor');
            window.ace_editor.setTheme('ace/theme/clouds');
            var ScalaMode = require('ace/mode/scala').Mode;
            window.ace_editor.getSession().setMode(new ScalaMode());
        """)
    private def createEditor() { }

    /**
     * Gets code from the editor.
     */
    @javascript("return window.ace_editor.getSession().getValue();")
    private def getCode: String = {
        ""
    }

    def initialize() {
    }

    /**
     * A post fail callback. Shows an alert that the upload failed.
     * @param t An instance of Throwable.
     */
    private def postFailedCallback(t: Throwable) {
        t match {
            case exc: ValidationException => AlertModal.display("Failed to upload plugin!", exc.fieldName)
            case t: Throwable => fatalErrorHandler(t)
        }
    }

    /**
     * Post success callback. Shows a success alert and redirects back to listing.
     */
    private def postWasSuccessfulCallback() {
        val alert = new AlertModal("Success!", "Plugin compiled without an error. In order for it to be used, " +
            "though, it needs to go through a review by the admin. He's been notified by an email.", "alert-success")
        alert.confirming += { e =>
            window.location.href = listPluginsURL
            true
        }
        alert.render()
    }

    /**
     * Posts code to the server to be compiled and a new plugin created.
     * @param code Code of the plugin.
     */
    private def postCodeToServer(code: String) {
        blockPage("Compiling plugin...")
        PluginManager.uploadPlugin(code) { () =>
            postWasSuccessfulCallback()
            unblockPage()
        } { t =>
            postFailedCallback(t)
            unblockPage()
        }
    }
}
