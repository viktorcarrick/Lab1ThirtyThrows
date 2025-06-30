package se.umu.cs.ens20vck.lab1_thirtythrows

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayout
import androidx.activity.viewModels
import se.umu.cs.ens20vck.lab1_thirtythrows.fragments.PlayFragment
import se.umu.cs.ens20vck.lab1_thirtythrows.fragments.ResultFragment
import se.umu.cs.ens20vck.lab1_thirtythrows.viewModels.StorageViewModel

/**
 * Main activity, that hosts the Play and Result fragments,
 * manages the shared storage ViewModel and sets up the tab navigation
 * and UI.
 *
 * @author Viktor Carrick (ens20vck@cs.umu.se)
 */
class MainActivity : AppCompatActivity() {
    /**
     * Shared ViewModel, stores game data.
     * Scoped to this activity so that the fragments can share the same instance.
     */
    val storageViewModel: StorageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Adds the title text to the toolbar
        val mainText = "ThirtyThrows"
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = mainText

        // Sets up the tab navigation listeners
        setUpTab()

        //Loads the play fragment on init, works as a start page
        showPlayFragment()
    }

    /**
     * Sets up the TabLayout listener to switch between
     * the Play and Result fragments based on tab selection.
     */
    private fun setUpTab(){
        //Attaches listener to the tab
        val tab = findViewById<TabLayout>(R.id.tabLayout)
        tab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            //Overrides methods, these will be changed later
            override fun onTabSelected(tab: TabLayout.Tab) {
                //Start the play fragment when Play is selected
                if(tab.text?.equals("Play") == true){
                    showPlayFragment()
                }
                //Start result fragment when View Results is selected
                else if(tab.text?.equals("View Results") == true) {
                    showResultFragment()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * Replaces the fragment container with the Play fragment.
     */
    private fun showPlayFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayFragment())
            .commit()
    }


    /**
     * Replaces the fragment container with the Result fragment.
     */
    private fun showResultFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ResultFragment())
            .commit()
    }
}