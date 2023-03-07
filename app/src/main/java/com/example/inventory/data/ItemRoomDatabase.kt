package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {

    abstract fun itemDao() : ItemDao

    companion object {

        //ESTA ANOTACIÓN AYUDA A QUE LA VARIABLE SIEMPRE ESTE ACTUALIZADA
        //PORQUE LE INDICA QUE TODAS LA S OPERACIONES SE HAGAN DESDE LA MEMORIA
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(context: Context): ItemRoomDatabase {

            //PUEDE SUCEDER QUE DESDE VARIOS SUBPROCESOS SE QUIERA ACCEDER A LA BASE DE DATOS
            //POR LO QUE SE PODRÍA GENERAR ERRORES O CREAR VARIAS BASES; PARA EVITAR ESTO
            //ES CONVENIENTE USAR synchronized QUE LO QUE HACE ES BLOQUEAR EL ACCESO PARA
            //QUE SOLO UN SUBPROCESO PUEDA ACCEDER A LA VEZ (EN COLA).
            return INSTANCE ?: synchronized(this){

                val instance: ItemRoomDatabase = Room.databaseBuilder(
                    context,
                    ItemRoomDatabase::class.java,
                    "item_database"
                )
                    //ACA LA ESTRATEGIA DE MIGRACION ES BORRAR TODA LA INFORMACIÓN
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }

        }

    }

}