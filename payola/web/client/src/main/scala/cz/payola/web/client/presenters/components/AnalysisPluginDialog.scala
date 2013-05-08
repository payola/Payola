package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields.TextInput
import cz.payola.web.shared.DomainData
import s2js.compiler._
import s2js.adapters.html
import s2js.adapters.browser.`package`._

class AnalysisPluginDialog() extends Modal("Create a new plugin from an existing analysis", Nil)
{


    @javascript(""" return jQuery("#analysis").select2("val"); """)
    def getChosenAnalysisID : String = ""

    @javascript(
        """
          jQuery("#analysis").select2({
              minimumInputLength: 1,
              multiple: false,
              query: function (query) {
                  var data = {results: []};
                  self.fetchAnalysesByQuery(
                    query.term,
                    function(id, text){ data.results.push({id: id, text: text}); },
                    function(){ query.callback(data); }
                  );
              },
              initSelection : function (element) {
                  var data = [];
                  $(element.val().split(",")).each(function () {
                      var parts = this.split(":");
                      data.push({id: parts[0], text: parts[1]});
                  });
                  return data;
              }
            });
        """)
    def bindSelect {}

    def fetchAnalysesByQuery(term: String, itemCallback: (String, String) => Unit, callback: () => Unit) {
        DomainData.searchAccessibleAnalyses(term) { analyses =>
            analyses.map { u =>
                itemCallback(u.id, u.name)
            }
            callback()
        } { _ =>}
    }

    val placeholder = new Div(List(new TextInput("analysis", "", "Choose an analysis")))

    override val body = List(placeholder)

    override def render(parent: html.Element = document.body) {
        super.render(parent)
        bindSelect
    }
}
