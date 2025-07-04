package se.umu.cs.ens20vck.lab1_thirtythrows.dataModels
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
/**
 * Model class, represents a die in the game.
 *
 * @author Viktor Carrick (ens20vck@cs.umu.se)
 */
@Parcelize
data class Die(
    //Identifier
    val id: Int,
    //Value of the die
    var value: Int,
    //Indicates whether a die has been selected
    var isSelected : Boolean = false,
    //Indicates whether a die has been paired
    var isPaired : Boolean = false,
):Parcelable
