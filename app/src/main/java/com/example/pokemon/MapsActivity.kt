package com.example.pokemon

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var myLocation: Location?= null
    private var oldLocation: Location?= null
    var pokemons: ArrayList<Pokemon> = arrayListOf()
    var myPower: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        oldLocation = Location("me")
        oldLocation!!.latitude = 0.0
        oldLocation!!.longitude = 0.0
        pokemons.add(
            Pokemon("Fire Pokemon",
            "This Pokemon Breathes Fire", R.drawable.fire, 65.5f, 35.5, 25.2)
        )
        pokemons.add(
            Pokemon("Pecatcho",
                "This Pokemon Is Yellow Like a Banana", R.drawable.yellow, 25.5f, 22.5, 20.2)
        )
        pokemons.add(
            Pokemon("Green Pokemon",
                "This Pokemon Is Green And Have A Pretty Tail", R.drawable.green, 40.5f, 30.5, 10.2)
        )
        checkPermission()
    }

    val ACCESS_LOCATION = 123
    fun checkPermission(){

        if(Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_LOCATION)
                return
            }
        }

        getUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        when(requestCode){
            ACCESS_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else{
                    Toast.makeText(this, "Location access is denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(){
        Toast.makeText(this, "Location Access Now", Toast.LENGTH_SHORT).show()

        val myLocationListener = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 3f, myLocationListener)
        val myThread = MyThread()
        myThread.start()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    inner class MyLocationListener: LocationListener{

        constructor(){
            myLocation = oldLocation
        }
        override fun onLocationChanged(location: Location?) {
            myLocation = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    inner class MyThread() : Thread() {

        override fun run() {
            while (true){
                if (oldLocation != myLocation) {
                    oldLocation = myLocation
                    runOnUiThread() {
                        mMap.clear()
                        val me = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(me)
                                .title("Me")
                                .snippet("This is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(me))

                        for (pokemon in pokemons) {
                            if (pokemon.isCatch == false) {
                                val pos = LatLng(pokemon.location!!.latitude, pokemon.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                    .position(pos)
                                    .title(pokemon.name)
                                    .snippet(pokemon.description + ", Power: ${pokemon.power}")
                                    .icon(BitmapDescriptorFactory.fromResource(pokemon.img!!)))

                                if (pokemon.location!!.distanceTo(myLocation) <= 5f){
                                    pokemon.isCatch = true
                                    myPower += pokemon.power!!
                                    Toast.makeText(this@MapsActivity, "${pokemon.name} is caught", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                sleep(1000)

            }
        }
    }
}
