package com.example.pokemon

import android.location.Location

class Pokemon {
    var name: String ?= null
    var description: String ?= null
    var img: Int ?= null
    var power: Float ?= null
    var location: Location ?= null
    var isCatch: Boolean ?= false

    constructor(name: String, des: String, img: Int, power: Float, lat: Double, long: Double){
        this.name = name
        this.description = des
        this.img = img
        this.power = power
        this.location = Location(name)
        this.location!!.latitude = lat
        this.location!!.longitude = long
    }
}