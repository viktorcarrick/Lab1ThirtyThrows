package se.umu.cs.ens20vck.lab1_thirtythrows.viewModels

import android.widget.TextView.SavedState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import se.umu.cs.ens20vck.lab1_thirtythrows.dataModels.Die

//Helper class for the dice, creates initial dice list and rolls the dice
/**
 * ViewModel class that manages the list of dice used in game.
 * Initializes the dice list, handles rolling and state changes.
 *
 * @author Viktor Carrick (ens20vck@cs.umu.se)
 */
class DiceViewModel(private val savedState:SavedStateHandle): ViewModel() {
    // Mutable live data list of dice
    private val _diceList = savedState.getLiveData<List<Die>>("diceList")

    // Immutable list of dice for observation
    val diceList: LiveData<List<Die>> = _diceList

    // Initiates the list of dice, assigns a value and unique id for each die
    init {
        if(savedState.get<List<Die>>("diceList") == null) {
            val initDiceList = mutableListOf<Die>()
            for (i in 1..6) {
                val dice = Die(id = i, value = i)
                initDiceList.add(dice)
            }
            _diceList.value = initDiceList
            savedState["diceList"] = initDiceList
        }
    }

    /**
     * Rolls all dice in the list by assigning a random value
     * from 1 to 6. Resets the selected state for each die.
     */
    fun rollAllDice(){
        val diceListCopy = _diceList.value.orEmpty()
        val updatedDiceList = diceListCopy.map { die ->
            die.copy(
                value = (1..6).random(),
                isSelected = false
            )
        }
        _diceList.value = updatedDiceList
        savedState["diceList"] = updatedDiceList
    }

    /**
     * Rolls only the dice currently marked as selected.
     * Resets the selected state for each rolled dice.
     */
    fun rollSelectedDice(){
        val diceListCopy = _diceList.value.orEmpty()
        val updatedSelectedDiceList = diceListCopy.map { die ->
            if(die.isSelected){
                die.copy(
                    value = (1..6).random(),
                    isSelected = false
                )
            } else { die }
        }
        _diceList.value = updatedSelectedDiceList
        savedState["diceList"] = updatedSelectedDiceList
    }

    /**
     * Toggles the state of a die based on the current throw.
     * If the throw is 0 or 1: isSelected is toggled.
     * If the throw is 2: isPaired is toggled.
     *
     * @param index - index of the die to toggle.
     * @param throwCounter - indicates what throw the user is on.
     */
    fun toggleDiceState(index:Int, throwCounter: Int){
        val updateDiceList = _diceList.value.orEmpty().mapIndexed() { i, die ->
            if(i==index){
                when(throwCounter) {
                    0, 1 -> die.copy (isSelected = !die.isSelected)
                    2 -> die.copy(isPaired = !die.isPaired)
                    else -> die
                }
            } else { die }
        }
        _diceList.value = updateDiceList
        savedState["diceList"] = updateDiceList
    }

    /**
     * Marks the given dice as paired/grouped for scoring.
     *
     * @param diceGroup - List of dice to mark
     */
    fun groupDices(diceGroup:List<Die>){
        val updateDiceList = _diceList.value.orEmpty().map { die ->
            if(diceGroup.any { it.id ==  die.id }) { die.copy(isPaired = true) }
            else { die }
        }
        _diceList.value = updateDiceList
        savedState["diceList"] = updateDiceList
    }

    /**
     * Unmarks the given dice from being paired.
     *
     * @param diceToReset - List of dice to unpair.
     */
    fun resetGroupDices(diceToReset:List<Die>){
        val updateDiceList = _diceList.value.orEmpty().map { die ->
            if(diceToReset.any { it.id == die.id }) { die.copy(isPaired = false) }
            else { die }
        }
        _diceList.value = updateDiceList
        savedState["diceList"] = updateDiceList
    }

    /**
     * Marks dice as paired if they exist in any group from the provided list.
     *
     * @param groupList - a list of diceGroups
     */
    fun syncGroupedDice(groupList:List<List<Die>>){
        val group = groupList.flatten().toSet()
        val updateDiceList = _diceList.value.orEmpty().map { die ->
            if(group.any {it.id == die.id}) { die.copy(isPaired = true) }
            else die
        }
        _diceList.value = updateDiceList
        savedState["diceList"] = updateDiceList
    }

    /**
     * Resets all dice by clearing their selected and paired states.
     */
    fun resetAllDice(){
        val clearedList = _diceList.value.orEmpty().map { die ->
            die.copy(isSelected = false, isPaired = false)
        }
        _diceList.value = clearedList
        savedState["diceList"] = clearedList
    }

    fun togglePairedState(targetDie: Die){
        val updateDiceList = _diceList.value.orEmpty().map { die ->
            if(die == targetDie){ die.copy(isPaired = !die.isPaired)}
            else { die }
        }
        _diceList.value = updateDiceList
        savedState["diceList"] = updateDiceList
    }
}