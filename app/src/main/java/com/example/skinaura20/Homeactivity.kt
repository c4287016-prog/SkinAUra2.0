package com.example.skinaura20

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.navigation.NavigationView
import com.example.skinaura20.fragment.*
import com.google.firebase.auth.FirebaseAuth

class Homeactivity : AppCompatActivity() {

    private lateinit var scrollView: ScrollView
    private lateinit var fragmentContainer: View

    private lateinit var accountLayout: LinearLayout
    private lateinit var categoryLayout: LinearLayout
    private lateinit var homeLayout: LinearLayout
    private lateinit var premiumLayout: LinearLayout
    private lateinit var cartLayout: LinearLayout

    private lateinit var oilyCard: TextView
    private lateinit var dryCard: TextView
    private lateinit var sensitiveCard: TextView
    private lateinit var combinationCard: TextView

    private lateinit var btnCleanser: LinearLayout
    private lateinit var btnMoisturizer: LinearLayout
    private lateinit var btnSunscreen: LinearLayout
    private lateinit var btnSerum: LinearLayout

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeactivity)

        initViews()
        setupImageSlider()
        setupDrawer()
        setupClickListeners() // ⭐ setupSearch() yahan se hata diya gaya hai

        val directOpen = intent.getStringExtra("OPEN_FRAGMENT")
        if (directOpen != null) {
            handleDirectRedirection(directOpen)
        } else {
            showHomeScreen()
        }

        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
                showHomeScreen()
            } else {
                finish()
            }
        }
    }

    private fun handleDirectRedirection(skinType: String) {
        when (skinType.uppercase()) {
            "OILY SKIN" -> loadFragment(Oily())
            "DRY SKIN" -> loadFragment(Dry())
            "SENSITIVE SKIN" -> loadFragment(Sensitive())
            "COMBINATION SKIN" -> loadFragment(Combination())
            "NORMAL SKIN" -> loadFragment(Oily())
            else -> showHomeScreen()
        }
    }

    private fun setupImageSlider() {
        val slider: ImageSlider = findViewById(R.id.imageSlider)
        val slides = listOf(
            SlideModel(R.drawable.ff, null, ScaleTypes.FIT),
            SlideModel(R.drawable.fa, null, ScaleTypes.FIT),
            SlideModel(R.drawable.fb, null, ScaleTypes.FIT),
            SlideModel(R.drawable.fc, null, ScaleTypes.FIT)
        )
        slider.setImageList(slides)
        slider.startSliding(1500)
    }

    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)

        styleNavigationMenu(navView.menu)

        findViewById<ImageView>(R.id.menuIcon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> showHomeScreen()
                R.id.nav_oily -> loadFragment(Oily())
                R.id.nav_dry -> loadFragment(Dry())
                R.id.nav_sensitive -> loadFragment(Sensitive())
                R.id.nav_combination -> loadFragment(Combination())
                R.id.nav_logout -> logoutUser()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        scrollView = findViewById(R.id.homeScrollView)
        fragmentContainer = findViewById(R.id.fragmentContainer)

        accountLayout = findViewById(R.id.accountLayout)
        categoryLayout = findViewById(R.id.categoryLayout)
        homeLayout = findViewById(R.id.homeLayout)
        premiumLayout = findViewById(R.id.premiumLayout)
        cartLayout = findViewById(R.id.cartLayout)

        oilyCard = findViewById(R.id.cardoil)
        dryCard = findViewById(R.id.carddry)
        sensitiveCard = findViewById(R.id.cardsen)
        combinationCard = findViewById(R.id.cardcom)

        btnCleanser = findViewById(R.id.btnCleanser)
        btnMoisturizer = findViewById(R.id.btnMoisturizer)
        btnSunscreen = findViewById(R.id.btnSunscreen)
        btnSerum = findViewById(R.id.btnSerum)
    }

    private fun setupClickListeners() {
        homeLayout.setOnClickListener { showHomeScreen() }
        categoryLayout.setOnClickListener { loadFragment(Category()) }
        cartLayout.setOnClickListener { loadFragment(Cart()) }
        accountLayout.setOnClickListener { loadFragment(Account()) }
        premiumLayout.setOnClickListener { loadFragment(Premiuma()) }
        oilyCard.setOnClickListener { loadFragment(Oily()) }
        dryCard.setOnClickListener { loadFragment(Dry()) }
        sensitiveCard.setOnClickListener { loadFragment(Sensitive()) }
        combinationCard.setOnClickListener { loadFragment(Combination()) }

        btnCleanser.setOnClickListener { openProductTypeFragment("Cleanser") }
        btnMoisturizer.setOnClickListener { openProductTypeFragment("Moisturizer") }
        btnSunscreen.setOnClickListener { openProductTypeFragment("Sunscreen") }
        btnSerum.setOnClickListener { openProductTypeFragment("Serum") }
    }

    private fun openProductTypeFragment(type: String) {
        val fragment = ProductType()
        val bundle = Bundle()
        bundle.putString("pType", type)
        fragment.arguments = bundle
        loadFragment(fragment)
    }

    private fun styleNavigationMenu(menu: Menu) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            item.title = SpannableString(item.title).apply {
                setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
                setSpan(RelativeSizeSpan(1.1f), 0, length, 0)
                setSpan(ForegroundColorSpan(Color.BLACK), 0, length, 0)
            }
        }
    }

    fun showHomeScreen() {
        scrollView.visibility = View.VISIBLE
        fragmentContainer.visibility = View.GONE
    }

    private fun loadFragment(fragment: Fragment) {
        scrollView.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}