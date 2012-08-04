package s2js.adapters.html.elements

import s2js.adapters.js.html._
import s2js.adapters.html.Element

abstract class Input extends Element with InputLike
{
    var `type`: String
}

abstract class TextInput extends Input with TextInputLike
{
    var maxLength: Int
}

abstract class CheckInput extends Input
{
    var checked: Boolean

    val defaultChecked: Boolean
}

abstract class InputButton extends Input

abstract class InputCheckbox extends CheckInput

abstract class InputFile extends Input
{
    var accept: String
}

abstract class InputHidden extends Input

abstract class InputPassword extends TextInput

abstract class InputRadio extends CheckInput

abstract class InputReset extends Input

abstract class InputSubmit extends Input

abstract class InputText extends TextInput
