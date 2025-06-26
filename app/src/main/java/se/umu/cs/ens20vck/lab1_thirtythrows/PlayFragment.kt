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


// TODO: Rename parameter arguments, choose names that match, Pass shared data from MainActivity
// TODO: Create some kind of start button, makes initial shuffle of dies.
// TODO: Refactor, tons of code in this fragment, some can probs be moved out or made into functions

// NOTE: Man slår alla tärningar första rundan, sedan får man välja. Gör något som inte tillåter en användare att välja ifall det är första rundan.
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayFragment : Fragment(R.layout.fragment_play) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val storageViewModel: StorageViewModel by activityViewModels()
    //Stores selected choice
    private var choice: String? = null
    //DiceHelper class and list to store dices
    private lateinit var diceViewModel: DiceViewModel
    private lateinit var groupManager: DiceGroupManager
    private lateinit var groupList: List<List<Die>>
    //Counts the amount of throws per round.
    private var throwCounter = 0
    //Counts the total amount of throws
    private var totThrowCounter = 0
    //Flag to prevent spinner toast from appearing during init
    private var spinnerInit = true

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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        //Observes the round counter
        storageViewModel.roundCounter.observe(viewLifecycleOwner) {
            roundCount ->
            val actionButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
            actionButton.isEnabled = false
            groupManager.clearGroups()
            //should be 10 here, 1 is temp for testing
            if(roundCount > 1){
                //Retrieves all rounds for the game
                val allRounds = storageViewModel.getRounds()
                storageViewModel.addGame(allRounds)
                //Resets the game and ui states
                clearGamesStates(view)
            }
        }
        diceViewModel = DiceViewModel(requireContext())
        diceViewModel.diceList.observe(viewLifecycleOwner){ diceList ->
            addDicesToGrid(view, diceList)
        }
        //TODO: Remove this, create listener for diceList since its livedata
        //diceList = diceViewModel.diceList
        groupManager = DiceGroupManager()
        initOverlay(view)
        setRoundText(view)
        setThrowText(view)
        setupSpinner(view)
        setUpButtonListener(view)
        setupFloatingActionButton(view)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //Inits the overlay, pressing the start button removes the overlay and rolls the dices
    private fun initOverlay(view:View){
        val overlay:FrameLayout = view.findViewById(R.id.gameOverlay)
        val startButton: Button =  view.findViewById(R.id.startGameButton)

        startButton.setOnClickListener {
            overlay.visibility = View.GONE
            diceViewModel.rollAllDice()

        }
    }
    private fun setupFloatingActionButton(view:View){
        val fButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
        fButton.setOnClickListener{
            //Iterate through diceList, find paired dice
            val pairedDices = mutableListOf<Die>()
            //Makes it searchable
            val prevGroupedDices: Set<Die> = groupManager.getGroups().flatten().toSet()
            Log.d("GroupDebug", "Grouped dice this round: ${prevGroupedDices.size}")
            val diceList = diceViewModel.diceList.value ?: emptyList()
            for(die in diceList){
                if(die.isPaired && die !in prevGroupedDices){
                    //Add to pair dice pair thingy
                    pairedDices.add(die)
                    //Reset the die state DO THIS IN VIEWMODEL
                    //die.isPaired = false
                    //diceViewModel.togglePairedState(die)
                    Log.d("PairedDice", "Found paired die: $die")

                }
            }
            val groupSum = pairedDices.sumOf { it.value }
            Log.d("ChoiceDebug", "Current choice: $choice")
            val scoreLimit = when(choice) {
                "LOW" -> 3
                else -> choice?.toInt() ?: 0
            }
            val isValidGroup = when(choice) {
                "LOW" -> pairedDices.all{ it.value <= 3 }
                else -> groupSum == scoreLimit
            }
            //Exits the listener if the user has submitted a group that is not allowed
            if(!isValidGroup){
                Log.d("PairedDice", "Not valid group! Value of group: $groupSum Score Limit: $scoreLimit")
                Toast.makeText(requireContext(), "Illegal grouping!", Toast.LENGTH_SHORT).show()
                diceViewModel.resetGroupDices(pairedDices)
                return@setOnClickListener
            }
            Log.d("PairedDice", "Group dice values: ${pairedDices.map { it.value }}")
            //Adds a copy to group manager, prevents it from being cleared
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
       //TODO: This works but can probably be made more understandable
       button.setOnClickListener {
           when {
               throwCounter < 2 -> {
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
                       Log.d("GroupLog", "Number of groups: ${groupList.size}")
                       Toast.makeText(requireContext(), "Select a scoring option", Toast.LENGTH_SHORT).show()
                       choice = spinner.selectedItem as String
                   }
               }
               //Scores the round
               throwCounter == 2 && choice != null -> {
                   //Group of dices to be scored
                   val diceGroup = groupManager.getGroups()
                   val score = ScoringManager.scoreRound(diceGroup,choice.toString())
                   Log.d("ScoreLog", "Score for round: $score")
                   val round = Round(choice = choice.toString(), score = score, diceGroup) // Replace with real scoring
                   storageViewModel.addRound(round)
                   storageViewModel.incrementRoundCounter()
                   storageViewModel.addChoice(choice.toString())
                   //TODO: Throw dices here also, runda 7 -> 18
                   // Reset
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
       }
    }

    //Adds dice to the fragment, vector is selected based on each dice-value in the given list.
    private fun addDicesToGrid(view:View, diceList:List<Die>){
       val grid: GridLayout = view.findViewById(R.id.diceGrid)
        grid.removeAllViews()
       //TODO: Rename i to something more readable
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
}