package se.umu.cs.ens20vck.lab1_thirtythrows

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.gridlayout.widget.GridLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlayFragment : Fragment(R.layout.fragment_play) {
    private val storageViewModel: StorageViewModel by activityViewModels()
    private val diceViewModel: DiceViewModel by activityViewModels()
    //Stores selected choice
    private var choice: String? = null
    private lateinit var groupManager: DiceGroupManager
    private lateinit var groupList: List<List<Die>>
    //Counts the amount of throws per round.
    private var throwCounter = 0
    //Counts the total amount of throws
    private var totThrowCounter = 0
    //Flag to prevent spinner toast from appearing during init
    private var spinnerInit = true
    private val maxRounds = 10
    //Map of dice vectors, maps value to each image
    private val diceImages = mapOf(
        1 to R.drawable.die_1,
        2 to R.drawable.die_2,
        3 to R.drawable.die_3,
        4 to R.drawable.die_4,
        5 to R.drawable.die_5,
        6 to R.drawable.die_6,
    )
    private val selectedDiceImages = mapOf(
        1 to R.drawable.red_die_1,
        2 to R.drawable.red_die_2,
        3 to R.drawable.red_die_3,
        4 to R.drawable.red_die_4,
        5 to R.drawable.red_die_5,
        6 to R.drawable.red_die_6,
    )

    private val pairedDiceImages = mapOf(
        1 to R.drawable.gray_die_1,
        2 to R.drawable.gray_die_2,
        3 to R.drawable.gray_die_3,
        4 to R.drawable.gray_die_4,
        5 to R.drawable.gray_die_5,
        6 to R.drawable.gray_die_6,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private fun clearGamesStates(view:View){
        //Resets storage
        storageViewModel.clearRounds()
        storageViewModel.resetRoundCounter()
        storageViewModel.clearChoices()

        throwCounter = 0
        totThrowCounter = 0
        choice = null
        diceViewModel.resetAllDice()
        setRoundText(view)
        setThrowText(view)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupManager = DiceGroupManager()
        setupObservers(view)
        setupUI(view)
    }

    private fun setupUI(view:View){
        initOverlay(view)
        setRoundText(view)
        setThrowText(view)
        setupSpinner(view)
        setUpButtonListener(view)
        setupFloatingActionButton(view)
    }

    /**
     * Sets up observers for the LiveData objects in the app.
     * Observers two ViewModels: storageViewModel and diceViewModel.
     *
     * @param view - the root view used to access the UI.
     */
    private fun setupObservers(view:View){
        //Observe the round counter to handle progression and game reset logic.
        storageViewModel.roundCounter.observe(viewLifecycleOwner) {
                roundCount ->
            // Disable the action button between rounds
            val actionButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
            actionButton.isEnabled = false
            groupManager.clearGroups()
            if(roundCount > maxRounds){
                //Retrieves all rounds for the game
                val allRounds = storageViewModel.getRounds()
                storageViewModel.addGame(allRounds)
                //Resets the game and ui states
                clearGamesStates(view)
            }
        }
        // Observe changes in the dice list and re-render the dice grid when needed.
        diceViewModel.diceList.observe(viewLifecycleOwner){ diceList ->
            addDicesToGrid(view, diceList)
        }
    }

    /**
     * Initializes the game's start overlay.
     * Hides the overlay and triggers the first dice roll when the start button is pressed.
     *
     * @param view The root view used to access UI elements.
     */
    private fun initOverlay(view:View){
        val overlay:FrameLayout = view.findViewById(R.id.gameOverlay)
        val startButton: Button =  view.findViewById(R.id.startGameButton)

        startButton.setOnClickListener {
            overlay.visibility = View.GONE
            diceViewModel.rollAllDice()

        }
    }

    /**
     * Sets up the listener for the floating action button used to
     * lock in a group of dice. Handles:
     * - Collecting paired dice that have not been paired yet.
     * - Validating the group of dice based on the selected scoring choice.
     */
    private fun setupFloatingActionButton(view:View){
        val fButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
        fButton.setOnClickListener{
            val pairedDices = mutableListOf<Die>()
            val prevGroupedDices: Set<Die> = groupManager.getGroups().flatten().toSet()
            val diceList = diceViewModel.diceList.value ?: emptyList()
            // Collect all newly paired dice
            for(die in diceList){
                if(die.isPaired && die !in prevGroupedDices){
                    pairedDices.add(die)
                }
            }
            val groupSum = pairedDices.sumOf { it.value }
            // Sets the score limit
            val scoreLimit = when(choice) {
                "LOW" -> 3
                else -> choice?.toInt() ?: 0
            }
            // Sets a boolean used to check whether a selected group is valid or not
            val isValidGroup = when(choice) {
                // "LOW" allows all groups smaller or equal to three
                "LOW" -> pairedDices.all{ it.value <= 3 }
                else -> groupSum == scoreLimit
            }
            // Exits the listener and displays an error-toast if the group is not valid
            if(!isValidGroup){
                Toast.makeText(requireContext(), "Illegal grouping!", Toast.LENGTH_SHORT).show()
                diceViewModel.resetGroupDices(pairedDices)
                return@setOnClickListener
            }
            // Add group and update dice states
            groupManager.addGroup(pairedDices.toList())
            diceViewModel.groupDices(pairedDices)
            val diceGroups = groupManager.getGroups()
            diceViewModel.syncGroupedDice(diceGroups)
            pairedDices.clear()
        }
        fButton.isEnabled = false
    }

    //Populates the spinner with the different choices. Does not populate it with already selected choices
    //Creates an arrayAdapter to populate
    private fun setupSpinner(view: View){
       val spinner: Spinner = view.findViewById(R.id.spinner)
       val allChoices = resources.getStringArray(R.array.choice_array).toList()
       val usedChoices = storageViewModel.getUsedChoices()
       val availableChoices = allChoices.filterNot { usedChoices.contains(it) }
       val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, availableChoices)
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
       spinner.adapter = adapter
       val button: Button = view.findViewById(R.id.button3)
       val actionButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
       setupSpinnerListener(spinner,view,button, actionButton)
    }

    //Adds listener to spinner
    //Disable the spinner until the third throw has been made, then send the score after selecting option from spinner
    private fun setupSpinnerListener(spinner: Spinner, view: View, button: Button, actionButton: FloatingActionButton){
       spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
           override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
               //Prevents the toast from being shown on launch
               if(spinnerInit){
                   spinnerInit = false
                   return
               }
               if(throwCounter == 2){
                   choice = parent.getItemAtPosition(pos) as String
                   Toast.makeText(requireContext(), "Group dices", Toast.LENGTH_SHORT).show()
                   //Enables the button
                   button.isEnabled = true
                   actionButton.isEnabled = true

               }
           }

           override fun onNothingSelected(parent: AdapterView<*>) {
               button.isEnabled = false
               actionButton.isEnabled = false
           }
       }
    }

    //Adds listener to button
    //The user should be able to do three throws, before the round is created -> track button clicks?
    private fun setUpButtonListener(view:View){
       val button: Button = view.findViewById(R.id.button3)
       val spinner: Spinner = view.findViewById(R.id.spinner)
       //Initial state before round
       spinner.alpha = 0.5f
       spinner.isEnabled = false
       button.isEnabled = true
       button.setOnClickListener {
           when {
               throwCounter < 2 -> {
                   throwDice(view, spinner, button)
               }
               //Scores the round
               throwCounter == 2 && choice != null -> {
                   scoreRound(view, spinner, button)
               }
           }
       }
    }

    //Adds dice to the fragment, vector is selected based on each dice-value in the given list.
    private fun addDicesToGrid(view:View, diceList:List<Die>){
       val grid: GridLayout = view.findViewById(R.id.diceGrid)
        grid.removeAllViews()
       diceList.forEachIndexed {index, die ->
           val imgView = ImageView(requireContext())
           //sets image resource based on what state is selected
           val resId = when {
               die.isPaired -> pairedDiceImages[die.value]?: R.drawable.gray_die_0
               die.isSelected -> selectedDiceImages[die.value]?: R.drawable.red_die_0
               else -> diceImages[die.value]?: R.drawable.die_0
           }
           imgView.setImageResource(resId)
           imgView.setOnClickListener {
               //toggle selected paired based on throwCounter
               diceViewModel.toggleDiceState(index, throwCounter)
           }
           grid.addView(imgView)
       }
    }


    private fun setRoundText(view:View){
       val roundText : TextView = view.findViewById(R.id.roundText)
       val current = storageViewModel.roundCounter.value ?: 0
       roundText.text = getString(R.string.round_text, current, throwCounter)
    }

    private fun setThrowText(view:View){
       val throwText : TextView = view.findViewById(R.id.throwText)
       throwText.text = getString(R.string.currThrows_text, totThrowCounter)
    }

    private fun throwDice(view:View, spinner: Spinner, button: Button){
        throwCounter++
        totThrowCounter++
        diceViewModel.rollSelectedDice()
        setRoundText(view)
        setThrowText(view)

        if(throwCounter == 2){
            spinner.alpha = 1.0f
            spinner.isEnabled = true
            button.isEnabled = false
            button.text = "Score Round"
            groupList = groupManager.getGroups()
            Toast.makeText(requireContext(), "Select a scoring option", Toast.LENGTH_SHORT).show()
            choice = spinner.selectedItem as String
        }
    }

    private fun scoreRound(view: View,spinner: Spinner,button: Button){
        //Group of dices to be scored
        val diceGroup = groupManager.getGroups()
        val score = ScoringManager.scoreRound(diceGroup,choice.toString())
        val round = Round(choice = choice.toString(), score = score, diceGroup) // Replace with real scoring
        storageViewModel.addRound(round)
        storageViewModel.incrementRoundCounter()
        storageViewModel.addChoice(choice.toString())
        throwCounter = 0
        choice = null
        diceViewModel.resetAllDice()
        //Rerolls all dice after each round
        diceViewModel.rollAllDice()
        spinner.alpha = 0.5f
        spinner.isEnabled = false
        button.text = "Throw"
        button.isEnabled = true

        setupSpinner(view)
        setRoundText(view)
        setThrowText(view)
    }
}