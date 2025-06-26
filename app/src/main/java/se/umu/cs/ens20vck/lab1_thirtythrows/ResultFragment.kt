package se.umu.cs.ens20vck.lab1_thirtythrows

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.activityViewModels

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//TODO: Add final row to table, should contain the total score for each game
class ResultFragment : Fragment() {
    //Data class
    private val storageViewModel: StorageViewModel by activityViewModels()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tableContainer: LinearLayout = view.findViewById(R.id.tableContainer)
        val lightPurple = Color.parseColor("#E6E0F8")
        val lightGray = Color.parseColor("#E0E0E0")
        //Retrieve game data
        val gameData = storageViewModel.getGames()
        for ((gameIndex, gameRounds) in gameData.withIndex()) {
            //Header for table
            val tableHeader = TextView(requireContext()).apply{
                //TODO: Move into string resource
                text = "Game ${gameIndex + 1}"
                setTypeface(typeface, Typeface.BOLD)
                textSize = 18f
                setPadding(16,32,16,16)
            }
            tableContainer.addView(tableHeader)

            //Create table / TableLayout
            val tableLayout = TableLayout(requireContext()).apply {
                layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )
                isStretchAllColumns = true
            }

            //Table header
            setTableHeader(tableLayout,lightPurple)
            //add rows
            addRows(gameRounds, tableLayout, lightGray, lightPurple)
            addSumRow(gameRounds,tableLayout)
            tableContainer.addView(tableLayout)
        }


    }

    private fun setTableHeader(tableLayout: TableLayout, color: Int){
        val tableHeaderRow = TableRow(requireContext())
        val headers = listOf("Round", "Choice", "Score")
        for(h in headers){
            val textView = TextView(requireContext()).apply{
                text = h
                setTypeface(typeface, Typeface.BOLD)
                setPadding(16,16,16,16)
            }
            tableHeaderRow.setBackgroundColor(color)
            tableHeaderRow.addView(textView)
        }
        tableLayout.addView(tableHeaderRow)
    }
    //Function that adds rows and fills them with game data
    private fun addRows(list:List<Round>, tableLayout: TableLayout, color1: Int, color2: Int){
        for((roundIndex, round) in list.withIndex()){
            val row = TableRow(requireContext())
            val rowColor = if (roundIndex % 2 == 0) color1 else color2
            row.setBackgroundColor(rowColor)
            val cells = listOf(
                "Round ${roundIndex + 1}",
                round.choice,
                round.score
            )
            for(cellText in cells){
                val textView = TextView(requireContext()).apply{
                    text = cellText.toString()
                    setPadding(16, 16, 16, 16)
                }
                row.addView(textView)
            }
            tableLayout.addView(row)
        }
    }


    private fun addSumRow(list:List<Round>, tableLayout: TableLayout){
        val totScore = ScoringManager.sumScores(list)
        var sumRow = TableRow(requireContext())
        var sumCells = listOf(
            "Total Score",
            "",
            totScore
        )

        for(cellText in sumCells){
            val textView = TextView(requireContext()).apply{
                text = cellText.toString()
                setPadding(16, 16, 16, 16)
                setTypeface(null, Typeface.BOLD)
            }
            sumRow.addView(textView)
        }
        tableLayout.addView(sumRow)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}