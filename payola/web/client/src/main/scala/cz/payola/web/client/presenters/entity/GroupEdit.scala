package cz.payola.web.client.presenters.entity

import s2js.compiler.javascript
import cz.payola.web.shared.DomainData
import cz.payola.web.client.Presenter

class GroupEdit(groupId: String) extends Presenter
{
    def initialize {
        bindSelect
    }

    @javascript(
        """
          jQuery("#members").select2({
              minimumInputLength: 1,
              multiple: true,
              query: function (query) {
                  var data = {results: []};
                  self.fetchUsersByQuery(
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

    def fetchUsersByQuery(term: String, itemCallback: (String, String) => Unit, callback: () => Unit) {
        DomainData.searchMembersAvailableForGroup(groupId, term) { users =>
            users.map { u =>
                itemCallback(u.id, u.name)
            }
            callback()
        } { _ =>}
    }
}
