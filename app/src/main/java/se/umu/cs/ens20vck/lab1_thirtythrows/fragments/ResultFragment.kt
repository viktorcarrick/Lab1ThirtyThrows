package se.umu.cs.ens20vck.lab1_thirtythrows.fragments

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
import se.umu.cs.ens20vck.lab1_thirtythrows.R
import se.umu.cs.ens20vck.lab1_thirtythrows.dataModels.Round
import se.umu.cs.ens20vck.lab1_thirtythrows.managers.ScoringManager
import se.umu.cs.ens20vck.lab1_thirtythrows.viewModels.StorageViewModel

/**
 * Fragment class that displays the results from each game in
 * a table format, creates one table per game.
 *
 * @author Viktor Carrick (ens20vck@cs.umu.se)
 */
class ResultFragment : Fragment() {
    private val storageViewModel: StorageViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    /**
     * Creates and populates a table per game when the view is created.
     * Defines what background colors are used for the table rows and retrieves
     * game data from the storage ViewModel.
     *
     * @param view - the root view of the fragment.
     * @param savedInstanceState - saved state of the fragment (if available).
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tableContainer: LinearLayout = view.findViewById(R.id.tableContainer)
        val lightPurple = Color.parseColor("#E6E0F8")
        val lightGray = Color.parseColor("#E0E0E0")
        // Retrieve game data from storage
        val gameData = storageViewModel.getGames()
        for ((gameIndex, gameRounds) in gameData.withIndex()) {
            // Sets header for table
            val tableHeader = TextView(requireContext()).apply{
                text = getString(R.string.results_table_header,gameIndex + 1)
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

            setTableHeader(tableLayout,lightPurple)
            addRows(gameRounds, tableLayout, lightGray, lightPurple)
            // Add the sum row last
            addSumRow(gameRounds,tableLayout)
            tableContainer.addView(tableLayout)
        }


    }

    /**
     * Adds a header row to the given table with column titles
     * "Round", "Choice" and "Score".
     *
     * @param tableLayout - the table to which the header row will be added.
     * @param color - the background color for the header header row.
     */
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

    /**
     * Adds rows with game result data to the given table.
     * Each row alternates between two background colors.
     *
     * @param list - the list of Round objects representing a game's data.
     * @param tableLayout - the table to which the rows will be added.
     * @param color1 - background color for even-numbered rows.
     * @param color2 - background color for odd-numbered rows.
     */
    private fun addRows(list:List<Round>, tableLayout: TableLayout, color1: Int, color2: Int){
        for((roundIndex, round) in list.withIndex()){
            val row = TableRow(requireContext())
            val rowColor = if (roundIndex % 2 == 0) color1 else color2
            row.setBackgroundColor(rowColor)
            val cells = listOf(
                getString(R.string.results_round_text, roundIndex + 1),
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

    /**
     * Adds a row containing the total score for a game to the given table.
     *
     * @param tableLayout - the table that the row gets added to.
     */
    private fun addSumRow(list:List<Round>, tableLayout: TableLayout){
        val totScore = ScoringManager.sumScores(list)
        val sumRow = TableRow(requireContext())
        val sumCells = listOf(
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

}