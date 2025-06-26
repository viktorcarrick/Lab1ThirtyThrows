package se.umu.cs.ens20vck.lab1_thirtythrows

/**
 * Model class, represents a die in the application.
 */
data class Die(
    //Identifier
    val id: Int,
    //Value of the die
    var value: Int,
    //Indicates whether a die has been selected
    var isSelected : Boolean = false,
    //Indicates whether a die has been paired
    var isPaired : Boolean = false,
)
