package s2js.adapters.html.elements

import s2js.adapters.html.Element

trait Input extends Element with InputLike
{
    var `type`: String
}

trait TextInput extends Input with TextInputLike
{
    var maxLength: Int
}

trait CheckInput extends Input
{
    var checked: Boolean

    val defaultChecked: Boolean
}

trait InputButton extends Input

trait InputCheckbox extends CheckInput

trait InputFile extends Input
{
    var accept: String
}

trait InputHidden extends Input

trait InputPassword extends TextInput

trait InputRadio extends CheckInput

trait InputReset extends Input

trait InputSubmit extends Input

trait InputText extends TextInput
