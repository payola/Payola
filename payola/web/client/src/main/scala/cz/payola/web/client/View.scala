package cz.payola.web.client

import s2js.compiler.javascript
import s2js.adapters.html
import s2js._

object View
{
    @javascript("$.blockUI(self.getBlockParameters(message));")
    def blockPage(message: String = "") { }

    @javascript("$.unblockUI({ fadeOut: 0 });")
    def unblockPage() { }

    @javascript("$(target).block(self.getBlockParameters(message));")
    private def block(target: html.Element, message: String) { }

    @javascript("$(target).unblock({ fadeOut: 0 });")
    private def unblock(target: html.Element) { }

    @javascript("""
        return {
            message: self.messageToHtml(message),
            fadeIn: 100,
            css: {
                padding: 0,
                border: '0'
            },
            overlayCSS: {
                backgroundColor: '#FFF',
                opacity: 0.6
            }
        };""")
    private def getBlockParameters(message: String): String = ""

    private def messageToHtml(message: String): String = {
        if (message == null || message == "") {
            null
        } else {
            """
                <div class="row">
                    <h3 style="padding: 20px;">%s</h3>
                </div>
                <div class="row">
                    <div class="progress progress-striped active">
                     <div class="progress-bar" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 100%%">
                       <span class="sr-only">45%% Complete</span>
                     </div>
                    </div>
                </div>
            """.format(message)
        }
    }
}

/**
 * The View trait is a base trait for all the components which one can implement. All such components should be derived
 * from this trait. By doing that, you'll get common interface for all the view components, as well as some
 * expected behaviour.
 *
 * The trait needs you to implement methods which defines:
 * - how the view is rendered
 * - how the view is destroyed
 * - how the DOM element representing the view can be reached
 *
 * It comes with implemented functionality - blocking and unblocking the UI of the view. This is done while using
 * the companion object of this trait.
 */
trait View
{
    /**
     * Constructs this View's HTML representation and appends it to the parent HTML element.
     * @param parent element to which this View will be appended
     */
    def render(parent: html.Element)

    /**
     * Destroys inner variables and removes this View's HTML representation from its parent element.
     */
    def destroy()

    /**
     * This method should return the DOM element which contains the whole view. It is used to block the user
     * interface to avoid the user to click on nested elements, etc.
     */
    def blockHtmlElement: html.Element

    /**
     * Prevents the user from interacting with the view
     * @param message Message which is displayed to the user
     */
    def block(message: String = "") {
        View.block(blockHtmlElement, message)
    }

    /**
     * Stop blocking the UI
     */
    def unblock() {
        View.unblock(blockHtmlElement)
    }
}
