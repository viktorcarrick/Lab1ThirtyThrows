package se.umu.cs.ens20vck.lab1_thirtythrows

import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//Helper class for the dice, creates initial dice list and rolls the dice
class DiceViewModel(context: Context) {
    private val _diceList = MutableLiveData<List<Die>>()
    val diceList: LiveData<List<Die>> = _diceList

    init {
        val initDiceList = mutableListOf<Die>()
        for(i in 1..6){
            val dice = Die(id = i,value = i)
            initDiceList.add(dice)
        }
        _diceList.value = initDiceList
    }


    fun rollAllDice(){
        val diceListCopy = _diceList.value.orEmpty()
        val updatedDiceList = diceListCopy.map { die ->
            die.copy(
                value = (1..6).random(),
                isSelected = false
            )
        }
        _diceList.value = updatedDiceList
    }

    //Rolls a selected die
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
    }

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
    }

    fun groupDices(diceGroup:List<Die>){
        val updateDiceList = _diceList.value.orEmpty().map { die ->
            if(diceGroup.any { it.id ==  die.id }) { die.copy(isPaired = true) }
            else { die }
        }
        _diceList.value = updateDiceList
    }

    fun resetGroupDices(diceToReset:List<Die>){
        val updateDiceList = _diceList.value.orEmpty().map { die ->
            if(diceToReset.any { it.id == die.id }) { die.copy(isPaired = false) }
            else { die }
        }
        _diceList.value = updateDiceList
    }

    fun syncGroupedDice(groupList:List<List<Die>>){
        val group = groupList.flatten().toSet()
        val updateDiceList = _diceList.value.orEmpty().map { die ->
            if(group.any {it.id == die.id}) { die.copy(isPaired = true) }
            else die
        }
        _diceList.value = updateDiceList
    }

    fun resetAllDice(){
        _diceList.value = _diceList.value.orEmpty().map { die ->
            die.copy(isSelected = false, isPaired = false)
        }
    }

    fun togglePairedState(targetDie:Die){
        val updateDiceList = _diceList.value.orEmpty().map { die ->
            if(die == targetDie){ die.copy(isPaired = !die.isPaired)}
            else { die }
        }
        _diceList.value = updateDiceList
    }
}