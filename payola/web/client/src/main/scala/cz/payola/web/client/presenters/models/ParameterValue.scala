package cz.payola.web.client.presenters.models

import cz.payola.web.client.views.bootstrap.InputControl

class ParameterValue(val pluginInstanceId: String, val parameterId: String, val name: String, val value: String, val control: InputControl[_])
{
}
