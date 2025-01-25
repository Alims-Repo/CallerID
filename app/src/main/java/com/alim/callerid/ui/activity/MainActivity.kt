package com.alim.callerid.ui.activity

import android.app.role.RoleManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alim.callerid.R
import com.alim.callerid.databinding.ActivityMainBinding
import com.alim.callerid.ui.viewmodel.ContactsViewModel
import com.alim.callerid.utils.PermissionUtils
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<ContactsViewModel>()

    private lateinit var binding: ActivityMainBinding

    private val callScreeningPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.e("Default Dialer", "Now set as default dialer")
        } else {
            Snackbar.make(
                binding.root,
                "You need to set this app as default Caller ID & Spam app to block spam calls",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Set") {
                checkSpamAppPermission()
            }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
        checkPermissionsAndInitiateServices()
        checkSpamAppPermission()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment
        binding.bottomNav.setupWithNavController(navHostFragment.navController)
    }

    private fun checkSpamAppPermission() {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
        if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING))
            callScreeningPermission.launch(
                roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            )
    }

    private fun checkPermissionsAndInitiateServices() {
        if (PermissionUtils.areAllPermissionsGranted(this)) {
            viewModel.loadAll()
        } else {
            PermissionUtils.requestPermissions(this) { isGranted ->
                if (isGranted) viewModel.loadAll()
                else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}