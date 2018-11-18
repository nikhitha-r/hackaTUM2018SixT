package com.chandruscm.hackatum

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.chandruscm.hackatum.fragments.TrunkKeyFragmentBottomSheetDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback
{
    private var topMargin = 0
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var uniqueUserID = ""
    private val SEARCH_REQUEST_CODE = 999
    private lateinit var currentLatLng: LatLng

    private val CLOSE_BY_LAT_1 = 48.252331
    private val CLOSE_BY_LONG_1 = 11.661961
    private val CLOSE_BY_LAT_2 = 48.251
    private val CLOSE_BY_LONG_2 = 11.65
    private val CLOSE_BY_LAT_3 = 48.250
    private val CLOSE_BY_LONG_3 = 11.66

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    fun init()
    {
        initTranslucencyPadding()
        getLocationPermission()
        initMap()
        initUserID()
    }

    fun initTranslucencyPadding()
    {
        ViewCompat.setOnApplyWindowInsetsListener(activity_main_layout) { v, insets ->
            topMargin = insets.systemWindowInsetTop
            (activity_main_search_tab.layoutParams as ViewGroup.MarginLayoutParams).topMargin = topMargin + resources.getDimensionPixelSize(R.dimen.spacing_normal)
            insets.consumeSystemWindowInsets()
        }
    }

    fun initMap()
    {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.sample_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun initUserID()
    {
        uniqueUserID = android.os.Build.BRAND + "_" + android.os.Build.PRODUCT
    }

    fun onSearchTabClick(view: View)
    {
        Log.i("MainActivity", "Search Tab clicked")
        startActivityForResult(Intent(this@MainActivity, SearchDestinationActivity::class.java), SEARCH_REQUEST_CODE)
    }

    fun getLocationPermission()
    {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            val permissions = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions( this, permissions, LocationService.LOCATION_PERMISSION );
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(googleMap: GoogleMap)
    {
        val locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(object : OnCompleteListener<Location> {
            override fun onComplete(task: Task<Location>)
            {
                if (task.isSuccessful)
                {
                    val location = task.getResult()!!
                    currentLatLng = LatLng(location.getLatitude(), location.getLongitude());
                    Log.i("MainActivity", "Current LatLng is $currentLatLng")
                    val update = CameraUpdateFactory.newLatLngZoom(currentLatLng, LocationService.DEFAULT_ZOOM);
                    databaseReference.child("Packages/package01/pkgStartLatitude").setValue(location.latitude)
                    databaseReference.child("Packages/package01/pkgStartLongitude").setValue(location.longitude)
                    googleMap.moveCamera(update);
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap)
    {
        map = googleMap
        map.setMyLocationEnabled(true);
        positionButton()
        getDeviceLocation(googleMap)
    }

    fun positionButton()
    {
        val mapView = findViewById<View>(R.id.sample_map)
        val locationButton= (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        rlp.setMargins(0,0,30,30);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == SEARCH_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                activity_main_destination_text.text = data?.getStringExtra("DESTINATION")
                databaseReference.child("Packages/package01/pkgEndLatitude").setValue(48.138082)
                databaseReference.child("Packages/package01/pkgEndLongitude").setValue(11.575462)
                databaseReference.child("Packages/package01/pkgSenderId").setValue(data?.getStringExtra("RECIPIENT"))

                addCarMarkers(map)
            }
        }
    }

    fun addCarMarkers(googleMap: GoogleMap)
    {
        val sixtMarker = BitmapDescriptorFactory.fromResource(R.drawable.sixtmarker2)
        googleMap.addMarker(MarkerOptions().position(LatLng(CLOSE_BY_LAT_1, CLOSE_BY_LONG_1)).title("BMW M5").icon(sixtMarker))
        googleMap.addMarker(MarkerOptions().position(LatLng(CLOSE_BY_LAT_2, CLOSE_BY_LONG_2)).title("BMW 7 Series").icon(sixtMarker))
        googleMap.addMarker(MarkerOptions().position(LatLng(CLOSE_BY_LAT_3, CLOSE_BY_LONG_3)).title("BMW 6 Series GT").icon(sixtMarker))

        googleMap.setOnMarkerClickListener {
            val bottomSheet = TrunkKeyFragmentBottomSheetDialog.newInstance()
            bottomSheet.show(supportFragmentManager, "TRUNK_FRAGMENT")
            true
        }

        val update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f);
        googleMap.moveCamera(update)
    }

}
