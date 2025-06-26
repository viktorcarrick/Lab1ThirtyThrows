package se.umu.cs.ens20vck.lab1_thirtythrows

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayout
import androidx.activity.viewModels

//TODO: Create two models or one, implement game and have some model for results.
//      This Activity provides both models, Play updates results Result reads from it
//      The model should be created here so its shared between the two fragments
class MainActivity : AppCompatActivity() {
    //Storage model, stores rounds, completed games etc
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

        //Attaches listener to the tab
        val tab = findViewById<TabLayout>(R.id.tabLayout)
        tab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            //Overrides methods, these will be changed later
            override fun onTabSelected(tab: TabLayout.Tab) {
                //Start the play fragment when Play is selected
                if(tab.text?.equals("Play") == true){
                    val playFragment = PlayFragment()
                    //Shows the fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, playFragment)
                        .commit()
                }
                //Start result fragment when View Results is selected
                else if(tab.text?.equals("View Results") == true) {
                    val resFragment = ResultFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, resFragment)
                        .commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Optional: Do something when a tab is unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Optional: Do something when a tab is reselected
            }
        })

        //Loads the play fragment on init, works as a startpage
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayFragment())
            .commit()
    }
}