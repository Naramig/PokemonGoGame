package com.radiant.pokemon

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var playerPower: Double = 0.0
    var location: Location? = null
    var ACCESSLOCATION = 123
    var listPokemons=ArrayList<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermmision()
        LoadPokemons()

    }

    fun checkPermmision(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }
        getUserLocation()
    }

    fun getUserLocation(){
        Toast.makeText(this, "User location access on", Toast.LENGTH_LONG).show()

        var myLocation = MyLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)
        var myThread = myThread()
        myThread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            ACCESSLOCATION->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else{
                    Toast.makeText(this, "We cannot access to your location", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    inner class  MyLocationListener:LocationListener{

        constructor(){
            location= Location("Start")
            location!!.longitude = 0.0
            location!!.latitude = 0.0
        }
        override fun onLocationChanged(loc: Location?) {
            location = loc
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("Not yet implemented")
        }

        override fun onProviderDisabled(provider: String?) {
            TODO("Not yet implemented")
        }

    }

    var userLocation: LatLng = LatLng(0.0, 0.0)

    inner class myThread:Thread{

        constructor():super(){

        }

        override  fun run(){
            //Wait until the phone gets user's location
            while(location!!.latitude == 0.0 && location!!.longitude == 0.0){}

            runOnUiThread() {
                setUserLocationToTheMap()
                setCameraToTheUserLocation()
            }
            while (true){
                try {
                    runOnUiThread {
                        mMap!!.clear()
                        setUserLocationToTheMap()
                        //show pokemons
                        setPokemonLocationToTheMap()
                    }
                    Thread.sleep(1000)
                }catch (e: Exception){
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        private fun setPokemonLocationToTheMap() {
            for (i in 0..listPokemons.size - 1) {
                var newPokemon = listPokemons[i]

                if (newPokemon.isCatch == false) {
                    val pokemon = LatLng(newPokemon.location!!.latitude, newPokemon.location!!.longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(pokemon)
                            .title(newPokemon.name)
                            .snippet(newPokemon.desc)
                            .icon(BitmapDescriptorFactory.fromResource(newPokemon.img!!))
                    )
                    if(location!!.distanceTo(newPokemon.location) < 2){
                        newPokemon.isCatch = true
                        listPokemons[i] = newPokemon
                        playerPower += newPokemon.power!!
                        Toast.makeText(applicationContext, "You catch new Pokemon. You power = " + playerPower.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        private fun setUserLocationToTheMap() {
            userLocation = LatLng(location!!.latitude, location!!.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(userLocation)
                    .title("Me")
                    .snippet("here is my location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
            )
        }
    }

    fun setCameraToTheUserLocation(){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 25f))
    }

    fun LoadPokemons(){
        listPokemons.add(Pokemon(R.drawable.charmander, "Charmander", "Test Pokemon", 5.0, 42.885021, 74.572400 ))
    }

    fun goToTheUserLocationButtonListener(view: View) {
        setCameraToTheUserLocation()
    }
}
