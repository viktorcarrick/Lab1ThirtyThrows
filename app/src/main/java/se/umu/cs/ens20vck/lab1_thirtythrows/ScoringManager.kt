package se.umu.cs.ens20vck.lab1_thirtythrows

/**
 * Utility class, manages the scoring logic in the game.
 * Provides methods to:
 *  - Calculate the score for a single round.
 *  - Sum up the total scores from all rounds in a game.
 *
 *  @author Viktor Carrick (ens20vck@cs.umu.se)
 */
class ScoringManager {
    companion object {
        /**
         * Calculates the score for a single round. The scoring is calculated based
         * on the user's selected scoring option.
         * @param groupList - List containing all the dice to be scored.
         * @param choice - String, contains what scoring choice the user has selected.
         *
         * @return - The calculated score for a round
         */
        fun scoreRound(groupList: List<List<Die>>, choice: String):Int{
            //Sets the choice to 3 if the user has selected the "LOW" option
            val selectedChoice = if(choice == "LOW"){
                3
            } else{
                choice.toIntOrNull()?:0
            }
            // Set the score based on the selected scoring choice
            val score = when(selectedChoice){
                3 -> {
                    groupList.flatten()
                        .filter { it.value <= 3 }
                        .sumOf { it.value }
                }
                in 4..12 -> {
                    groupList.flatten()
                        .sumOf { it.value }
                }
                //Fallback if all fails
                else -> 0
            }
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