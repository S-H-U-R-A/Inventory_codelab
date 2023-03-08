package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    //SE CONVIERTE EL FLOW A UN LIVEDATA Y RETORNA LA LISTA DE ITEMS DESDE LA DB
    val allItems: LiveData< List<Item> > = itemDao.getItems().asLiveData()

    //MÉTODO QUE RECUPERA UN ITEM POR EL ID
    fun retrieveItem( id: Int ): LiveData<Item>{
        return itemDao.getItem(id).asLiveData()
    }

    //MÉTODO QUE LLAMA AL DAO PARA INSERTAR EN LA BD
    private fun insertItem(item: Item){
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    //MÉTODO QUE RETORNA UN OBJETO Item
    private fun getNewItemEntry(
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item = Item(
        itemName = itemName,
        itemPrice = itemPrice.toDouble(),
        quantityInStock = itemCount.toInt()
    )

    //ESTE MÉTODO BTIENE UN OBJETO ITEM Y LLAMA EL MÉTODO DE INSERCIÓN
    fun addNewItem(
        itemName: String,
        itemPrice: String,
        itemCount: String
    ){
        val newItem = getNewItemEntry(
            itemName,
            itemPrice,
            itemCount
        )
        //SE LLAMA EL MÉTODO DE INSERTAR DEL DAO
        insertItem(newItem)

    }

    //ESTE MÉTODO VALIDA TRES CAMPOS STRING QUE NO ESTEN EN BLANCO
    //Y RETORNA UN BOOLEAN
    fun isEntryValid(
        itemName:   String,
        itemPrice:  String,
        itemCount:  String
    ): Boolean{
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()){
            return false
        }
        return true
    }

    //MÉTODOS PARA ACTUALIZAR LOS REGISTROS CUANDO SE VENDE UN ITEM
    private fun updateItem(item: Item){
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    fun sellItem(item: Item){
        if( isStockAvailable(item) ){
            //ACA LO QUE SE HACE ES HACER UNA COPIA DEL
            //OBJETO PASADO Y RESTARLE 1 ELEMENTO AL STOCK
            val newItem = item.copy(
                quantityInStock = item.quantityInStock - 1
            )
            updateItem(newItem)
        }
    }

    //SE VERIFICA QUE EL ITEM DISPONGA DE ARTICULOS PARA LA VENTA
    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }

    //MÉTODOS PARA ELIMINAR UN ITEM
    fun deleteItem(item: Item){
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    //MËTODOS PARA ACTUALIZAR UN ITEM EXISTENTE
    private fun getUpdateItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    fun updateItemExist(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updateItem: Item = getUpdateItemEntry(
            itemId,
            itemName,
            itemPrice,
            itemCount
        )

        updateItem(updateItem)
    }

}

//FÁBRICA DE INVENTORY VIEWMODEL
class InventoryViewModelFactory(private val itemDao: ItemDao) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        //SI modelClass ES DE TIPO InventoryViewModel RETORNAMOS LA INSTANCIA DEL VIEWMODEL
        if( modelClass.isAssignableFrom( InventoryViewModel::class.java ) ){

            @Suppress("UNCHECKED_CAST")
            return  InventoryViewModel(itemDao) as  T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}