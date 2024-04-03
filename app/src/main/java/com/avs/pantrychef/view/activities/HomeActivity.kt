package com.avs.pantrychef.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.AuthController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.navigation.ui.onNavDestinationSelected
import com.avs.pantrychef.view.fragments.IngredientsListFragmentDirections

/**
 * Actividad principal de la aplicación cuando el usuario ha iniciado sesión
 *
 * @property authController Controlador de autenticación
 */
class HomeActivity: AppCompatActivity() {

    private lateinit var authController: AuthController

    /**
     * Método que se ejecuta al crear la actividad, se encarga de inicializar los componentes de la interfaz y manejar la navegación
     *
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        authController = AuthController()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                else -> {
                    item.onNavDestinationSelected(navController)
                }
            }
        }

        setupUserIconMenu()

        // Manejar el intent que se recibe al abrir la actividad
        handleIntent(intent)
    }

    /**
     * Método que permite insertar un nuevo contexto antes de que la actividad sea creada
     *
     * @param newBase Context?
     */
    override fun attachBaseContext(newBase: Context?) {
        val sharedPref = newBase?.getSharedPreferences("PantryChef", Context.MODE_PRIVATE)
        val languageCode = sharedPref?.getString("language", Locale.getDefault().language) ?: Locale.getDefault().language
        applyLocale(newBase, languageCode)

        super.attachBaseContext(newBase)
    }

    /**
     * Método que se encarga de cambiar el idioma de la aplicación en tiempo de ejecución
     *
     * @param context Context?
     * @param languageCode String
     */
    private fun applyLocale(context: Context?, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context?.resources?.updateConfiguration(config, context.resources.displayMetrics)
    }

    /**
     * Método que se encarga del manejo del menú de opciones del ícono de usuario
     * Se encarga de cerrar la sesión del usuario y cambiar el idioma de la aplicación
     *
     */
    private fun setupUserIconMenu() {
        val userIcon = findViewById<ImageView>(R.id.userIcon)
        userIcon.setOnClickListener {
            val popupMenu = PopupMenu(this, userIcon)
            popupMenu.menuInflater.inflate(R.menu.user_menu, popupMenu.menu)

            val languageMenuItem = popupMenu.menu.findItem(R.id.changeLanguage)

            // Cambiar el texto del item de cambio de idioma dependiendo del idioma actual
            if (Locale.getDefault().language.equals("es", ignoreCase = true)) {
                languageMenuItem.title = "Cambiar a Inglés"
            } else {
                languageMenuItem.title = "Change to Spanish"
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.logout -> {
                        authController.logOut()
                        val intent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.changeLanguage -> {
                        // Cambiar el idioma y reiniciar la actividad
                        if (Locale.getDefault().language.equals("es", ignoreCase = true)) {
                            setLocale("en")
                        } else {
                            setLocale("es")
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    /**
     * Método que se encarga de cambiar el idioma de la aplicación
     *
     * @param languageCode String
     * @param shouldRecreate Boolean
     */
    private fun setLocale(languageCode: String, shouldRecreate: Boolean = true) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = baseContext.resources
        val config = resources.configuration
        config.setLocale(locale)

        // Verificar si la versión de Android es mayor o igual a Nougat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            baseContext.createConfigurationContext(config)
        } else {
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        if (shouldRecreate) {
            recreate()
        }

        // Guardar el idioma seleccionado en las preferencias compartidas
        saveLanguageToPreferences(languageCode)
    }

    /**
     * Método que guarda el idioma seleccionado en las preferencias compartidas
     *
     * @param languageCode String
     */
    private fun saveLanguageToPreferences(languageCode: String) {
        val sharedPref = getSharedPreferences("PantryChef", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language", languageCode)
            apply()
        }
    }

    /**
     * Método que se encarga de manejar un nuevo intent recibido por la actividad
     *
     * @param intent Intent?
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("HomeActivity", "onNewIntent: Received new intent")
        setIntent(intent)
        handleIntent(intent)
    }

    /**
     * Método que se encarga de manejar el intent recibido por la actividad
     * Se encarga de navegar a la pantalla de detalle de un paso de una receta si le llega
     * un intent con información de una notificación de temporizador
     *
     * @param intent Intent?
     */
    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val recipeId = it.getStringExtra("notificationRecipeId") ?: ""
            val stepIndex = it.getIntExtra("notificationStepIndex", -1)
            Log.d("HomeActivity", "Handling intent - Recipe ID: $recipeId, Step Index: $stepIndex")
            if (recipeId.isNotEmpty() && stepIndex != -1) {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
                val navController = navHostFragment.navController

                val bundle = Bundle().apply {
                    putString("recipeId", recipeId)
                    putInt("stepIndex", stepIndex)
                }
                navController?.navigate(R.id.action_global_recipeStepFragment, bundle)
            }
        }
    }
}