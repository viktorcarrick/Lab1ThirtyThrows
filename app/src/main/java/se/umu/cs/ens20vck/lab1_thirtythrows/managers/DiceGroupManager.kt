package se.umu.cs.ens20vck.lab1_thirtythrows.managers

import se.umu.cs.ens20vck.lab1_thirtythrows.dataModels.Die

/**
 * Model class, acts as a helper class for grouping selected dice.
 * Contains methods for creating and managing lists of grouped dice.
 *
 * @author Viktor Carrick (ens20vck@cs.umu.se)
 */
class DiceGroupManager {
    //List containing dice groups
    private val groups = mutableListOf<List<Die>>()

    /**
     * Method to add a group the the list, a group must
     * contain dice to be added to the list.
     *
     * @param group - A list of dice
     */
    fun addGroup(group: List<Die>){
        if(group.isNotEmpty()){
            groups.add(group)
        }
    }

    /**
     * Method to clear the list of groups.
     */
    fun clearGroups(){
        groups.clear()
    }

    /**
     * Method that returns a copy of the list of groups.
     * @return - a copy of the list of groups
     */
    fun getGroups(): List<List<Die>>{
        return groups.toList()
    }
}