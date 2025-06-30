package se.umu.cs.ens20vck.lab1_thirtythrows.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import se.umu.cs.ens20vck.lab1_thirtythrows.dataModels.Round

/**
 * ViewModel class that acts as a shared storage across the application.
 * Manages scoring choices, played rounds, complete games and round counting.
 *
 * @author Viktor Carrick (ens20vck@cs.umu.se)
 */
class StorageViewModel: ViewModel() {
    // Set to store previously selected scoring choices
    private val usedChoices = mutableSetOf<String>()

    // List to keep track of rounds played in the current game
    private val roundList = mutableListOf<Round>()

    // List of complete games (each game is a list of rounds)
    private val gameList = mutableListOf<List<Round>>()

    // Mutable live data to track the current round number
    private val _roundCounter = MutableLiveData(1)

    // Immutable public live data for observing the round number
    val roundCounter: LiveData<Int> get() = _roundCounter

    /**
     * @return - A copy of the list of used scoring choices
     */
    fun getUsedChoices(): List<String>{
        return usedChoices.toList()
    }

    /**
     * Adds a scoring choice to the set of used choices
     */
    fun addChoice(string: String){
        usedChoices.add(string)
    }

    /**
     * Clears all stored choices
     */
    fun clearChoices(){
        usedChoices.clear()
    }

    /**
     * @return - A copy of the list of rounds played in the current game.
     */
    fun getRounds(): List<Round>{
        return roundList.toList()
    }

    /**
     * Adds a round to the current game.
     */
    fun addRound(round: Round){
        roundList.add(round)
    }

    /**
     * @return - Returns a copy of the list of completed games.
     */
    fun getGames(): List<List<Round>>{
        return gameList.toList()
    }

    /**
     * Adds a completed game to the list of games.
     */
    fun addGame(list:List<Round>){
        gameList.add(list)
    }

    /**
     * Increments the current round counter by one.
     */
    fun incrementRoundCounter(){
        _roundCounter.value = (_roundCounter.value?:1) +1
    }

    /**
     * Resets the round counter to 1
     */
    fun resetRoundCounter(){
        _roundCounter.value = 1
    }

    /**
     * Clears all rounds from the current game.
     */
    fun clearRounds(){
        roundList.clear()
    }
}