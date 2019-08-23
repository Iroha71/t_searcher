package com.example.tapitapi

data class Store(var status:String,
                var url:String,
                var menu:ArrayList<Menu>)

data class Menu(var name:String,
                var price:Int)