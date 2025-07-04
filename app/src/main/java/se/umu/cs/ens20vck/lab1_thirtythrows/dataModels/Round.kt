package se.umu.cs.ens20vck.lab1_thirtythrows.dataModels
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
/**
 * Model class representing a round in the game.
 *
 * @author Viktor Carrick (ens20vck@cs.umu.se)
 */
@Parcelize
data class Round(
    //The scoring choice selected by the user
    val choice: String,
    //Total score for the round
    val score: Int,
    //The dices related to the round,currently not used in the application
    val dice: List<List<Die>> = emptyList()
):Parcelable
