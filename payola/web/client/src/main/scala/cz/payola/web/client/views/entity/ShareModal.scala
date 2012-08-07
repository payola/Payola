package cz.payola.web.client.views.entity

import s2js.compiler.javascript
import s2js.adapters.html
import cz.payola.web.client.views.bootstrap._
import cz.payola.common.entities._
import cz.payola.web.client.events._
import cz.payola.web.client.views.elements.form.fields.TextInput

class ShareModal(
    val entityName: String,
    val granteeClassNameText: String,
    val grantees: Seq[PrivilegableEntity])
    extends Modal("Share " + entityName + " to " + granteeClassNameText + "s", Nil, Some("Share"))
{
    val granteeSearching = new UnitEvent[ShareModal, GranteeSearchEventArgs]

    val granteeSelection = new InputControl(
        "Share to " + granteeClassNameText + "s",
        new TextInput("", "init", "Enter name")
    )

    override val body = List(granteeSelection)

    override def render(parent: html.Element) {
        super.render()
        initializeGranteeSelection()
    }

    private def triggerGranteeSearching(searchTerm: String, successCallback: Seq[PrivilegableEntity] => Unit) {
        granteeSearching.trigger(new GranteeSearchEventArgs(this, searchTerm, successCallback))
    }

    @javascript( """
        jQuery(self.granteeSelection.input.htmlElement).select2({
            minimumInputLength: 1,
            multiple: true,
            initSelection: function(element) {
                return self.granteesToSelectObjects(self.grantees);
            },
            query: function(query) {
                self.triggerGranteeSearching(query.term, function(grantees) {
                    query.callback({ results: self.granteesToSelectObjects(grantees) });
                });
            }
        });
                 """)
    private def initializeGranteeSelection() {}

    @javascript("return grantees.map(function(g) { return { id: g.id, text: g.name() }; }).getInternalJsArray();")
    private def granteesToSelectObjects(grantees: Seq[PrivilegableEntity]): Any = ""
}

class GranteeSearchEventArgs(
    target: ShareModal,
    val searchTerm: String,
    val successCallback: Seq[PrivilegableEntity] => Unit)
    extends EventArgs[ShareModal](target)
