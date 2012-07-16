package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.views.elements._

class SpanButton(text: String, cssClass: String = "") extends Span(List(new Text(text)), "btn " + cssClass)
