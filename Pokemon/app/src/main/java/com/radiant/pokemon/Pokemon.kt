package com.radiant.pokemon

import android.location.Location

class Pokemon {
    var name: String? = null
    var desc: String? = null
    var img: Int? = null
    var power: Double? = null
    var location: Location? = null
    var isCatch:Boolean? = null
    constructor(image: Int, name: String, desc: String, power: Double, lat: Double, log: Double){
        this.name = name
        this.desc = desc
        this.img = image
        this.power = power
        this.location = Location(name)
        this.location!!.latitude = lat
        this.location!!.longitude = log
        this.isCatch = false
    }
}