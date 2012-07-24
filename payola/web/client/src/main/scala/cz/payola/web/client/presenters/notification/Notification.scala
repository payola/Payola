package cz.payola.web.client.presenters.notification

import s2js.compiler.javascript

// HTML5 notification
object Notification
{
    /** Posts a HTML5 notification. Already handles permission requests.
     *
     * @param url URL of the site.
     * @param title Title of the notification.
     * @param subtitle Subtitle of the notification.
     */
    def postNotification(url: String, title: String, subtitle: String = "") {
        askForPermission(url, title, subtitle)
    }

    /** Posts a HTML5 notification via a webkitNotifications.requestPermission,
     * making sure the permission is granted. When denied, this method has no
     * effect.
     *
     * @param url URL of the site.
     * @param title Title of the notification.
     * @param subtitle Subtitle of the notification.
     */
    @javascript("if (window.webkitNotifications != undefined) { \nwebkitNotifications.requestPermission(cz.payola.web.client.presenters.notification.Notification.reallyPostNotification(url, title, subtitle)); \n}")
    private def askForPermission(url: String, title: String, subtitle: String) {

    }

    /** A method that actually posts the HTML5 notification.
     *
     * @param url URL of the site.
     * @param title Title of the notification.
     * @param subtitle Subtitle of the notification.
     */
    @javascript("var notification = webkitNotifications.createNotification(url, title, subtitle); notification.show();")
    private def reallyPostNotification(url: String, title: String, subtitle: String) {

    }

}
