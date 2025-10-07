package com.algonquincollege.torunse


data class DataRepository (
    var Name : String = "", //compiler creates getName, setName because it's data class
    var Age : Int = 0
    ){

    //static region
   companion object{
       var theInstance = DataRepository() //static variable


   }

}

