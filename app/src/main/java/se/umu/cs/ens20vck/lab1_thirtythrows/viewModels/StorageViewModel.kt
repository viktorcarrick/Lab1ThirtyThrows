package se.umu.cs.ens20vck.lab1_thirtythrows.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import se.umu.cs.ens20vck.lab1_thirtythrows.dataModels.Round
import kotlin.math.round

/**
 * ViewModel class that acts as a shared storage across the application.
 * Manages scoring choices, played rounds, complete games and round counting.
 *
 * @author Viktor Carrick (ens20vck@cs.umu.se)
 */
class StorageViewModel(private val storedState: SavedStateHandle): ViewModel() {
    // Set to store previously selected scoring choices
    private val usedChoices = storedState.get<Set<String>>("usedChoices")?.toMutableSet() ?: mutableSetOf()

    // List to keep track of rounds played in the current game
    private val roundList = storedState.get<List<Round>>("roundList")?.toMutableList() ?: mutableListOf()

    // List of complete games (each game is a list of rounds)
    private val gameList = mutableListOf<List<Round>>()

    // Mutable live data to track the current round number
    private val _roundCounter = storedState.getLiveData("roundCounter",1)

    // Immutable public live data for observing the round number
    val roundCounter: LiveData<Int> get() = _roundCounter

    // Mutable live data flag to indicate that a game has been started
    private val _startFlag = storedState.getLiveData("startFlag",false)

    // Immutable public flag for observing the state
    val startFlag: LiveData<Boolean> get() = _startFlag
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
        storedState["usedChoices"] = usedChoices
    }

    /**
     * Clears all stored choices
     */
    fun clearChoices(){
        usedChoices.clear()
        storedState["usedChoices"] = usedChoices
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
        storedState["roundList"] = roundList
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
        val current = storedState["roundCounter"] ?: 1
        storedState["roundCounter"] = current + 1

    }

    /**
     * Resets the round counter to 1
     */
    fun resetRoundCounter(){
        storedState["roundCounter"] = 1
    }

    /**
     * Clears all rounds from the current game.
     */
    fun clearRounds(){
        roundList.clear()
        storedState["roundList"] = roundList
    }

    /**
     * Sets the flag to true on game start
     */
    fun startGame(){
        _startFlag.value = true
        storedState["startFlag"] = true
    }

    /**
     * Resets the flag when the game ends
     */
    fun endGame(){
        _startFlag.value = false
        storedState["startFlag"] = false
    }
}
