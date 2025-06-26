package se.umu.cs.ens20vck.lab1_thirtythrows

import android.util.Log

/**
 * Utility class, manages the scoring logic in the game.
 * Provides methods to:
 *  - Calculate the score for a single round.
 *  - Sum up the total scores from all rounds in a game.
 */
class ScoringManager {
    companion object {
        //TODO: Refactor can be made much shorter
        /**
         * Calculates the score for a single round. The scoring is calculated based
         * on the user's selected scoring option.
         * @param groupList - List containing all the dice to be scored.
         * @param choice - String, contains what scoring choice the user has selected.
         *
         * @return - The calculated score for a round
         */
        fun scoreRound(groupList: List<List<Die>>, choice: String):Int{
            Log.d("ScoreRound", "Choice: $choice list of dice ${groupList.size}")
            //Sets the choice to 3 if the user has selected the "LOW" option
            val selectedChoice = if(choice == "LOW"){
                3
            } else{
                choice.toIntOrNull()?:0
            }
            groupList.forEachIndexed { index, group ->
                Log.d("ScoreRound", "Group $index: ${group.map { it.value }}")
            }
            val score = when(selectedChoice){
                3 -> {
                    groupList.flatten()
                        .filter { it.value <= 3 }
                        .sumOf { it.value }
                }
                in 4..12 -> {
                    Log.d("ScoreRound", "In ...12")
                    groupList.flatten()
                        .sumOf { it.value }
                }
                //Fallback if all fails
                else -> 0
            }
            Log.d("ScoreRound", "Score: $score")
            return score
        }

        /**
         * Method that summarizes the scores from all rounds in a game
         * @param rounds - List containing all rounds in a singular game.
         *
         * @return - The total score for all the provided rounds
         */
        fun sumScores(rounds:List<Round>): Int{
            var totalScore = 0
            for(round in rounds){
                totalScore += round.score
            }
            return totalScore
        }
    }
}