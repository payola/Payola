package cz.payola.model

object ParameterConstrains {
    // Any object is valid
    val ParameterConstrainNone = 0
    // Only Int
    val ParameterConstrainInt = 1 << 0
    // Only Bool
    val ParameterConstrainBool = 1 << 1
    // Only String
    val ParameterConstrainString = 1 << 2
    // Only Float
    val ParameterConstrainFloat = 1 << 3
    // Only Custom object - TODO
    val ParameterConstrainCustomObject = 1 << 24


    /******* Convenience shortcuts *******/
    // Int and float
    val ParameterConstrainNumeric = (ParameterConstrainInt | ParameterConstrainFloat)

    /** Takes a look if the constrains passed don't have some bits set that aren't used by the constrains defined
     *  above.
     *
     * @param constrains The constrains to be checked.
     *
     * @return Whether the constrains are valid or not.
     */
    def areValidConstrains(constrains: Int): Boolean = {
        if (constrains == ParameterConstrainNone)
            true
        else
            (constrains & ~(ParameterConstrainInt | ParameterConstrainBool | ParameterConstrainString |
                ParameterConstrainFloat | ParameterConstrainCustomObject)) == 0
    }

    /** Checks if the constrain passed is one of the constrains defined above.
     *
     * @param constrain The constrain to be checked.
     *
     * @return True or false.
     */
    def isValidConstrain(constrain: Int): Boolean = {
        constrain == ParameterConstrainNone ||
        constrain == ParameterConstrainInt ||
        constrain == ParameterConstrainBool ||
        constrain == ParameterConstrainString ||
        constrain == ParameterConstrainFloat ||
        constrain == ParameterConstrainCustomObject
    }
}

import ParameterConstrains._

class Parameter {
    /** Value constrains. Used by ParameterInstance to check the values passed in the setters.  */
    private var _constrains: Int = 0

    /** Adds a constrain to the constrains.
     *
     * @param constrain The constrain.
     *
     * @throws AssertionError if the constrain isn't valid.
     */
    def addConstrain(constrain: Int) = {
        assert(ParameterConstrains.isValidConstrain(constrain), "Passed constrain is not valid (" + constrain + ")")
        _constrains |= constrain
    }

    /** Constrains getter.
     *
     * @return The constrains bit-field.
     */
    def constrains: Int = _constrains

    /** Constrains setter.
     *
     *  @parameter newConstrains New constrains.
     *
     *  @throws AssertionError if the constrains aren't valid.
     */
    def constrains_=(newConstrains: Int) = {
        assert(ParameterConstrains.areValidConstrains(newConstrains))
        
        _constrains = newConstrains
    }

    /** Checks whether the parameter has a particular constrain. Must be one of the constrains defined in
     *  the ParameterConstrains object.
     *
     * @param constrain The constrain to be checked.
     *
     * @return True or false.
     *
     * @throws AssertionError if the constrain isn't valid.
     */
    def hasValueConstrain(constrain: Int): Boolean = {
        assert(ParameterConstrains.isValidConstrain(constrain), "Passed constrain is not valid (" + constrain + ")")

        (_constrains & constrain) != 0
    }

    /** Returns whether the parameter is value-constrained.
     *
     *  @return True or false.
     */
    def isConstrained:Boolean = _constrains == ParameterConstrainNone

    /** Removes a constrain from the constrains.
     *
     * @param constrain The constrain.
     *
     * @throws AssertionError if the constrain isn't valid.
     */
    def removeConstrain(constrain: Int) = {
        assert(ParameterConstrains.isValidConstrain(constrain), "Passed constrain is not valid (" + constrain + ")")
        _constrains &= ~constrain
    }

    /** Convenience method that just calls constrains_=()
     *
     * @throws AssertionError if the constrains aren't valid.
     */
    def setValueConstrains(newConstrains: Int) = constrains_=(newConstrains)

}
